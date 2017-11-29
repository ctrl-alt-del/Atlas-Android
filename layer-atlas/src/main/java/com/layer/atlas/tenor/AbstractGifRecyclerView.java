package com.layer.atlas.tenor;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.layer.atlas.tenor.adapters.OnDismissPopupWindowListener;
import com.layer.atlas.tenor.threepartgif.GifLoaderClient;
import com.layer.atlas.tenor.threepartgif.GifSender;

public class AbstractGifRecyclerView extends RecyclerView {

    private GifLoaderClient mGifLoaderClient;
    private OnDismissPopupWindowListener mOnDismissListener;
    private GifSender mGifSender;

    @NonNull
    private String mSearchQuery = "";
    @NonNull
    private String mPreviousSearchQuery = mSearchQuery;

    public AbstractGifRecyclerView(Context context) {
        this(context, null);
    }

    public AbstractGifRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractGifRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnDismissPopupWindowListener(OnDismissPopupWindowListener dismissListener) {
        mOnDismissListener = dismissListener;
    }

    public void setGifSender(GifSender sender) {
        mGifSender = sender;
    }

    public GifSender getGifSender() {
        return mGifSender;
    }

    public void setGifLoaderClient(GifLoaderClient gifLoaderClient) {
        mGifLoaderClient = gifLoaderClient;
    }

    public GifLoaderClient getGifLoaderClient() {
        return mGifLoaderClient;
    }

    @CallSuper
    public void setSearchQuery(String searchQuery) {
        mPreviousSearchQuery = mSearchQuery;
        mSearchQuery = StringConstant.getOrEmpty(searchQuery).trim();
    }

    @NonNull
    public String getSearchQuery() {
        return mSearchQuery;
    }

    public boolean isSearchQueryChanged() {
        return mSearchQuery.equals(mPreviousSearchQuery);
    }
}
