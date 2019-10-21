package com.ase.plugin.progress.writer;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.ui.FieldsDialog;
import com.intellij.openapi.vfs.VirtualFile;

public class JavaWriter  extends AbsWriter {

    public void process(TaskHolder taskHolder) {
        write(taskHolder);
    }

    private void write(TaskHolder taskHolder) {
        if (taskHolder.currentFile == null) {
            return;
        }
        VirtualFile file = taskHolder.currentFile;
        String content = readFileContent(file);

        // 添加 import
        content = content.replaceFirst("import",
                "import im.yixin.app.AppProfile;\n" +
                "import");

        String extractTemplate = FieldsDialog.TEMPLATE;
        for (FieldEntity field : taskHolder.selectedFields()) {
            String text = field.source;
            String replace = extractTemplate.replace(FieldsDialog.ID, "R.string." + field.result);
            content = content.replace("\"" + text + "\"", replace);
        }

        writeFileContent(file, content);
    }
}
