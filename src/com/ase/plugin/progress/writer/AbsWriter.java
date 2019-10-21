package com.ase.plugin.progress.writer;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;

public class AbsWriter {

    protected void saveAllFile() {
        FileDocumentManager.getInstance().saveAllDocuments();
    }


    protected void writeFileContent(VirtualFile file, String content) {
        try {
            OutputStream outputStream = file.getOutputStream(this);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String readFileContent(VirtualFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line).append("\n");
            }
            String content = builder.toString();
            reader.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
