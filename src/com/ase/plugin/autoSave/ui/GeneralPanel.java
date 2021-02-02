package com.ase.plugin.autoSave.ui;


import com.ase.plugin.autoSave.model.Action;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static com.ase.plugin.autoSave.model.Action.*;

class GeneralPanel {

    private static final String TEXT_TITLE_ACTIONS = "General";

    private final Map<com.ase.plugin.autoSave.model.Action, JCheckBox> checkboxes;

    GeneralPanel(Map<Action, JCheckBox> checkboxes) {
        this.checkboxes = checkboxes;
    }

    JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_ACTIONS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(checkboxes.get(activate));
        panel.add(checkboxes.get(activateOnShortcut));
        panel.add(checkboxes.get(activateOnBatch));
        panel.add(checkboxes.get(noActionIfCompileErrors));
        panel.add(checkboxes.get(stringExtractor));
        panel.add(Box.createHorizontalGlue());
        panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
        return panel;
    }

}
