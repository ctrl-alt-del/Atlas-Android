package com.layer.atlas.tenor.adapter;

import android.support.annotation.NonNull;

import com.layer.atlas.tenor.model.IMinimalResult;

public interface OnSendGifListener {
    void onGifSent(@NonNull IMinimalResult minimalResult);
}
