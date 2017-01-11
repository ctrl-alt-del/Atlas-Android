package com.layer.atlas.tenor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tenor.android.sdk.constants.StringConstant;

public class SmartGifsUtils {

    private static String sLastConversation = StringConstant.EMPTY;
    private static int sPosition = -1;
    private static boolean sMessageChanged;

    public synchronized static void update(@Nullable final String str, int position) {
        if (TextUtils.isEmpty(str) || position < sPosition) {
            return;
        }
        sLastConversation = str;
        sPosition = position;
        sMessageChanged = true;
    }

    @NonNull
    public static String getSearchQuery() {
        return sLastConversation;
    }

    public static void clear() {
        sLastConversation = StringConstant.EMPTY;
        sPosition = -1;
    }

    public static boolean isMessageChanged() {
        return sMessageChanged;
    }

    public static void resetMessageChanged() {
        sMessageChanged = false;
    }
}
