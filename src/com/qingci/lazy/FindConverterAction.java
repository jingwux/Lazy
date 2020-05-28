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
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindConverterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        SelectionModel selectionModel = e.getData(PlatformDataKeys.EDITOR).getSelectionModel();
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        selectionModel.selectLineAtCaret();
        String line = selectionModel.getSelectedText();
        selectionModel.removeSelection();

        String regex = "convert[(].*\\.class";
        Pattern r = Pattern.compile(regex);
        Matcher matcher = r.matcher(line);
        if (matcher.find()) {
            String convert = matcher.group();
            String paramStr = convert.substring(convert.indexOf("(") + 1, convert.lastIndexOf("."));
            String replace = paramStr.replace(" ", "");
            String[] params = replace.split(",");
            String source = params[0];
            String target = params[1];

            file.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitLocalVariable(PsiLocalVariable variable) {
                    super.visitLocalVariable(variable);
                    if (StringUtils.equals(variable.getName(), source)) {
                        PsiTypeElement typeElement = variable.getTypeElement();
                        PsiClass[] classes = PsiShortNamesCache.getInstance(project).getClassesByName(typeElement.getText() + "2" + target + "Converter", GlobalSearchScope.projectScope(project));
                        PsiClass aClass = classes[0];
                        VirtualFile virtualFile = aClass.getOriginalElement().getContainingFile().getVirtualFile();
                        ApplicationManager.getApplication()
                                .invokeLater(() -> FileEditorManager.getInstance(project).openFile(virtualFile, true, true));
                    }

                }
            });

        }

    }

}
