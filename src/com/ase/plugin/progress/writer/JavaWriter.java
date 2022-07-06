package com.ase.plugin.progress.writer;

import com.ase.plugin.autoSave.model.Storage;
import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.ui.FieldsDialog;
import com.ase.plugin.util.TextUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;

public class JavaWriter extends AbsWriter {

    private Project project;
    private String headFile;
    private String stringFormat;

    public JavaWriter(Project project) {
        this.project = project;
    }

    public void process(TaskHolder taskHolder) {
        Storage storage = ServiceManager.getService(project, Storage.class);
        stringFormat = storage.getStringFormat();
        headFile = storage.getHeadFile();
        System.out.println("~~~~stringFormat: " + stringFormat + " headFile: " + headFile);
        write(taskHolder);
    }

    private void write(TaskHolder taskHolder) {
        if (taskHolder.currentFile == null) {
            return;
        }
        VirtualFile file = taskHolder.currentFile;
        String content = readFileContent(file);

        // 添加 import
        if (!TextUtil.isEmpty(headFile)) {
            String[] list = headFile.split(";");
            if (list != null && list.length > 0) {
                for (String s : list) {
                    if (!content.contains(s)) {
                        content = content.replaceFirst("import",
                                "import " + s + ";\n" +
                                        "import");
                    }
                }
            }
        }

//        String extractTemplate = "AppProfile.getContext().getString($id)";
        for (FieldEntity field : taskHolder.selectedFields()) {
            String text = field.source;
            String replace = stringFormat.replace("$id", "R.string." + field.result);
            content = content.replace("\"" + text + "\"", replace);
        }

        writeFileContent(file, content);
    }
}
