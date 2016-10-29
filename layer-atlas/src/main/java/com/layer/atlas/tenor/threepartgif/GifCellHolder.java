package com.layer.atlas.tenor.threepartgif;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.atlas.tenor.GlideUtils;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.tenor.android.core.listeners.OnImageLoadedListener;
import com.tenor.android.core.models.GlidePayload;

import java.lang.ref.WeakReference;

public class GifCellHolder extends AtlasCellFactory.CellHolder implements View.OnClickListener, View.OnLongClickListener {

    private static final int PLACEHOLDER = R.drawable.atlas_message_item_cell_placeholder;

    private ImageView mImageView;
    private ContentLoadingProgressBar mProgressBar;

    private final LayerClient mLayerClient;
    private final WeakReference<Activity> mrfActivity;
    private Message mMessage;
    private GifInfo mInfo;

    public GifCellHolder(View view, LayerClient layerClient, WeakReference<Activity> rfActivity) {
        mImageView = (ImageView) view.findViewById(R.id.cell_image);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.cell_progress);
        mLayerClient = layerClient;
        mrfActivity = rfActivity;

        mImageView.setOnClickListener(this);
        mImageView.setOnLongClickListener(this);
    }

    public void render(@Nullable final GifInfo info, @Nullable final Message message) {

        if (info == null || message == null) {
            return;
        }

        mInfo = info;
        mMessage = message;

        mProgressBar.show();
        GlidePayload payload = new GlidePayload(mImageView, info.previewPartId)
                .setPlaceholder(PLACEHOLDER)
                .setListener(new OnImageLoadedListener(){
                    @Override
                    public void onImageLoadingFinished() {
                        mProgressBar.hide();
                    }

                    @Override
                    public void onImageLoadingFailed() {
                        mProgressBar.hide();
                    }
                });

        GifRequestBuilder<String> requestBuilder = GlideUtils.getInstance()
                .load(payload.getPath())
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(info.width > 0 ? info.width : Target.SIZE_ORIGINAL,
                        info.height > 0 ? info.height : Target.SIZE_ORIGINAL);

        GlideUtils.load(requestBuilder, payload);
    }

    @Override
    public void onClick(View v) {
        GifPopupActivity.init(mLayerClient);
        Activity activity = mrfActivity.get();
        if (activity == null) return;

        Intent intent = new Intent(activity, GifPopupActivity.class);
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
