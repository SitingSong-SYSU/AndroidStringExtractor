package com.ase.plugin;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.finder.AbsFieldFinder;
import com.ase.plugin.finder.JavaFieldFinder;
import com.ase.plugin.finder.LayoutXmlFieldFinder;
import com.ase.plugin.progress.FileProcessor;
import com.ase.plugin.progress.PrefixProcessor;
import com.ase.plugin.progress.TextFormatProcessor;
import com.ase.plugin.progress.TranslateProcessor;
import com.ase.plugin.progress.writer.DataWriter;
import com.ase.plugin.ui.FieldsDialog;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.util.PsiUtilBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StringsExtractorAction extends BaseGenerateAction {

    private PsiElementFactory factory;
    private Project project;
    private Editor editor;
    private List<TaskHolder> taskHolders = new ArrayList<>();
    int cnt = 0;

    public StringsExtractorAction() {
        super(null);
    }

    protected StringsExtractorAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        editor = e.getData(PlatformDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile == null) {
            return;
        }
        PsiClass psiClass = getTargetClass(editor, psiFile);
        factory = JavaPsiFacade.getElementFactory(project);

//        AbsFieldFinder fieldFinder;
//        FileType fileType = psiFile.getFileType();
//        if (fileType instanceof XmlFileType) {
//            fieldFinder = new LayoutXmlFieldFinder();
//        } else if (fileType instanceof JavaFileType) {
//            fieldFinder = new JavaFieldFinder();
//        } else {
//            return;
//        }

//        ModuleManager instance = ModuleManager.getInstance(project);
//        Module[] modules = instance.getModules();

//        TaskHolder taskHolder = new TaskHolder();
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        PsiDirectory[] files = baseDir.getSubdirectories();
        for (PsiDirectory file : files) {
            findFile(file);
        }

        System.out.println("~~~ taskHolders size: " + taskHolders.size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(13); // 固定数量线程池
        for (TaskHolder taskHolder : taskHolders) {
            fixedThreadPool.execute(() -> {
                System.out.println("~~~ taskHolder name: " +taskHolder.currentFile.getName());
                DataWriter dataWriter = new DataWriter(taskHolder.psiFile, project, taskHolder);
                dataWriter.go();
            });
        }

//        FieldsDialog dialog = new FieldsDialog(factory, project, taskHolders.get(0), callback);
//        dialog.setSize(800, 500);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);

//        TaskHolder taskHolder = fieldFinder.find(psiFile);
//
//        FileProcessor.getInstance().process(project, psiFile, taskHolder);
//        TranslateProcessor.getInstance().process(taskHolder);
//        TextFormatProcessor.getInstance().process(taskHolder);
//        PrefixProcessor.getInstance().refreshDefaultPrefix(project, psiFile, taskHolder);
//
//        FieldsDialog dialog = new FieldsDialog(factory, psiClass, psiFile, project, taskHolder);
//        dialog.setSize(800, 500);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
    }

    private void findFile(PsiDirectory directory){
//        if (taskHolders.size() > 100) return;
        PsiFile[] files = directory.getFiles();
        for(PsiFile f : files){
            AbsFieldFinder fieldFinder;
            // 按模块分别执行替换
            if (f.getParent() != null && f.getParent().toString().contains("service")) {
                if (f.getFileType() instanceof JavaFileType) {
                    fieldFinder = new JavaFieldFinder();
                } else if (f.getFileType() instanceof XmlFileType) {
                    fieldFinder = new LayoutXmlFieldFinder();
                } else {
                    continue;
                }
            } else {
                continue;
            }
            TaskHolder taskHolder = fieldFinder.find(f);
            taskHolder.psiFile = f;
            FileProcessor.getInstance().process(project, f, taskHolder);
            TranslateProcessor.getInstance().process(taskHolder);
            TextFormatProcessor.getInstance().process(taskHolder);
            PrefixProcessor.getInstance().refreshDefaultPrefix(project, f, taskHolder);
            if (taskHolder.fields.size() > 0) {
                taskHolders.add(taskHolder);
            }
//            for (FieldEntity fieldEntity : taskHolder.fields) {
//                System.out.println("~~~" + taskHolder.currentFile + "~~~" + fieldEntity.source);
//            }
        }
        for (PsiDirectory file : directory.getSubdirectories())	{
            // 过滤build文件夹下内容
            if (file.getName().equals("build")) {
                continue;
            }
            findFile(file);
        }
    }

    public interface Callback {
        void run();
    }
}
