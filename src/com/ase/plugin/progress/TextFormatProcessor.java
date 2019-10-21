package com.ase.plugin.progress;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;

import java.util.Arrays;
import java.util.List;

public class TextFormatProcessor {
    private List<String> simpleWords = Arrays.asList("a", "an", "the");
    public static volatile TextFormatProcessor instance = null;

    public static TextFormatProcessor getInstance() {
        if (instance == null) {
            synchronized (TextFormatProcessor.class) {
                if (instance == null) {
                    instance = new TextFormatProcessor();
                }
            }
        }
        return instance;
    }


    private String removeSimpleWord(String src) {
        String result = src;
        for (String simpleWord : simpleWords) {
            result = result.replace(" " + simpleWord + " ", " ");
        }
        return result;
    }

    private String cleanText(String src) {
//        String result = src.replace(',', '_');
//        result = result.replace('.', '_');
//        result = result.replace('!', '_');
//        result = result.replace('?', '_');
//        result = result.replace('-', '_');
//        result = result.replace('/', '_');
//        result = result.replace(' ', '_');
//        result = result.replace('&', '_');
//        result = result.replace('=', '_');
//        result = result.replace('\\', '_');
//        result = result.replace(':', '_');
//        result = result.replace('：', '_');
//        result = result.replace('！', '_');
//        result = result.replace('，', '_');
//        result = result.replace('。', '_');
//        result = result.replace('？', '_');
//        result = result.replace('@', '_');
//        result = result.replace('~', '_');
//        result = result.replace('～', '_');
//        result = result.replace('<', '_');
//        result = result.replace('>', '_');
//        result = result.replace('》', '_');
//        result = result.replace('《', '_');
//        result = result.replace('+', '_');
//        result = result.replace('“', '_');
//        result = result.replace('”', '_');
//        result = result.replace('[', '_');
//        result = result.replace(']', '_');
//        result = result.replace('(', '_');
//        result = result.replace(')', '_');
//        result = result.replace('、', '_');
//        result = result.replace('（', '_');
//        result = result.replace('）', '_');
//        result = result.replace('…', '_');
//        result = result.replace('·', '_');
//        result = result.replace(Regex(" +"), " ");
        String result = wordsEncode(src);
        result = result.trim();
        result = result.toLowerCase();
        return result;
    }

    public static String wordsEncode(String source) {
        if (source == null) {
            return "";
        }
        String res = "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (((c - '0') > 0x4E00 && (c - '0') < 0x9FA5)
                    || ((c - '0') >= 0 && (c - '0') <= 9)
                    || ((c - 'a') >= 0 && (c - 'a') <= 25)
                    || ((c - 'A') >= 0 && (c - 'A') <= 25)) {
                buffer.append(c);
            } else {
                buffer.append("_");
            }
        }
        res = buffer.toString();
        return res;
    }

    private String concat(String src) {
        return src.replace(' ', '_');
    }


    public String processText(String src) {
        String removeSimpleWord = removeSimpleWord(src);
        String cleanText = cleanText(removeSimpleWord);
        return concat(cleanText);
    }


    public void process(TaskHolder taskHolder) {
        List<FieldEntity> fields = taskHolder.selectedFields();
        for (int i = 0; i < fields.size(); i++) {
            String src = fields.get(i).result;
            String processText = processText(src);
            fields.get(i).result = processText;
            fields.get(i).resultSrc = processText;
        }
    }
}
