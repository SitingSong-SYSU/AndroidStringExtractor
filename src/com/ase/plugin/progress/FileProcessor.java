package com.ase.plugin.progress;

import com.ase.plugin.entity.TaskHolder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

public class FileProcessor {
    public static volatile FileProcessor instance = null;

    public static FileProcessor getInstance() {
        if (instance == null) {
            synchronized (FileProcessor.class) {
                if (instance == null) {
                    instance = new FileProcessor();
                }
            }
        }
        return instance;
    }

    // src/main/res/values/strings.xml
    private String defaultFilePath = "/src/main/res/values/strings.xml";

    public void process(Project project, PsiFile psiFile, TaskHolder taskHolder) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        taskHolder.currentFile = virtualFile;
        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
        if (module == null) {
            return;
        }
        VirtualFile moduleFile = module.getModuleFile();
        if (moduleFile == null) {
            return;
        }
        String moduleName = moduleFile.getParent().getName();
        VirtualFile desFile = moduleFile.getParent().getParent().getParent().getParent().findFileByRelativePath(moduleName + defaultFilePath);
        if (desFile == null) {
            return;
        }
        taskHolder.desFile = desFile;
    }
}
