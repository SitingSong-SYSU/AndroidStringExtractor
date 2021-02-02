package com.ase.plugin.autoSave.processors;

import com.ase.plugin.autoSave.model.ExecutionMode;
import com.ase.plugin.autoSave.model.Action;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.Set;
import java.util.function.BiFunction;

import static com.ase.plugin.autoSave.processors.ResultCode.OK;

/**
 * Implements a read action that returns a {@link Result}.
 * 实现一个read操作，该操作返回
 */
public class SaveReadCommand extends SaveCommand {

    public SaveReadCommand(Project project, Set<PsiFile> psiFiles, Set<ExecutionMode> modes, Action action,
                           BiFunction<Project, PsiFile[], Runnable> command) {
        super(project, psiFiles, modes, action, command);
    }

    @Override
    public Result<ResultCode> execute() {
        getCommand().apply(getProject(), getPsiFilesAsArray()).run();
        return new Result<>(OK);
    }

}
