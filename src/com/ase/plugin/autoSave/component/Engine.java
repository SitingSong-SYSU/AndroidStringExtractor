package com.ase.plugin.autoSave.component;


import com.ase.plugin.autoSave.model.Action;
import com.ase.plugin.autoSave.model.ExecutionMode;
import com.ase.plugin.autoSave.model.Storage;
import com.ase.plugin.autoSave.processors.Processor;
import com.ase.plugin.autoSave.processors.Result;
import com.ase.plugin.autoSave.processors.ResultCode;
import com.ase.plugin.autoSave.processors.SaveCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.PsiErrorElementUtil;

import java.util.AbstractMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.ase.plugin.autoSave.model.ExecutionMode.batch;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Implementation of the save action engine. This class will filter, process and log modifications to the files.
 * 保存动作引擎的实现。这个类将过滤、处理并记录文件的修改。
 */
class Engine {

    private static final String REGEX_STARTS_WITH_ANY_STRING = ".*?";

    private final Storage storage;
    private final List<Processor> processors;
    private final Project project;
    private final Set<PsiFile> psiFiles;
    private final Action activation;
    private final ExecutionMode mode;

    Engine(Storage storage,
           List<Processor> processors,
           Project project,
           Set<PsiFile> psiFiles,
           Action activation,
           ExecutionMode mode) {
        this.storage = storage;
        this.processors = processors;
        this.project = project;
        this.psiFiles = psiFiles;
        this.activation = activation;
        this.mode = mode;
    }

    void processPsiFilesIfNecessary() {
        if (!storage.isEnabled(activation)) {
            System.out.println("~~~~Plugin not activated on " + project);
            return;
        }
        System.out.println("~~~~Processing " + project + " files " + psiFiles + " mode " + mode);
        Set<PsiFile> psiFilesEligible = psiFiles.stream()
                .filter(psiFile -> isPsiFileEligible(project, psiFile))
                .collect(toSet());
        System.out.println("~~~~Valid files " + psiFilesEligible);
        processPsiFiles(project, psiFilesEligible, mode);
    }

    private void processPsiFiles(Project project, Set<PsiFile> psiFiles, ExecutionMode mode) {
        if (psiFiles.isEmpty()) {
            return;
        }
        System.out.println("~~~~Start processors (" + processors.size() + ")");
        List<SaveCommand> processorsEligible = processors.stream()
                .map(processor -> processor.getSaveCommand(project, psiFiles))
                .filter(command -> storage.isEnabled(command.getAction()))
                .filter(command -> command.getModes().contains(mode))
                .collect(toList());
        System.out.println("~~~~Filtered processors " + processorsEligible);
        List<AbstractMap.SimpleEntry<Action, Result<ResultCode>>> results = processorsEligible.stream()
                .peek(command -> System.out.println("~~~~Execute command " + command + " on " + psiFiles.size() + " files"))
                .map(command -> new AbstractMap.SimpleEntry<>(command.getAction(), command.execute()))
                .collect(toList());
        System.out.println("~~~~Exit engine with results "
                + results.stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(toList()));
    }

    private boolean isPsiFileEligible(Project project, PsiFile psiFile) {
        return psiFile != null
                && isProjectValid(project)
                && isPsiFileInProject(project, psiFile)
                && isPsiFileNoError(project, psiFile)
                && isPsiFileIncluded(psiFile)
                && isPsiFileFresh(psiFile)
                && isPsiFileValid(psiFile);
    }

    private boolean isProjectValid(Project project) {
        return project.isInitialized()
                && !project.isDisposed();
    }

    private boolean isPsiFileInProject(Project project, PsiFile psiFile) {
        boolean inProject = ProjectRootManager.getInstance(project)
                .getFileIndex().isInContent(psiFile.getVirtualFile());
        if (!inProject) {
            System.out.println("~~~~File " + psiFile + " not in current project " + project);
        }
        return inProject;
    }

    private boolean isPsiFileNoError(Project project, PsiFile psiFile) {
        if (storage.isEnabled(Action.noActionIfCompileErrors)) {
            boolean hasErrors = PsiErrorElementUtil.hasErrors(project, psiFile.getVirtualFile());
            if (hasErrors) {
                System.out.println("~~~~File " + psiFile + " has errors");
            }
            return !hasErrors;
        }
        return true;
    }

    private boolean isPsiFileIncluded(PsiFile psiFile) {
        String canonicalPath = psiFile.getVirtualFile().getCanonicalPath();
        return isIncludedAndNotExcluded(canonicalPath);
    }

    private boolean isPsiFileFresh(PsiFile psiFile) {
        if (mode == batch) {
            return true;
        }
        return psiFile.getModificationStamp() != 0;
    }

    private boolean isPsiFileValid(PsiFile psiFile) {
        return psiFile.isValid();
    }

    boolean isIncludedAndNotExcluded(String path) {
        return isIncluded(path) && !isExcluded(path);
    }

    private boolean isExcluded(String path) {
        Set<String> exclusions = storage.getExclusions();
        boolean psiFileExcluded = atLeastOneMatch(path, exclusions);
        if (psiFileExcluded) {
            System.out.println("~~~~File " + path + " excluded in " + exclusions);
        }
        return psiFileExcluded;
    }

    private boolean isIncluded(String path) {
        Set<String> inclusions = storage.getInclusions();
        if (inclusions.isEmpty()) {
            // If no inclusion are defined, all files are allowed
            return true;
        }
        boolean psiFileIncluded = atLeastOneMatch(path, inclusions);
        if (psiFileIncluded) {
            System.out.println("~~~~File " + path + " included in " + inclusions);
        }
        return psiFileIncluded;
    }

    private boolean atLeastOneMatch(String psiFileUrl, Set<String> patterns) {
        for (String pattern : patterns) {
            try {
                Matcher matcher = Pattern.compile(REGEX_STARTS_WITH_ANY_STRING + pattern).matcher(psiFileUrl);
                if (matcher.matches()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                // invalid patterns are ignored
                return false;
            }
        }
        return false;
    }

}
