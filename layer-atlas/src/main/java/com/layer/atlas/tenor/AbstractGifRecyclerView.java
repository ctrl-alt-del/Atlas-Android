package com.layer.atlas.tenor;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.layer.atlas.tenor.adapter.IGifAdapter;
import com.layer.atlas.tenor.adapter.OnSendGifListener;
import com.layer.atlas.tenor.messagetype.gif.GifLoaderClient;
import com.layer.atlas.tenor.messagetype.threepartgif.GifSender;

public abstract class AbstractGifRecyclerView extends RecyclerView {

    public AbstractGifRecyclerView(Context context) {
        this(context, null);
    }

    public AbstractGifRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractGifRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnSendGifListener(OnSendGifListener listener) {
        if (getAdapter() == null) {
            throw new IllegalStateException("Please call setAdapter() first before calling this method.");
        }

        if (!(getAdapter() instanceof IGifAdapter)) {
            throw new IllegalArgumentException("Please make sure your adapter implements IGifAdapter");
        }
        ((IGifAdapter) getAdapter()).setOnSendGifListener(listener);
    }

    public void loadGifs(boolean append) {
        postLoadGifs(append, 0);
    }

    public abstract void postLoadGifs(boolean append, long delay);

    public abstract void setGifSender(GifSender sender);
}
