package com.ase.plugin.autoSave.inspection;


import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.style.UnqualifiedStaticUsageInspection;
import org.jetbrains.annotations.NotNull;

/**
 * Copy pasting because: cannot extend. Do not reformat (useful for diffs)
 * 复制粘贴原因:无法扩展。不要重新格式化(对于diffs很有用)
 * 向声明类之外的静态成员访问添加类限定符
 *
 * @see com.siyeh.ig.style.UnqualifiedStaticUsageInspection.UnqualifiedStaticCallVisitor
 */
public class CustomUnqualifiedStaticUsageInspection extends UnqualifiedStaticUsageInspection {

    @Override
    public BaseInspectionVisitor buildVisitor() {
        return new CustomUnqualifiedStaticCallVisitor();
    }

    private class CustomUnqualifiedStaticCallVisitor extends BaseInspectionVisitor {
        @Override
        public void visitMethodCallExpression(
                @NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (m_ignoreStaticMethodCalls) {
                return;
            }
            final PsiReferenceExpression methodExpression =
                    expression.getMethodExpression();
            if (!isUnqualifiedStaticAccess(methodExpression)) {
                return;
            }
            registerError(methodExpression, expression);
        }

        @Override
        public void visitReferenceExpression(
                @NotNull PsiReferenceExpression expression) {
            super.visitReferenceExpression(expression);
            if (m_ignoreStaticFieldAccesses) {
                return;
            }
            final PsiElement element = expression.resolve();
            if (!(element instanceof PsiField)) {
                return;
            }
            final PsiField field = (PsiField)element;
            if (field.hasModifierProperty(PsiModifier.FINAL) &&
                    PsiUtil.isOnAssignmentLeftHand(expression)) {
                return;
            }
            if (!isUnqualifiedStaticAccess(expression)) {
                return;
            }
            registerError(expression, expression);
        }

        private boolean isUnqualifiedStaticAccess(
                PsiReferenceExpression expression) {
            if (m_ignoreStaticAccessFromStaticContext) {
                final PsiMember member =
                        PsiTreeUtil.getParentOfType(expression,
                                PsiMember.class);
                if (member != null &&
                        member.hasModifierProperty(PsiModifier.STATIC)) {
                    return false;
                }
            }
            final PsiExpression qualifierExpression =
                    expression.getQualifierExpression();
            if (qualifierExpression != null) {
                return false;
            }
            final JavaResolveResult resolveResult =
                    expression.advancedResolve(false);
            final PsiElement currentFileResolveScope =
                    resolveResult.getCurrentFileResolveScope();
            if (currentFileResolveScope instanceof PsiImportStaticStatement) {
                return false;
            }
            final PsiElement element = resolveResult.getElement();
            if (!(element instanceof PsiField) &&
                    !(element instanceof PsiMethod)) {
                return false;
            }
            final PsiMember member = (PsiMember)element;
            if (member instanceof PsiEnumConstant &&
                    expression.getParent() instanceof PsiSwitchLabelStatement) {
                return false;
            }
            PsiClass expressionClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            PsiClass memberClass = member.getContainingClass();
            if (memberClass != null && memberClass.equals(expressionClass)) {
                return false;
            }
            return member.hasModifierProperty(PsiModifier.STATIC);
        }
    }

}