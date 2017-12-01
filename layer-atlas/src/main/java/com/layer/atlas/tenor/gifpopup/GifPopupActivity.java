package com.layer.atlas.tenor.gifpopup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.layer.atlas.R;
import com.layer.atlas.tenor.messagetype.gif.GifLoaderClient;
import com.layer.atlas.tenor.messagetype.threepartgif.GifInfo;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.messaging.MessagePart;

/**
 * GifPopupActivity implements a ful resolution gif viewer Activity.  This Activity
 * registers with the LayerClient as a LayerProgressListener to monitor progress.
 */
public class GifPopupActivity extends Activity implements LayerProgressListener.BackgroundThread.Weak, SubsamplingScaleImageView.OnImageEventListener {
    private static LayerClient sLayerClient;
    private static GifLoaderClient sGifLoaderClient;

    private ImageView mImageView;
    private ContentLoadingProgressBar mProgressBar;
    private String mMessagePartFullId;
    private GifInfo mGifInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(R.color.atlas_image_popup_background);
        setContentView(R.layout.tenor_gif_popup);
        mImageView = (ImageView) findViewById(R.id.image_popup);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.image_popup_progress);

        Intent intent = getIntent();
        if (intent == null) return;
        mGifInfo = intent.getParcelableExtra("info");
        mMessagePartFullId = intent.getStringExtra("fullId");

        mProgressBar.show();
        if (sGifLoaderClient != null) {
            sGifLoaderClient.load(mImageView, mGifInfo, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sLayerClient.registerProgressListener(null, this);
        sGifLoaderClient.resume(mImageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sLayerClient.unregisterProgressListener(null, this);
        sGifLoaderClient.pause(mImageView);
    }

    public static void init(LayerClient layerClient, GifLoaderClient gifLoaderClient) {
        sLayerClient = layerClient;
        sGifLoaderClient = gifLoaderClient;
    }

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
