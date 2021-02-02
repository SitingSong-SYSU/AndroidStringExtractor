package com.ase.plugin.progress.writer;

import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.ui.Toast;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

public class DataWriter extends WriteCommandAction.Simple {

    private PsiElementFactory factory;
    private Project project;
    private PsiFile file;
    private TaskHolder taskHolder;

    public DataWriter(PsiFile file, Project project, TaskHolder taskHolder) {
        super(project, file);
        factory = JavaPsiFacade.getElementFactory(project);
        this.file = file;
        this.project = project;
        this.taskHolder = taskHolder;
    }

    public void go() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Extract String") {

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                long currentTimeMillis = System.currentTimeMillis();
                execute();
                progressIndicator.setIndeterminate(false);
                progressIndicator.setFraction(1.0);
                Toast.make(project, MessageType.INFO, "String Extracted [" + (System.currentTimeMillis() - currentTimeMillis) + " ms]\n");
            }
        });
    }

    @NotNull
    @Override
    public RunResult execute() {
        return super.execute();
    }

    @Override
    protected void run() {

        if (file instanceof PsiJavaFile || file.getFileType().getName().contains("Kotlin")) {
            JavaWriter javaWriter = new JavaWriter(project);
            javaWriter.process(taskHolder);
        } else if (file instanceof XmlFile) {
            XmlWriter xmlWriter = new XmlWriter();
            xmlWriter.process(taskHolder);
        }

        StringsWriter stringsWriter = new StringsWriter(project);
        stringsWriter.process(taskHolder);


    }

}
