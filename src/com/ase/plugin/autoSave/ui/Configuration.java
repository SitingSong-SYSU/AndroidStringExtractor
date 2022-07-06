package com.ase.plugin.autoSave.ui;


import com.ase.plugin.autoSave.model.Action;
import com.ase.plugin.autoSave.model.Storage;
import com.ase.plugin.util.TextUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.apache.batik.bridge.TextUtilities;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
//import org.jf.util.TextUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static com.ase.plugin.autoSave.model.Action.*;

public class Configuration implements Configurable {

    public static final int BOX_LAYOUT_MAX_WIDTH = 3000;
    public static final int BOX_LAYOUT_MAX_HEIGHT = 100;

    private static final String TEXT_DISPLAY_NAME = "Save Actions";

    private final Storage storage;

    private final Set<String> exclusions = new HashSet<>();
    private final Set<String> inclusions = new HashSet<>();
    private final List<String> quickLists = new ArrayList<>();
    private final Map<com.ase.plugin.autoSave.model.Action, JCheckBox> checkboxes = new HashMap<>();
    private final ActionListener checkboxActionListener = this::updateCheckboxEnabled;
    private String headFile;
    private String stringFormat;
    private final DocumentListener headFileEditListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent event) {
            updateHeadFile(event);
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            updateHeadFile(event);
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
            updateHeadFile(event);
        }
    };
    private final DocumentListener stringFormatEditListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent event) {
            updateStringFormat(event);
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            updateStringFormat(event);
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
            updateStringFormat(event);
        }
    };

    private GeneralPanel generalPanel;
    private SettingPanel settingPanel;
//    private FormattingPanel formattingPanel;
//    private BuildPanel buildPanel;
//    private InspectionPanel inspectionPanel;
//    private FileMaskPanel fileMasksExclusionPanel;
//    private FileMaskPanel fileMasksInclusionPanel;
//    private IdeSupportPanel ideSupport;

    public Configuration(Project project) {
        storage = ServiceManager.getService(project, Storage.class);
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel panel = initComponent();
        initFirstLaunch();
        initActionListeners();
        return panel;
    }

    private void initFirstLaunch() {
        if (storage.isFirstLaunch()) {
            updateSelectedStateOfCheckboxes(com.ase.plugin.autoSave.model.Action.getDefaults());
            storage.stopFirstLaunch();
        }
    }

    private void initActionListeners() {
        for (Map.Entry<com.ase.plugin.autoSave.model.Action, JCheckBox> checkbox : checkboxes.entrySet()) {
            checkbox.getValue().addActionListener(checkboxActionListener);
        }
        settingPanel.getAddImport().getDocument().addDocumentListener(headFileEditListener);
        settingPanel.getReplaceRegex().getDocument().addDocumentListener(stringFormatEditListener);
    }

    @Override
    public boolean isModified() {
        for (Map.Entry<com.ase.plugin.autoSave.model.Action, JCheckBox> checkbox : checkboxes.entrySet()) {
            if (storage.isEnabled(checkbox.getKey()) != checkbox.getValue().isSelected()) {
                return true;
            }
        }
        if (storage.getConfigurationPath() != null) {
//                && !storage.getConfigurationPath().equals(ideSupport.getPath())) {
            return true;
        }

        return !storage.getExclusions().equals(exclusions)
                || !storage.getInclusions().equals(inclusions)
                || !storage.getQuickLists().equals(quickLists)
                || !TextUtil.equals(storage.getStringFormat(), stringFormat)
                || !TextUtil.equals(storage.getHeadFile(), headFile);
    }

    @Override
    public void apply() {
        for (Map.Entry<com.ase.plugin.autoSave.model.Action, JCheckBox> checkbox : checkboxes.entrySet()) {
            storage.setEnabled(checkbox.getKey(), checkbox.getValue().isSelected());
        }
        storage.setExclusions(new HashSet<>(exclusions));
        storage.setInclusions(new HashSet<>(inclusions));
        storage.setQuickLists(new ArrayList<>(quickLists));
        storage.setHeadFile(headFile);
        storage.setStringFormat(stringFormat);
//        storage.setConfigurationPath(ideSupport.getPath());
//        Storage efpStorage = EpfStorage.INSTANCE.getStorageOrDefault(ideSupport.getPath(), storage);
//        updateSelectedStateOfCheckboxes(efpStorage.getActions());
        updateCheckboxEnabled(null);
    }

    @Override
    public void reset() {
        updateSelectedStateOfCheckboxes(storage.getActions());
        updateCheckboxEnabled(null);
        updateExclusions();
        updateInclusions();
        updateQuickLists();
        updateSettings();
//        ideSupport.setPath(storage.getConfigurationPath());
    }

    private void updateSelectedStateOfCheckboxes(Set<com.ase.plugin.autoSave.model.Action> selectedActions) {
        for (Map.Entry<com.ase.plugin.autoSave.model.Action, JCheckBox> checkbox : checkboxes.entrySet()) {
            boolean isSelected = selectedActions.contains(checkbox.getKey());
            checkbox.getValue().setSelected(isSelected);
        }
    }

    @Override
    public void disposeUIResources() {
        checkboxes.clear();
        exclusions.clear();
        inclusions.clear();
        quickLists.clear();
        generalPanel = null;
        settingPanel = null;
//        formattingPanel = null;
//        buildPanel = null;
//        inspectionPanel = null;
//        fileMasksInclusionPanel = null;
//        fileMasksExclusionPanel = null;
//        ideSupport = null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return TEXT_DISPLAY_NAME;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    private JPanel initComponent() {
        for (com.ase.plugin.autoSave.model.Action action : com.ase.plugin.autoSave.model.Action.values()) {
            checkboxes.put(action, new JCheckBox(action.getText()));
        }
        stringFormat = storage.getStringFormat();
        headFile = storage.getHeadFile();

        generalPanel = new GeneralPanel(checkboxes);
        settingPanel = new SettingPanel(headFile, stringFormat);

//        formattingPanel = new FormattingPanel(checkboxes);
//        buildPanel = new BuildPanel(checkboxes, quickLists);
//        inspectionPanel = new InspectionPanel(checkboxes);
//        fileMasksInclusionPanel = new FileMaskInclusionPanel(inclusions);
//        fileMasksExclusionPanel = new FileMaskExclusionPanel(exclusions);
//        ideSupport = new IdeSupportPanel();
        return initRootPanel(
                generalPanel.getPanel(),
                settingPanel.getPanel()
//                formattingPanel.getPanel(),
//                buildPanel.getPanel(),
//                inspectionPanel.getPanel(),
//                fileMasksInclusionPanel.getPanel(),
//                fileMasksExclusionPanel.getPanel(),
//                ideSupport.getPanel(storage.getConfigurationPath())
        );
    }

    private JPanel initRootPanel(JPanel general, JPanel setting) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;

        c.gridy = 0;
        panel.add(general, c);
        c.gridy = 1;
        panel.add(setting, c);
        c.gridy = 2;
