package com.qingci.lazy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConvertDialog extends DialogWrapper {

    private ConvertLayout layout;

    protected ConvertDialog(@Nullable Project project) {
        super(true);
        setTitle("Pojo Converter");
        layout = new ConvertLayout(project);
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        //定义表单的主题，放置到IDEA会话框的中央位置
        return layout.$$$getRootComponent$$$();
    }

    public ConverterData getData() {
        return layout.getConvertData();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (layout.getConvertData().getSourceClass() == null) {
            return new ValidationInfo("请选择 Source Pojo");
        }
         if (layout.getConvertData().getTargetClass() == null) {
            return new ValidationInfo("请选择 Target Pojo");
        }
        return null;
    }
}
