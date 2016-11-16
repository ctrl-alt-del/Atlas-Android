package com.layer.atlas.tenor;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tenor.android.core.models.GlidePayload;
import com.tenor.android.core.models.Media;
import com.tenor.android.core.utils.AbstractGlideUtils;
import com.tenor.android.core.utils.AbstractUIUtils;

public class GlideUtils extends AbstractGlideUtils {

    public static void resumeRequests(@Nullable final Context context) {
        if (context != null) {
            Glide.with(context).resumeRequests();
        }
    }

    public static void pauseRequests(@Nullable final Context context) {
        if (context != null) {
            Glide.with(context).pauseRequests();
        }
    }

    public static void loadGif(@Nullable final Activity activity,
                               @Nullable final GlidePayload payload) {
        if (activity == null || payload == null || AbstractUIUtils.isActivityDestroyed(activity)) {
            return;
        }

        GifRequestBuilder<String> requestBuilder = Glide.with(activity).load(payload.getPath()).asGif()
                .diskCacheStrategy(DiskCacheStrategy.RESULT);

        if (payload.getWidth() > 0 && payload.getHeight() > 0) {
            requestBuilder.override(payload.getWidth(), payload.getHeight());
        }
        load(requestBuilder, payload);
    }
}
