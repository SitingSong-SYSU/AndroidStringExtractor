package com.ase.plugin.autoSave.ui;

import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;
import java.awt.*;

public class SettingPanel {

    private static final String TEXT_TITLE_ACTIONS = "Setting";

    private JPanel panel;
    private JTextField replaceRegex;
    private JTextField addImport;

    SettingPanel(String headFile, String stringFormat) {
        System.out.println("~~~~SettingPanel: " + headFile + " : " + stringFormat);
        panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_ACTIONS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JLabel addImportHint = new JLabel("添加必要的头文件: ");
        addImport = new JTextField(headFile);
        panel.add(addImportHint);
        panel.add(addImport);

        JLabel replaceRegexHint = new JLabel("替换字符串格式，使用$id代替原字符串: ");
        replaceRegex = new JTextField(stringFormat);
        panel.add(replaceRegexHint);
        panel.add(replaceRegex);

        panel.add(Box.createHorizontalGlue());
        panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
    }

    JPanel getPanel() {
        return panel;
    }

    JTextField getReplaceRegex() {
        return replaceRegex;
    }

    public JTextField getAddImport() {
        return addImport;
    }
}
