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

    private static RequestManager sGlide;

    public static void init(Context context) {
        if (sGlide == null) {
            sGlide = Glide.with(context);
        }
    }

    public static RequestManager getInstance() {
        return sGlide;
    }

    public static void resumeRequests() {
        if (sGlide != null) {
            sGlide.resumeRequests();
        }
    }

    public static void pauseRequests() {
        if (sGlide != null) {
            sGlide.pauseRequests();
        }
    }

    public static void loadGif(@Nullable final Activity activity,
                               @Nullable final GlidePayload payload) {
        if (activity == null || payload == null || AbstractUIUtils.isActivityDestroyed(activity)) {
            return;
        }

        GifRequestBuilder<String> requestBuilder = getInstance().load(payload.getPath()).asGif()
                .diskCacheStrategy(DiskCacheStrategy.RESULT);

        final Media media = payload.getMedia();
        if (media != null) {
            requestBuilder.override(media.getWidth(), media.getHeight());
        }
        load(requestBuilder, payload);
    }
}
