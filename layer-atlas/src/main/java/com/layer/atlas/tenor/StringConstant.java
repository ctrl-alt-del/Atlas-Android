package com.layer.atlas.tenor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class StringConstant {
    public static final String EMPTY = "";

    public static <T> T getOrDef(@Nullable T t, @NonNull T def) {
        return t != null?t:def;
    }

    public static String getOrEmpty(@Nullable String str) {
        return getOrDef(str, "");
    }
}
