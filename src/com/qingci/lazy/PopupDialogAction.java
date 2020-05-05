package com.qingci.lazy;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PopupDialogAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if (Objects.isNull(project)) {
            return;
        }

        MyDialog myDialog = new MyDialog(project);
        myDialog.show();
        myDialog.doValidate();
        boolean ok = myDialog.isOK();

        if (ok) {
            ConverterData data = myDialog.getData();
            String choosePkg = PropertiesComponent.getInstance(project).getValue("choosePkg");

            if (StringUtils.isBlank(choosePkg)) {
                return;
            }

            PsiPackage pkg = JavaPsiFacade.getInstance(project).findPackage(choosePkg);
            if (Objects.isNull(pkg)) {
                return;
            }
            PsiDirectory directory = pkg.getDirectories(GlobalSearchScopesCore.projectProductionScope(project))[0];

            WriteCommandAction.runWriteCommandAction(project, () -> {
                String sourceClass = data.getSourceClass().getName();
                String targetClass = data.getTargetClass().getName();
                String convert = sourceClass + "2" + targetClass + "Converter";
                PsiFile file = directory.createFile(convert + ".java");
                if (file instanceof PsiJavaFile) {
                    PsiJavaFile converterFile = (PsiJavaFile) file;

                    String path = converterFile.getVirtualFile().getPath();

                    VirtualFile virtualFile = converterFile.getVirtualFile();
                    VirtualFileManager manager = VirtualFileManager.getInstance();

                    String content = buildContent(sourceClass, targetClass, convert);

                    // write content
                    try {
                        if (virtualFile != null && virtualFile.exists()) {
                            // write
                            virtualFile.setBinaryContent(content.getBytes(StandardCharsets.UTF_8));
                        } else {
                            File f = new File(path);
                            FileUtils.writeStringToFile(f, content, "utf-8");
                            virtualFile = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(path));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    VirtualFile finalVirtualFile = virtualFile;

                    ApplicationManager.getApplication()
                            .invokeLater(() -> FileEditorManager.getInstance(project).openFile(finalVirtualFile, true, true));

                    // set package name
                    converterFile.setPackageName(pkg.getQualifiedName());

                    // import class
                    PsiClass parent = JavaPsiFacade.getInstance(project).findClass("org.springframework.core.convert.converter.Converter", GlobalSearchScope.allScope(project));
                    PsiClass copy = JavaPsiFacade.getInstance(project).findClass("org.springframework.beans.BeanUtils", GlobalSearchScope.allScope(project));
                    PsiClass component = JavaPsiFacade.getInstance(project).findClass("org.springframework.stereotype.Component", GlobalSearchScope.allScope(project));

                    doImport(converterFile, parent);
                    doImport(converterFile, copy);
                    doImport(converterFile, component);
                    doImport(converterFile, data.getSourceClass());
                    doImport(converterFile, data.getTargetClass());

                    // format file
                    CodeStyleManager.getInstance(project).reformat(converterFile);
                }
            });
        }

    }

    @NotNull
    private String buildContent(String sourceClass, String targetClass, String convert) {
        StringBuilder content = new StringBuilder();
        content.append("@Component\r\n")
                .append("public class ").append(convert).append(" implements Converter<").append(sourceClass).append(", ").append(targetClass).append("> {\r\n")
                .append("@Override\r\n")
                .append("public ").append(targetClass).append(" convert(").append(sourceClass).append(" source) {\r\n")
                .append(targetClass).append(" target = new ").append(targetClass).append("();\r\n")
                .append("BeanUtils.copyProperties(source.data(), target);\r\n")
                .append("return target;\r\n")
                .append("}")
                .append("}");
        return content.toString();
    }

    private void doImport(PsiJavaFile javaFile, PsiClass aClass) {
        if (Objects.nonNull(aClass)) {
            javaFile.importClass(aClass);
        }
    }

}
