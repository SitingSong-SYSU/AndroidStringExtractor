package com.ase.plugin.progress.writer;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.intellij.openapi.vfs.VirtualFile;

public class XmlWriter extends AbsWriter {

    public void process(TaskHolder taskHolder) {
        if (taskHolder.currentFile == null) {
            return;
        }
        VirtualFile file =  taskHolder.currentFile;
        String content = readFileContent(file);
        for (FieldEntity field : taskHolder.selectedFields()) {
            String text = field.source;
            content = content.replace("android:text=\"" + text + "\"",
                    "android:text=\"@string/" + field.result + "\"");
        }
        writeFileContent(file, content);
    }
}