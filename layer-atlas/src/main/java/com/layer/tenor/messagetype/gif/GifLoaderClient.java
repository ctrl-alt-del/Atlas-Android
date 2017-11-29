package com.layer.tenor.messagetype.gif;

import android.widget.ImageView;

public interface GifLoaderClient {
    interface Callback {
        <V extends ImageView> void success(V view);

        void failure();
    }

    <V extends ImageView> void load(V view, String url, Callback callback);

    void pause();

    void resume();
}
