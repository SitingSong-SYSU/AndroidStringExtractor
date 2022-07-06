package com.ase.plugin.autoSave.processors;

import com.ase.plugin.autoSave.model.ExecutionMode;
import com.ase.plugin.autoSave.model.Action;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.BiFunction;

import static com.ase.plugin.autoSave.processors.ResultCode.OK;

/**
 * Implements a write action that encapsulates {@link com.intellij.openapi.command.WriteCommandAction} that returns
 * a {@link Result}.
 * 实现一个写操作
 */
public class SaveWriteCommand extends SaveCommand {

    public SaveWriteCommand(Project project, Set<PsiFile> psiFiles, Set<ExecutionMode> modes, Action action,
                            BiFunction<Project, PsiFile[], Runnable> command) {
        super(project, psiFiles, modes, action, command);
    }

    @Override
    public Result<ResultCode> execute() {
        RunResult<ResultCode> runResult = new WriteCommandAction<ResultCode>(getProject(), getPsiFilesAsArray()) {
            @Override
            protected void run(com.intellij.openapi.application.Result<? super ResultCode> result) throws Throwable {
                getCommand().apply(getProject(), getPsiFilesAsArray()).run();
                result.setResult(OK);
            }
        }.execute();
        return new Result<>(runResult);
    }

}
