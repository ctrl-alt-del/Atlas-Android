package com.layer.atlas.tenor.threepartgif;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.ImageView;

import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.layer.atlas.R;
import com.layer.atlas.tenor.GlideUtils;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.messaging.MessagePart;
import com.tenor.android.core.listeners.OnImageLoadedListener;
import com.tenor.android.core.models.GlidePayload;

/**
 * AtlasImagePopupActivity implements a ful resolution image viewer Activity.  This Activity
 * registers with the LayerClient as a LayerProgressListener to monitor progress.
 */
public class GifPopupActivity extends Activity implements LayerProgressListener.BackgroundThread.Weak, SubsamplingScaleImageView.OnImageEventListener {
    private static LayerClient sLayerClient;

    private ImageView mImageView;
    private ContentLoadingProgressBar mProgressBar;
    private String mMessagePartFullId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(R.color.atlas_image_popup_background);
        setContentView(R.layout.tenor_gif_popup);
        mImageView = (ImageView) findViewById(R.id.image_popup);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.image_popup_progress);

        Intent intent = getIntent();
        if (intent == null) return;
        mMessagePartFullId = intent.getStringExtra("fullId");

        mProgressBar.show();
        GlidePayload payload = new GlidePayload(mImageView, mMessagePartFullId)
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

        GifRequestBuilder<String> requestBuilder = Glide.with(this)
                .load(payload.getPath())
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        GlideUtils.load(requestBuilder, payload);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sLayerClient.registerProgressListener(null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sLayerClient.unregisterProgressListener(null, this);
    }

    public static void init(LayerClient layerClient) {
        sLayerClient = layerClient;
    }


    //==============================================================================================
    // SubsamplingScaleImageView.OnImageEventListener: hide progress bar when full part loaded
    //==============================================================================================

    @Override
    public void onReady() {

    }

    @Override
    public void onImageLoaded() {
        mProgressBar.hide();
    }

    @Override
    public void onPreviewLoadError(Exception e) {
        if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        mProgressBar.hide();
    }

    @Override
    public void onImageLoadError(Exception e) {
        if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        mProgressBar.hide();
    }

    @Override
    public void onTileLoadError(Exception e) {
        if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        mProgressBar.hide();
    }


    //==============================================================================================
    // LayerProgressListener: update progress bar while downloading
    //==============================================================================================

    @Override
    public void onProgressStart(MessagePart messagePart, Operation operation) {
        if (!messagePart.getId().equals(mMessagePartFullId)) return;
        mProgressBar.setProgress(0);
    }

    @Override
    public void onProgressUpdate(MessagePart messagePart, Operation operation, long bytes) {
        if (!messagePart.getId().equals(mMessagePartFullId)) return;
        double fraction = (double) bytes / (double) messagePart.getSize();
        int progress = (int) Math.round(fraction * mProgressBar.getMax());
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onProgressComplete(MessagePart messagePart, Operation operation) {
        if (!messagePart.getId().equals(mMessagePartFullId)) return;
        mProgressBar.setProgress(mProgressBar.getMax());
    }

    @Override
    public void onProgressError(MessagePart messagePart, Operation operation, Throwable e) {
        if (!messagePart.getId().equals(mMessagePartFullId)) return;
        if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
    }

}