//        panel.add(build, c);
        c.gridy = 3;
//        panel.add(inspections, c);

        JPanel fileMaskPanel = new JPanel();
        fileMaskPanel.setLayout(new BoxLayout(fileMaskPanel, BoxLayout.LINE_AXIS));
//        fileMaskPanel.add(fileMasksInclusions);
        fileMaskPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//        fileMaskPanel.add(fileMasksExclusions);
        c.gridy = 4;
        panel.add(fileMaskPanel, c);

        c.gridy = 5;
//        panel.add(ideSupport, c);

        c.gridy = 6;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        panel.add(filler, c);

        return panel;
    }

    private void updateInclusions() {
        inclusions.clear();
        inclusions.addAll(storage.getInclusions());
//        fileMasksInclusionPanel.update(inclusions);
    }

    private void updateExclusions() {
        exclusions.clear();
        exclusions.addAll(storage.getExclusions());
//        fileMasksExclusionPanel.update(exclusions);
    }

    private void updateQuickLists() {
        quickLists.clear();
        quickLists.addAll(storage.getQuickLists());
//        buildPanel.update();
    }

    private void updateSettings() {
        stringFormat = storage.getStringFormat();
        headFile = storage.getHeadFile();
    }

    private void updateCheckboxEnabled(ActionEvent event) {
        updateCheckboxEnabledIfActiveSelected();
        updateCheckboxGroupExclusive(event, reformat, reformatChangedCode);
        updateCheckboxGroupExclusive(event, compile, reload);
        updateCheckboxGroupExclusive(event, unqualifiedStaticMemberAccess, customUnqualifiedStaticMemberAccess);
    }

    private void updateHeadFile(DocumentEvent event) {
        Document document = event.getDocument();
        if (document == settingPanel.getAddImport().getDocument()) {
            try {
                headFile = document.getText(0, document.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateStringFormat(DocumentEvent event) {
        Document document = event.getDocument();
        if (document == settingPanel.getReplaceRegex().getDocument()) {
            try {
                stringFormat = document.getText(0, document.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCheckboxEnabledIfActiveSelected() {
        for (Map.Entry<com.ase.plugin.autoSave.model.Action, JCheckBox> checkbox : checkboxes.entrySet()) {
            com.ase.plugin.autoSave.model.Action currentCheckBoxKey = checkbox.getKey();
            if (!activate.equals(currentCheckBoxKey)
                    && !activateOnShortcut.equals(currentCheckBoxKey)
                    && !activateOnBatch.equals(currentCheckBoxKey)) {
                checkbox.getValue().setEnabled(isActiveSelected());
            }
        }
    }

    private void updateCheckboxGroupExclusive(ActionEvent event, com.ase.plugin.autoSave.model.Action checkbox1, Action checkbox2) {
        if (event == null || !(event.getSource() instanceof JCheckBox)) {
            return;
        }
        JCheckBox thisCheckbox = (JCheckBox) event.getSource();
        if (thisCheckbox.isSelected()) {
            if (thisCheckbox == checkboxes.get(checkbox1)) {
                checkboxes.get(checkbox2).setSelected(false);
            } else if (thisCheckbox == checkboxes.get(checkbox2)) {
                checkboxes.get(checkbox1).setSelected(false);
            }
        }
    }

    private boolean isActiveSelected() {
        boolean activateIsSelected = checkboxes.get(activate).isSelected();
        boolean activateShortcutIsSelected = checkboxes.get(activateOnShortcut).isSelected();
        boolean activateBatchIsSelected = checkboxes.get(activateOnBatch).isSelected();
        return activateIsSelected || activateShortcutIsSelected || activateBatchIsSelected;
    }

}
