package com.ase.plugin.autoSave.component;


import com.ase.plugin.autoSave.model.Action;
import com.ase.plugin.autoSave.model.ExecutionMode;
import com.ase.plugin.autoSave.model.Storage;
import com.ase.plugin.autoSave.model.StorageFactory;
import com.ase.plugin.autoSave.processors.Processor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import java.util.*;
import java.util.stream.Stream;

import static com.ase.plugin.autoSave.model.Action.activate;
import static com.ase.plugin.autoSave.model.ExecutionMode.saveAll;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

public class SaveActionManager extends FileDocumentManagerAdapter {

    public static final Logger LOGGER = Logger.getInstance(SaveActionManager.class);

    private static SaveActionManager instance;

    public static SaveActionManager getInstance() {
        if (instance == null) {
            instance = new SaveActionManager();
        }
        return instance;
    }

    private final List<Processor> processors;
    private boolean running;
    private boolean javaAvailable;
    private final boolean compilingAvailable;
    private StorageFactory storageFactory;

    private SaveActionManager() {
        processors = new ArrayList<>();
        running = false;
        javaAvailable = false;
        compilingAvailable = initCompilingAvailable();
        storageFactory = StorageFactory.DEFAULT;
    }

    private boolean initCompilingAvailable() {
        try {
            return Class.forName("com.intellij.openapi.compiler.CompilerManager") != null;
        } catch (Exception e) {
            return false;
        }
    }

    void addProcessors(Stream<Processor> processors) {
        processors.forEach(this.processors::add);
        this.processors.sort(new Processor.OrderComparator());
    }

    void enableJava() {
        javaAvailable = true;
    }

    void setStorageFactory(StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
    }

    public boolean isCompilingAvailable() {
        return compilingAvailable;
    }

    public boolean isJavaAvailable() {
        return javaAvailable;
    }

    public Storage getStorage(Project project) {
        return storageFactory.getStorage(project);
    }

    @Override
    public void beforeAllDocumentsSaving() {
        System.out.println("~~~~[+] Start SaveActionManager#beforeAllDocumentsSaving");
        Document[] unsavedDocuments = FileDocumentManager.getInstance().getUnsavedDocuments();
        beforeDocumentsSaving(asList(unsavedDocuments));
        System.out.println("~~~~End SaveActionManager#beforeAllDocumentsSaving");
    }

    private void beforeDocumentsSaving(List<Document> documents) {
        System.out.println("~~~~Locating psi files for " + documents.size() + " documents: " + documents);

        Map<Project, Set<PsiFile>> projectPsiFiles = new HashMap<>();
        documents.forEach(document -> stream(ProjectManager.getInstance().getOpenProjects())
                .forEach(project -> ofNullable(PsiDocumentManager.getInstance(project).getPsiFile(document))
                        .map(psiFile -> {
                            Set<PsiFile> psiFiles = projectPsiFiles.getOrDefault(project, new HashSet<>());
                            projectPsiFiles.put(project, psiFiles);
                            return psiFiles.add(psiFile);
                        })));
        projectPsiFiles.forEach(((project, psiFiles) -> {
            guardedProcessPsiFiles(project, psiFiles, activate, saveAll);
        }));
    }

    public void guardedProcessPsiFiles(Project project, Set<PsiFile> psiFiles, Action activation, ExecutionMode mode) {
        if (ApplicationManager.getApplication().isDisposed()) {
            System.out.println("~~~~Application is closing, stopping invocation");
            return;
        }
        try {
            if (running) {
                System.out.println("~~~~Plugin already running, stopping invocation");
                return;
            }
            running = true;
            Engine engine = new Engine(getStorage(project), processors, project, psiFiles, activation, mode);
            engine.processPsiFilesIfNecessary();
        } finally {
            running = false;
        }
    }

}
