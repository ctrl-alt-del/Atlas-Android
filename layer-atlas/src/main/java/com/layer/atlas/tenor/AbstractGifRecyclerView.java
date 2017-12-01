package com.layer.atlas.tenor;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.layer.atlas.tenor.adapter.OnDismissPopupWindowListener;
import com.layer.atlas.tenor.messagetype.gif.GifLoaderClient;
import com.layer.atlas.tenor.messagetype.threepartgif.GifSender;

public abstract class AbstractGifRecyclerView extends RecyclerView {

    private GifLoaderClient mGifLoaderClient;
    private OnDismissPopupWindowListener mOnDismissListener;
    private GifSender mGifSender;

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

    @CallSuper
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

    public void loadGifs(boolean append) {
        postLoadGifs(append, 0);
    }

    public abstract void postLoadGifs(boolean append, long delay);
}
