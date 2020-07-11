package com.qingci.lazy.data;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;

public class ConverterData {
    private PsiPackage converterPackage;
    private PsiClass sourceClass;
    private PsiClass targetClass;

    public PsiPackage getConverterPackage() {
        return converterPackage;
    }

    public ConverterData setConverterPackage(PsiPackage converterPackage) {
        this.converterPackage = converterPackage;
        return this;
    }

    public PsiClass getSourceClass() {
        return sourceClass;
    }

    public ConverterData setSourceClass(PsiClass sourceClass) {
        this.sourceClass = sourceClass;
        return this;
    }

    public PsiClass getTargetClass() {
        return targetClass;
    }

    public ConverterData setTargetClass(PsiClass targetClass) {
        this.targetClass = targetClass;
        return this;
    }
}
