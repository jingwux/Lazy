package com.qingci.lazy;

import com.intellij.psi.PsiPackage;

public class Cache {

    public static PsiPackage convertPackage;

    public static void setConvertPackage(PsiPackage convertPackage) {
        Cache.convertPackage = convertPackage;
    }
}
