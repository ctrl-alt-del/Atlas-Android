package com.layer.atlas.tenor.messagetype.gif;

import android.widget.ImageView;

import com.layer.atlas.tenor.messagetype.threepartgif.GifInfo;

public interface GifLoaderClient {
    interface Callback {
        <V extends ImageView> void success(V view);

        void failure();
    }

    <V extends ImageView> void load(V view, GifInfo info, Callback callback);

    <V extends ImageView> void pause(V view);

    <V extends ImageView> void resume(V view);
}
