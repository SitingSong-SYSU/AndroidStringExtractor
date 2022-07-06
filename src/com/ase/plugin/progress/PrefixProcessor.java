package com.ase.plugin.progress;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.util.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.util.List;

public class PrefixProcessor {
    public static volatile PrefixProcessor instance = null;

    public static PrefixProcessor getInstance() {
        if (instance == null) {
            synchronized (PrefixProcessor.class) {
                if (instance == null) {
                    instance = new PrefixProcessor();
                }
            }
        }
        return instance;
    }

    public void refreshDefaultPrefix(Project project, PsiFile psiFile, TaskHolder taskHolder) {
        StringBuilder builder = new StringBuilder();
        VirtualFile virtualFile = psiFile.getVirtualFile();
        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
        String moduleName;
        if (module == null) {
            moduleName = "";
        } else {
            moduleName = module.getName().toLowerCase() + "_";
        }
        builder.append(moduleName);
        String name = virtualFile.getName().split("\\.")[0];
        String componentName = formatComponentName(name) + "_";
        builder.append(componentName);
        refreshPrefix(taskHolder, builder.toString());
    }

    private String formatComponentName(String name) {
        return StringUtils.underscoreString(name);
    }

    public void  refreshPrefix(TaskHolder taskHolder, String prefix) {
        taskHolder.prefix = prefix;
        List<FieldEntity> fields = taskHolder.fields;
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).result = fields.get(i).resultSrc;
        }
    }
}
