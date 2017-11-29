package com.layer.tenor;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.layer.tenor.adapter.OnDismissPopupWindowListener;
import com.layer.tenor.messagetype.gif.GifLoaderClient;
import com.layer.tenor.messagetype.threepartgif.GifSender;

public class AbstractGifRecyclerView extends RecyclerView {

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
}
