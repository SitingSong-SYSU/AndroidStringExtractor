package com.ase.plugin.autoSave.inspection;

import com.ase.plugin.entity.TaskHolder;
import com.ase.plugin.finder.AbsFieldFinder;
import com.ase.plugin.finder.JavaFieldFinder;
import com.ase.plugin.finder.LayoutXmlFieldFinder;
import com.ase.plugin.progress.FileProcessor;
import com.ase.plugin.progress.PrefixProcessor;
import com.ase.plugin.progress.TextFormatProcessor;
import com.ase.plugin.progress.TranslateProcessor;
import com.ase.plugin.progress.writer.DataWriter;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class StringExtractorInspection extends BaseInspection {

    public StringExtractorInspection() {
        System.out.println("~~~~StringExtractorInspection: new");
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        System.out.println("~~~~StringExtractorInspection: getDisplayName");
        return "StringExtractorInspection";
    }

    @NotNull
    @Override
    protected String buildErrorString(Object... objects) {
        System.out.println("~~~~StringExtractorInspection: buildErrorString");
        if (objects != null) {
            return "StringExtractorInspection error: " + objects[0];
        } else {
            return "StringExtractorInspection error: ";
        }
    }

    public InspectionGadgetsFix buildFix(Object... infos) {
        System.out.println("~~~~StringExtractorInspection: buildFix");
        return infos.length == 1 && infos[0] instanceof PsiFile ? new StringExtractorFix((PsiFile)infos[0]) : null;
    }

    @Override
    public BaseInspectionVisitor buildVisitor() {
        System.out.println("~~~~StringExtractorInspection: buildVisitor");
        return new StringExtractorInspectionVisitor();
    }

    private static class StringExtractorInspectionVisitor extends BaseInspectionVisitor {
        private StringExtractorInspectionVisitor() {
            System.out.println("~~~~StringExtractorInspectionVisitor: new");
        }

        @Override
        public void visitFile(PsiFile file) {
            System.out.println("~~~~StringExtractorInspectionVisitor: visitFile " + file);
            System.out.println("~~~~StringExtractorInspectionVisitor: visitFile " + file.getParent());
            System.out.println("~~~~StringExtractorInspectionVisitor: visitFile " + file.getFileType().getName());
            super.visitFile(file);
            AbsFieldFinder fieldFinder;
            // 按模块分别执行替换
            if (file.getFileType() instanceof JavaFileType || file.getFileType().getName().contains("Kotlin")) {
                fieldFinder = new JavaFieldFinder();
            } else if (file.getFileType() instanceof XmlFileType && !file.getName().contains("strings")) {
                fieldFinder = new LayoutXmlFieldFinder();
            } else {
                return;
            }
            TaskHolder taskHolder = fieldFinder.find(file);
            System.out.println("~~~~visitFile: " + taskHolder.fields.size());
            if (taskHolder.fields.size() > 0) {
                this.registerError(file, file);
            }
        }
    }

    private static class StringExtractorFix extends InspectionGadgetsFix {
        private final PsiFile myPsiFile;

        StringExtractorFix(PsiFile psiFile) {
            System.out.println("~~~~StringExtractorFix: new");
            this.myPsiFile = psiFile;
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor problemDescriptor) {
            System.out.println("~~~~StringExtractorFix: doFix");
            PsiElement psiElement = problemDescriptor.getPsiElement();
            if (psiElement instanceof PsiFile) {
                PsiFile f = (PsiFile) psiElement;
                AbsFieldFinder fieldFinder;
                if (f.getFileType() instanceof JavaFileType || f.getFileType().getName().contains("Kotlin")) {
                    fieldFinder = new JavaFieldFinder();
                } else if (f.getFileType() instanceof XmlFileType) {
                    fieldFinder = new LayoutXmlFieldFinder();
                } else {
                    return;
                }
                TaskHolder taskHolder = fieldFinder.find(f);
                taskHolder.psiFile = f;
                FileProcessor.getInstance().process(project, f, taskHolder);
                TranslateProcessor.getInstance().process(taskHolder);
                TextFormatProcessor.getInstance().process(taskHolder);
                PrefixProcessor.getInstance().refreshDefaultPrefix(project, f, taskHolder);
                if (taskHolder.fields.size() > 0) {
                    System.out.println("~~~~ taskHolder name: " + taskHolder.currentFile.getName());
                    DataWriter dataWriter = new DataWriter(taskHolder.psiFile, project, taskHolder);
                    dataWriter.go();
                }
            }
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getFamilyName() {
            System.out.println("~~~~StringExtractorFix: getFamilyName");
            return "StringExtractorFix";
        }
    }
}
