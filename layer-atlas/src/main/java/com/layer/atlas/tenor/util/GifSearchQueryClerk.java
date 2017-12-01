package com.layer.atlas.tenor.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Suggest GIF search query based on the chat history or whatever user last typed
 */
public class GifSearchQueryClerk {

    @NonNull
    private String mSearchQuery = StringConstant.EMPTY;
    @NonNull
    private String mPreviousSearchQuery = StringConstant.EMPTY;

    public GifSearchQueryClerk() {
        clear();
    }

    public void update(@NonNull String query) {
        if (TextUtils.isEmpty(query)) {
            return;
        }
        mPreviousSearchQuery = mSearchQuery;
        mSearchQuery = sanitizeQuery(query);
    }

    @NonNull
    public String getSearchQuery() {
        return mSearchQuery;
    }

    public void clear() {
        mSearchQuery = StringConstant.EMPTY;
        mPreviousSearchQuery = StringConstant.EMPTY;
    }

    public boolean isSearchQueryChanged() {
        return mPreviousSearchQuery.equals(mSearchQuery);
    }

    public boolean isSearchQueryChanged(@NonNull String query) {
        return mSearchQuery.equals(sanitizeQuery(query));
    }

    private String sanitizeQuery(@NonNull String query) {
        return StringConstant.getOrEmpty(query).trim().toLowerCase(Locale.US);
    }

    /*
     * ======================
     * Static Methods
     * ======================
     */
    private static GifSearchQueryClerk sClerk;

    public static GifSearchQueryClerk get() {
        if (sClerk == null) {
            sClerk = new GifSearchQueryClerk();
        }
        return sClerk;
    }
}
