package com.ase.plugin.ui;

import com.ase.plugin.StringsExtractorAction;
import com.ase.plugin.common.CheckTreeTableManager;
import com.ase.plugin.common.FiledTreeTableModel;
import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.progress.PrefixProcessor;
import com.ase.plugin.progress.writer.DataWriter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.util.ArrayList;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class FieldsDialog extends JFrame {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel filedPanel;
    private JScrollPane sp;
    private PsiClass psiClass;
    private PsiElementFactory factory;
    private PsiFile file;
    private Project project;
    private JLabel generateClass;
    private JTextField textPrefix;
    private JTextField etTemplate;
    private JLabel labelTemplate;
    private JLabel labelExample;
    private ArrayList<DefaultMutableTreeTableNode> defaultMutableTreeTableNodeList;
    private StringsExtractorAction.Callback callback;

    private TaskHolder taskHolder;

    public static final String ID = "$id";
    public static String TEMPLATE = "AppProfile.getContext().getString($id)";


    public FieldsDialog(PsiElementFactory factory, Project project, TaskHolder taskHolder, StringsExtractorAction.Callback callback) {
        this.factory = factory;
        this.file = taskHolder.psiFile;
        this.project = project;
//        this.psiClass = psiClass;
        this.taskHolder = taskHolder;
        this.callback = callback;
        setTitle("String Extractor");
        getRootPane().setDefaultButton(buttonOK);
        this.setAlwaysOnTop(true);
        initListener();
    }

    private void initListener() {
        defaultMutableTreeTableNodeList = new ArrayList<>();

        JXTreeTable jxTreeTable = new JXTreeTable(new FiledTreeTableModel(createData()));
        CheckTreeTableManager manager = new CheckTreeTableManager(jxTreeTable);
        manager.getSelectionModel().addPathsByNodes(defaultMutableTreeTableNodeList);
        jxTreeTable.getColumnModel().getColumn(0).setPreferredWidth(100);


        jxTreeTable.expandAll();
        jxTreeTable.setCellSelectionEnabled(false);
        final DefaultListSelectionModel defaultListSelectionModel = new DefaultListSelectionModel();
        jxTreeTable.setSelectionModel(defaultListSelectionModel);

        DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField());
        defaultCellEditor.getComponent().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        jxTreeTable.getColumnModel().getColumn(1).setCellEditor(defaultCellEditor);

        defaultListSelectionModel.setSelectionMode(SINGLE_SELECTION);
        defaultListSelectionModel.addListSelectionListener(e -> defaultListSelectionModel.clearSelection());
        defaultMutableTreeTableNodeList = null;
        jxTreeTable.setRowHeight(30);
        sp.setViewportView(jxTreeTable);


        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setContentPane(contentPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        textPrefix.setText(taskHolder.prefix);
        textPrefix.addActionListener(e -> {
            if (e.getID() == ActionEvent.ACTION_PERFORMED) {
                String text = e.getActionCommand();
                PrefixProcessor.getInstance().refreshPrefix(taskHolder, text);
                jxTreeTable.updateUI();
            }
        });
        textPrefix.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChanged();
            }

            private void onChanged() {
                String text = textPrefix.getText();
                PrefixProcessor.getInstance().refreshPrefix(taskHolder, text);
                jxTreeTable.updateUI();
            }
        });

        labelTemplate.setVisible(taskHolder.isJavaFile());
        etTemplate.setVisible(taskHolder.isJavaFile());
        labelExample.setVisible(taskHolder.isJavaFile());

        etTemplate.setText(TEMPLATE);
        etTemplate.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChanged();
            }

            private void onChanged() {
                checkTemplate();
            }
        });
        checkTemplate();
    }

    private void checkTemplate() {
        String text = etTemplate.getText();
        TEMPLATE = text;
        if (!text.contains(ID)) {
            labelExample.setForeground(JBColor.RED);
            labelExample.setText("must contains " + ID);
            return;
        }
        String templateEg = text.replace(ID, "R.string.simple_text");
        labelExample.setForeground(JBColor.GRAY);
        labelExample.setText("eg: " + templateEg);
    }

    private void onOK() {
        this.setAlwaysOnTop(false);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            setVisible(false);
            DataWriter dataWriter = new DataWriter(file, project, taskHolder);
            dataWriter.go();
            callback.run();
        });
    }

    private void onCancel() {
        dispose();
    }

    private DefaultMutableTreeTableNode createData() {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
        createDataNode(root);
        return root;
    }

    private void createDataNode(DefaultMutableTreeTableNode root) {
        for (FieldEntity field : taskHolder.fields) {
            DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(field);
            root.add(node);
            defaultMutableTreeTableNodeList.add(node);
        }
    }

}