package com.layer.tenor.model;


import android.support.annotation.NonNull;

public interface IMinimalResult {

    @NonNull
    String getId();

    @NonNull
    String getUrl();

    @NonNull
    String getPreviewUrl();

    int getWidth();

    int getHeight();
}
