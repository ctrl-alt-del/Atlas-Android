package com.layer.tenor.messagetype.threepartgif;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.ImageView;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.tenor.gifpopup.GifPopupActivity;
import com.layer.tenor.messagetype.gif.GifLoaderClient;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

public class GifCellHolder extends AtlasCellFactory.CellHolder implements View.OnClickListener, View.OnLongClickListener {

    public interface OnLoadGifCallback {
        void render(@Nullable GifInfo info, @Nullable Message message);
    }

    private static final int PLACEHOLDER = R.drawable.atlas_message_item_cell_placeholder;

    private ImageView mImageView;
    private ContentLoadingProgressBar mProgressBar;

    private final LayerClient mLayerClient;
    private final GifLoaderClient mGifLoaderClient;
    private Message mMessage;
    private GifInfo mInfo;

    public GifCellHolder(View view, LayerClient layerClient, GifLoaderClient gifLoaderClient) {
        mImageView = (ImageView) view.findViewById(R.id.cell_image);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.cell_progress);
        mLayerClient = layerClient;
        mGifLoaderClient = gifLoaderClient;

        mImageView.setOnClickListener(this);
        mImageView.setOnLongClickListener(this);
    }

    public void render(@Nullable final GifInfo info, @Nullable final Message message,
                       @Nullable OnLoadGifCallback callback) {
        if (callback != null) {
            callback.render(info, message);
        }
    }
//    {
//
//        if (info == null || message == null) {
//            return;
//        }
//
//        mInfo = info;
//        mMessage = message;
//
//        mProgressBar.show();
//        GlidePayload payload = new GlidePayload(mImageView, info.previewPartId)
//                .setPlaceholder(PLACEHOLDER)
//                .setListener(new OnImageLoadedListener() {
//                    @Override
//                    public void onImageLoadingFinished() {
//                        mProgressBar.hide();
//                    }
//
//                    @Override
//                    public void onImageLoadingFailed() {
//                        mProgressBar.hide();
//                    }
//                });
//
//        if (info.width > 0 && info.height > 0) {
//            // adjust gif size according to screen width
//            final float ratio = AbstractUIUtils.getScreenWidth(mrfActivity.get()) * 0.6f / info.width;
//            final int adjustedWidth = Math.round(info.width * ratio);
//            final int adjustedHeight = Math.round(info.height * ratio);
//
//            // load gif resource with specific width and height
//            payload.setWidth(adjustedWidth);
//            payload.setHeight(adjustedHeight);
//
//            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
//            if (params != null) {
//                // specify image view with the width and height
//                params.width = adjustedWidth;
//                params.height = adjustedHeight;
//                mImageView.setLayoutParams(params);
//            }
//        }
//
//        GlideUtils.load(mrfActivity.get(), payload);
//    }

    @Override
    public void onClick(View v) {
        GifPopupActivity.init(mLayerClient, mGifLoaderClient);

        if (!(v.getContext() instanceof Activity)) {
            return;
        }
        Activity activity = (Activity) mImageView.getContext();

        Intent intent = new Intent(mImageView.getContext(), GifPopupActivity.class);
        intent.putExtra("previewId", mInfo.previewPartId);
        intent.putExtra("fullId", mInfo.fullPartId);
        intent.putExtra("info", mInfo);

        if (Build.VERSION.SDK_INT >= 21) {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, v, "gif").toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        if (mMessage == null) {
            return false;
        }

        MessagePart full = ThreePartGifUtils.getFullPart(mMessage);
        MessagePart preview = ThreePartGifUtils.getPreviewPart(mMessage);
        MessagePart info = ThreePartGifUtils.getInfoPart(mMessage);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(full.getDataStream(), null, options);
        Log.v("Full size: " + options.outWidth + "x" + options.outHeight);

        BitmapFactory.decodeStream(preview.getDataStream(), null, options);
        Log.v("Preview size: " + options.outWidth + "x" + options.outHeight);

        Log.v("Info: " + new String(info.getData()));

        return false;
    }
}
