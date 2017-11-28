package com.layer.atlas.tenor.threepartgif;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.Nullable;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AttachmentSender;
import com.layer.atlas.util.Util;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.PushNotificationPayload;
import com.tenor.android.core.constant.StringConstant;
import com.tenor.android.core.model.impl.Result;
import com.tenor.android.core.network.ApiClient;
import com.tenor.android.core.util.AbstractWeakReferenceUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GifSender extends AttachmentSender {

    private final WeakReference<Activity> mActivity;

    public GifSender(int titleResId, Integer iconResId, Activity activity) {
        this(activity.getString(titleResId), iconResId, activity);
    }

    public GifSender(String title, Integer iconResId, Activity activity) {
        super(title, iconResId);
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // no longer in use
    }

    @Override
    public boolean requestSend() {
        // no longer in use
        return false;
    }

    public boolean send(@Nullable Result result) {
        if (result == null) {
            return false;
        }

        if (!AbstractWeakReferenceUtils.isAlive(mActivity)
                || mActivity.get().isFinishing()) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && mActivity.get().isDestroyed()) {
            return false;
        }

        Identity me = getLayerClient().getAuthenticatedUser();
        String myName = me != null ? Util.getDisplayName(me) : StringConstant.EMPTY;
        final Message message;
        try {
            message = ThreePartGifUtils.newThreePartGifMessage(mActivity.get(), getLayerClient(), result);
        } catch (IOException ignored) {
            return false;
        }

        PushNotificationPayload payload = new PushNotificationPayload.Builder()
                .text(mActivity.get().getString(R.string.atlas_notification_gif, myName))
                .build();
        message.getOptions().defaultPushNotificationPayload(payload);

        // register share to improve the accuracy of search results in the future
        // ApiClient.registerShare(mActivity.get(), result.getId());

        return send(message);
    }
}
