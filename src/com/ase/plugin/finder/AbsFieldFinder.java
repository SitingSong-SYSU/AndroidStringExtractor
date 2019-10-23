package com.ase.plugin.finder;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import java.util.regex.Pattern;


public abstract class AbsFieldFinder {

    public TaskHolder find(PsiFile psiFile) {
        FileDocumentManager.getInstance().saveAllDocuments();

        TaskHolder taskHolder = new TaskHolder();

        if (psiFile.getVirtualFile() == null) {
            return taskHolder;
        }
        try {
            InputStream inputStream = psiFile.getVirtualFile().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                stringBuilder.append(line).append("\n");
            }

            reader.close();
            Pattern regex = Pattern.compile(regex());
            Matcher result = regex.matcher(stringBuilder);

            List<FieldEntity> fieldEntities = new ArrayList<>();
            while (result.find()) {
                String s = transformToString(result.group());
//                if (isDefaultChecked(s) && isChinese(s)) {
                if (s != null && s.trim().length() > 0) {
                    fieldEntities.add(new FieldEntity(s, "", true));
                }
            }
            taskHolder.fields = fieldEntities;

            return taskHolder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskHolder;
    }

    // 判断一个字符是否是中文
    private Boolean isChinese(char c) {
        return (c - '0') > 0x4E00 && (c - '0') < 0x9FA5; // 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    private Boolean isChinese(String str)  {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    abstract Boolean isDefaultChecked(String it);

    abstract protected String transformToString(String it);

    abstract protected String regex();
}
