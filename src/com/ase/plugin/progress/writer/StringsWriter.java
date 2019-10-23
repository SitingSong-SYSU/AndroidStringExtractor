package com.ase.plugin.progress.writer;

import com.ase.plugin.entity.FieldEntity;
import com.ase.plugin.entity.TaskHolder;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlComment;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.*;

public class StringsWriter extends AbsWriter {
    private Project project;

    public StringsWriter (Project project) {
        this.project = project;
    }

    private XmlFile openStringsFile(TaskHolder taskHolder) {
        if (taskHolder.desFile == null) {
            return null;
        }
        VirtualFile virtualFile = taskHolder.desFile;
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile instanceof XmlFile) {
            return (XmlFile)psiFile;
        } else {
            return null;
        }
    }

    private void writeComment(XmlTag rootTag, String text) {
        XmlElementFactory factory = XmlElementFactory.getInstance(project);
        XmlTag container = factory.createTagFromText("<comment><!-- " + text + " --></comment>", XMLLanguage.INSTANCE);
        PsiElement xmlComment = PsiTreeUtil.getChildOfType(container, XmlComment.class);
        if (xmlComment == null) {
            return;
        }
        rootTag.add(xmlComment);
    }

    private void writeContent(XmlTag rootTag, TaskHolder taskHolder) {
        List<FieldEntity> fields = taskHolder.selectedFields();

//        // 去重
//        Set<FieldEntity> set = new HashSet<>();
//        for (FieldEntity field : fields) {
//            set.add(field);
//        }
//        for (FieldEntity field : set) {
//            // 特殊字符转义
//            String attribute = htmlEncode(field.source);
//            System.out.println("~~~attribute: " + attribute);
//            XmlTag childTag = rootTag.createChildTag("string", "", attribute, false);
//
//            childTag.setAttribute("name", field.result);
//            rootTag.add(childTag);
//        }


        for (FieldEntity field : fields) {

            XmlTag[] xmlTags = rootTag.getSubTags();
            for (XmlTag xmlTag : xmlTags) {
//                System.out.println("~~~~!attribute: " + xmlTag.getAttribute("name").getValue());
//                System.out.println("~~~~!field.source: " + field.source);
                if (xmlTag.getAttribute("name").getValue().equals(field.source)) {
                    field.result = xmlTag.getValue().getText();
                    System.out.println("~~~attribute: " + field.result);
                }
            }

            XmlTag childTag = rootTag.createChildTag("string", "", field.result, false);
            childTag.setAttribute("name", field.source.substring(0, 63));
            rootTag.add(childTag);
        }
    }


    public void process(TaskHolder taskHolder) {
        if (taskHolder.selectedFields().isEmpty()) {
            return;
        }
        saveAllFile();
        XmlFile xmlFile = openStringsFile(taskHolder);
        if (xmlFile == null) {
            return;
        }
        XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag == null) {
            return;
        }
        writeComment(rootTag, taskHolder.descTag);
        writeContent(rootTag, taskHolder);
        saveAllFile();
    }

    public static String htmlEncode(String source) {
        if (source == null) {
            return "";
        }
        String html = "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    System.out.println("~~~catch < ");
                    buffer.append("&lt;");
                    break;
                case '>':
                    System.out.println("~~~catch > ");
                    buffer.append("&gt;");
                    break;
                case '&':
                    System.out.println("~~~catch & ");
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case 10:
                case 13:
                    break;
                default:
                    buffer.append(c);
            }
        }
        html = buffer.toString();
        return html;
    }
}
