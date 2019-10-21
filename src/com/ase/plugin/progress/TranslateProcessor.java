package com.ase.plugin.progress;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.translate.Translator;

import java.util.List;

public class TranslateProcessor {
    public static volatile TranslateProcessor instance = null;

    public static TranslateProcessor getInstance() {
        if (instance == null) {
            synchronized (TranslateProcessor.class) {
                if (instance == null) {
                    instance = new TranslateProcessor();
                }
            }
        }
        return instance;
    }

    public void process(TaskHolder taskHolder) {
        List<FieldEntity> fields = taskHolder.fields;
        for (int i = 0; i < fields.size(); i++) {
            String source = fields.get(i).source;
            String english = Translator.toEnglish(source);
//            String english = fields.get(i).source;
            fields.get(i).result = english;
            fields.get(i).resultSrc = english;
        }
    }
}
