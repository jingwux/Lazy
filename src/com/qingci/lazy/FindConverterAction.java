package com.qingci.lazy;

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
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;

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
                if (StringUtils.equals(line, statement.getText())) {
                    // 当前程序语句中被调用的方法对应一个表达式对象
                    // 获取当前执行语句的表达式对象
                    PsiExpression expression = ((PsiExpressionStatementImpl) statement).getExpression();
                    // 获取当表达式的参数列表对象中参数表达式(数组，每一个参数也有对应的表达式对象)
                    PsiExpression[] expressions = ((PsiMethodCallExpressionImpl) expression).getArgumentList().getExpressions();
                    PsiType sourceType = expressions[0].getType();
                    PsiType targetType = ((PsiImmediateClassType) expressions[1].getType()).getParameters()[0];

                    String sName = ((PsiImmediateClassType) sourceType).getName();
                    String tName = ((PsiClassReferenceType) targetType).getName();
                    PsiClass[] classes = PsiShortNamesCache.getInstance(project).getClassesByName(sName + "2" + tName + "Converter", GlobalSearchScope.projectScope(project));
                    PsiClass aClass = classes[0];
                    VirtualFile virtualFile = aClass.getOriginalElement().getContainingFile().getVirtualFile();
                    ApplicationManager.getApplication()
                            .invokeLater(() -> FileEditorManager.getInstance(project).openFile(virtualFile, true, true));
                }

            }

        });

    }

}
