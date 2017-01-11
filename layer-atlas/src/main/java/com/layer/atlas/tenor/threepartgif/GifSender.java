package com.layer.atlas.tenor.threepartgif;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AttachmentSender;
import com.layer.atlas.util.Log;
import com.layer.atlas.util.Util;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.PushNotificationPayload;
import com.tenor.android.sdk.models.Result;
import com.tenor.android.sdk.networks.ApiClient;
import com.tenor.android.sdk.responses.BaseError;
import com.tenor.android.sdk.responses.GifsResponse;
import com.tenor.android.sdk.responses.WeakRefCallback;
import com.tenor.android.sdk.utils.AbstractLocaleUtils;
import com.tenor.android.sdk.utils.AbstractSessionUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * GallerySender creates a ThreePartImage from the a selected image from the user's gallety.
 * Requires `Manifest.permission.READ_EXTERNAL_STORAGE` to read photos from external storage.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GifSender extends AttachmentSender {
    private static final String PERMISSION = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) ? Manifest.permission.READ_EXTERNAL_STORAGE : null;
    public static final int ACTIVITY_REQUEST_CODE = 10;
    public static final int PERMISSION_REQUEST_CODE = 11;

    private WeakReference<Activity> mActivity = new WeakReference<Activity>(null);

    public GifSender(int titleResId, Integer iconResId, Activity activity) {
        this(activity.getString(titleResId), iconResId, activity);
    }

    public GifSender(String title, Integer iconResId, Activity activity) {
        super(title, iconResId);
        mActivity = new WeakReference<>(activity);
    }

//    private void startGalleryIntent(Activity activity) {
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        activity.startActivityForResult(Intent.createChooser(intent, getContext().getString(R.string.atlas_gallery_sender_chooser)), ACTIVITY_REQUEST_CODE);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE) return;
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (Log.isLoggable(Log.VERBOSE)) Log.v("Gallery permission denied");
            return;
        }
        Activity activity = mActivity.get();
        if (activity == null) return;
//        startGalleryIntent(activity);
    }

    public interface OnGifRequestPermitted {
        boolean onReceiveGifRequestPermissionSucceeded();

        boolean onReceiveGifRequestPermissionFailed();
    }

    public boolean requestSend(@Nullable final OnGifRequestPermitted listener) {
        if (!requestSend()) {
            // No permission
            return false;
        }
        return listener != null && listener.onReceiveGifRequestPermissionSucceeded();
    }

    // TODO: this may not be needed
    @Override
    public boolean requestSend() {
        Activity activity = mActivity.get();
        if (activity == null) return false;
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Sending gallery image");
        if (PERMISSION != null && checkSelfPermission(activity, PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{PERMISSION}, PERMISSION_REQUEST_CODE);
            return true;
        }
//        startGalleryIntent(activity);

        return true;
    }

    public boolean send(Activity activity, Result result) {
        try {
            Identity me = getLayerClient().getAuthenticatedUser();
            String myName = me == null ? "" : Util.getDisplayName(me);
            Message message = ThreePartGifUtils.newThreePartGifMessage(activity, getLayerClient(), result);

            PushNotificationPayload payload = new PushNotificationPayload.Builder()
                    .text(activity.getString(R.string.atlas_notification_gif, myName))
                    .build();
            message.getOptions().defaultPushNotificationPayload(payload);
            send(message);

            // register share to improve the accuracy of search results in the future
            ApiClient.registerShare(activity, result.getId());
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        }
        return true;
    }
}
