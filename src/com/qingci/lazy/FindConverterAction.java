package com.qingci.lazy;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiImmediateClassType;
import com.intellij.psi.impl.source.tree.java.PsiExpressionStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReturnStatementImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;

public class FindConverterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        SelectionModel selectionModel = e.getData(PlatformDataKeys.EDITOR).getSelectionModel();
        PsiFile currentPsiFile = e.getData(CommonDataKeys.PSI_FILE);

        selectionModel.selectLineAtCaret();
        String line = selectionModel.getSelectedText().replace("\n", "").trim();
        selectionModel.removeSelection();

        currentPsiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitStatement(PsiStatement statement) {
                super.visitStatement(statement);
                if (statement.getText().contains(line)) {
                    PsiExpression[] expressions = getPsiExpressions(statement);
                    PsiType sourceType = expressions[0].getType();
                    PsiType targetType = ((PsiImmediateClassType) expressions[1].getType()).getParameters()[0];

                    String sName = getSourceName(sourceType);
                    String tName = ((PsiClassReferenceType) targetType).getName();
                    PsiClass[] classes = PsiShortNamesCache.getInstance(project).getClassesByName(sName + "2" + tName + "Converter", GlobalSearchScope.projectScope(project));
                    if (classes.length > 0) {
                        PsiClass aClass = classes[0];
                        VirtualFile virtualFile = aClass.getOriginalElement().getContainingFile().getVirtualFile();
                        ApplicationManager.getApplication()
                                .invokeLater(() -> FileEditorManager.getInstance(project).openFile(virtualFile, true, true));
                    }
                }

            }

        });

    }

    @NotNull
    private String getSourceName(PsiType sourceType) {
        if (sourceType instanceof PsiImmediateClassType) {
            return ((PsiImmediateClassType) sourceType).getName();
        } else if (sourceType instanceof PsiClassReferenceType) {
            return ((PsiClassReferenceType) sourceType).getName();
        } else {
            return ((PsiClassType.Stub) sourceType).getParameters()[0].getPresentableText();
        }
    }

    /**
     * 当前程序语句中被调用的方法对应一个表达式对象
     * 获取当前执行语句的表达式对象
     *
     * @param statement
     * @return
     */
    @NotNull
    private PsiExpression[] getPsiExpressions(PsiStatement statement) {
        // 获取当表达式的参数列表对象中参数表达式(数组，每一个参数也有对应的表达式对象)
        if (statement instanceof PsiExpressionStatementImpl) {
            PsiExpression expression = ((PsiExpressionStatementImpl) statement).getExpression();
            return ((PsiMethodCallExpressionImpl) expression).getArgumentList().getExpressions();
        } else if (statement instanceof PsiReturnStatementImpl) {
            ASTNode childByType = ((PsiReturnStatementImpl) statement).findChildByType(new PsiMethodCallExpressionImpl().getElementType());
            PsiMethodCallExpressionImpl psi = childByType.getPsi(PsiMethodCallExpressionImpl.class);
            return psi.getArgumentList().getExpressions();
        }
        return null;
    }

}
