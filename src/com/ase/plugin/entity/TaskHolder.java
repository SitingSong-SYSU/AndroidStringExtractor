package com.ase.plugin.entity;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

public class TaskHolder {
    public String prefix = "";
    public List<FieldEntity> fields = new ArrayList<FieldEntity>();
    public VirtualFile currentFile = null;
    public VirtualFile desFile = null;
    public String descTag = "SHORT_NAME";
    public PsiFile psiFile = null;

    public List<FieldEntity> selectedFields() {
        List<FieldEntity> fieldEntities = new ArrayList<>();
        for (FieldEntity f : fields) {
            if (f.isSelected) {
                fieldEntities.add(f);
            }
        }
        fields = fieldEntities;
        return fields;
    }

    public Boolean isJavaFile() {
        return currentFile.getFileType() instanceof JavaFileType;
    }
}
