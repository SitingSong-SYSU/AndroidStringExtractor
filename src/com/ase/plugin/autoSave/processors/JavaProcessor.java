package com.ase.plugin.autoSave.processors;


import com.ase.plugin.autoSave.inspection.*;
import com.ase.plugin.autoSave.model.Action;
import com.ase.plugin.autoSave.model.ExecutionMode;
import com.intellij.codeInspection.ExplicitTypeCanBeDiamondInspection;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.visibility.VisibilityInspection;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.siyeh.ig.classlayout.FinalPrivateMethodInspection;
import com.siyeh.ig.inheritance.MissingOverrideAnnotationInspection;
import com.siyeh.ig.maturity.SuppressionAnnotationInspection;
import com.siyeh.ig.performance.MethodMayBeStaticInspection;
import com.siyeh.ig.style.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Available processors for java.
 */
public enum JavaProcessor implements Processor {

    stringExtractor(Action.stringExtractor, StringExtractorInspection::new),

    fieldCanBeFinal(Action.fieldCanBeFinal,
            FieldMayBeFinalInspection::new),

    localCanBeFinal(Action.localCanBeFinal,
            CustomLocalCanBeFinal::new),

    localCanBeFinalExceptImplicit(Action.localCanBeFinalExceptImplicit,
            () -> {
                CustomLocalCanBeFinal inspection = new CustomLocalCanBeFinal();
                inspection.REPORT_IMPLICIT_FINALS = false;
                return inspection;
            }),

    methodMayBeStatic(Action.methodMayBeStatic,
            MethodMayBeStaticInspection::new),

    unqualifiedFieldAccess(Action.unqualifiedFieldAccess,
            UnqualifiedFieldAccessInspection::new),

    unqualifiedMethodAccess(Action.unqualifiedMethodAccess,
            UnqualifiedMethodAccessInspection::new),

    unqualifiedStaticMemberAccess(Action.unqualifiedStaticMemberAccess,
            () -> {
                UnqualifiedStaticUsageInspection inspection = new UnqualifiedStaticUsageInspection();
                inspection.m_ignoreStaticFieldAccesses = false;
                inspection.m_ignoreStaticMethodCalls = false;
                inspection.m_ignoreStaticAccessFromStaticContext = false;
                return inspection;
            }),

    customUnqualifiedStaticMemberAccess(Action.customUnqualifiedStaticMemberAccess,
            CustomUnqualifiedStaticUsageInspection::new),

    missingOverrideAnnotation(Action.missingOverrideAnnotation,
            () -> {
                MissingOverrideAnnotationInspection inspection = new MissingOverrideAnnotationInspection();
                inspection.ignoreObjectMethods = false;
                return inspection;
            }),

    useBlocks(Action.useBlocks,
            ControlFlowStatementWithoutBracesInspection::new),

    generateSerialVersionUID(Action.generateSerialVersionUID,
            SerializableHasSerialVersionUIDFieldInspectionWrapper::get),

    unnecessaryThis(Action.unnecessaryThis,
            UnnecessaryThisInspection::new),

    finalPrivateMethod(Action.finalPrivateMethod,
            FinalPrivateMethodInspection::new),

    unnecessaryFinalOnLocalVariableOrParameter(Action.unnecessaryFinalOnLocalVariableOrParameter,
            UnnecessaryFinalOnLocalVariableOrParameterInspection::new),

    explicitTypeCanBeDiamond(Action.explicitTypeCanBeDiamond,
            ExplicitTypeCanBeDiamondInspection::new),

    suppressAnnotation(Action.suppressAnnotation,
            SuppressionAnnotationInspection::new),

    unnecessarySemicolon(Action.unnecessarySemicolon,
            UnnecessarySemicolonInspection::new),

    singleStatementInBlock(Action.singleStatementInBlock,
            SingleStatementInBlockInspection::new),

    accessCanBeTightened(Action.accessCanBeTightened,
            () -> new CustomAccessCanBeTightenedInspection(new VisibilityInspection())),

    ;

    private final Action action;
    private final LocalInspectionTool inspection;

    JavaProcessor(Action action, Supplier<LocalInspectionTool> inspection) {
        this.action = action;
        this.inspection = inspection.get();
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public Set<ExecutionMode> getModes() {
        return EnumSet.allOf(ExecutionMode.class);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public SaveWriteCommand getSaveCommand(Project project, Set<PsiFile> psiFiles) {
        BiFunction<Project, PsiFile[], Runnable> command =
                (p, f) -> new InspectionRunnable(project, psiFiles, getInspection());
        return new SaveWriteCommand(project, psiFiles, getModes(), getAction(), command);
    }

    public LocalInspectionTool getInspection() {
        return inspection;
    }

    public static Optional<Processor> getProcessorForAction(Action action) {
        return stream().filter(processor -> processor.getAction().equals(action)).findFirst();
    }

    public static Stream<Processor> stream() {
        return Arrays.stream(values());
    }

}