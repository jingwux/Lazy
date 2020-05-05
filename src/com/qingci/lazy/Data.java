package com.qingci.lazy;

import com.intellij.psi.PsiPackage;

public class Data {

    public static PsiPackage convertPackage;

    public static void setConvertPackage(PsiPackage convertPackage) {
        Data.convertPackage = convertPackage;
    }
}
