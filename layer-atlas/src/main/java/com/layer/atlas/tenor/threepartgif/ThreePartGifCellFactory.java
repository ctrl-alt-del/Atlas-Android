package com.layer.atlas.tenor.threepartgif;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.atlas.tenor.GlideUtils;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.tenor.android.sdk.utils.AbstractGsonUtils;
import com.tenor.android.sdk.utils.AbstractWeakReferenceUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * ThreePartGif handles gif Messages with three parts: full gif, preview gif, and
 * gif metadata.
 */
public class ThreePartGifCellFactory extends AtlasCellFactory<GifCellHolder, GifInfo> {

    private final WeakReference<Activity> mActivity;
    private final LayerClient mLayerClient;

    public ThreePartGifCellFactory(Activity activity, LayerClient layerClient) {
        super(256 * 1024);
        mActivity = new WeakReference<>(activity);
        mLayerClient = layerClient;
    }

    @Override
    public boolean isBindable(Message message) {
        return ThreePartGifCellFactory.isType(message);
    }

    @Override
    public GifCellHolder createCellHolder(ViewGroup cellView, boolean isMe, LayoutInflater layoutInflater) {
        return new GifCellHolder(layoutInflater.inflate(R.layout.atlas_message_item_cell_image, cellView, true),
                mLayerClient, mActivity);
    }

    @Override
    public GifInfo parseContent(LayerClient layerClient, Message message) {
        return getInfo(message);
    }

    @Override
    public void bindCellHolder(final GifCellHolder cellHolder, final GifInfo info, final Message message, CellHolderSpecs specs) {

        if (info == null) {
            return;
        }
        cellHolder.render(info, message);
    }

    @Override
    public void onScrollStateChanged(int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (AbstractWeakReferenceUtils.isAlive(mActivity)) {
                    GlideUtils.pauseRequests(mActivity.get());
                }
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
            case RecyclerView.SCROLL_STATE_SETTLING:
                if (AbstractWeakReferenceUtils.isAlive(mActivity)) {
                    GlideUtils.resumeRequests(mActivity.get());
                }
                break;
        }
    }

    //==============================================================================================
    // Static utilities
    //==============================================================================================

    public static boolean isType(Message message) {
        List<MessagePart> parts = message.getMessageParts();
        return parts.size() == 3
                && parts.get(ThreePartGifUtils.PART_INDEX_FULL).getMimeType().equals(ThreePartGifUtils.MIME_TYPE_GIF)
                && parts.get(ThreePartGifUtils.PART_INDEX_PREVIEW).getMimeType().equals(ThreePartGifUtils.MIME_TYPE_GIF_PREVIEW)
                && parts.get(ThreePartGifUtils.PART_INDEX_INFO).getMimeType().equals(ThreePartGifUtils.MIME_TYPE_GIF_INFO);
    }

    public static String getMessagePreview(Context context, Message message) {
        return context.getString(R.string.atlas_message_preview_gif);
    }

    public static GifInfo getInfo(Message message) {

        String dimsString = new String(ThreePartGifUtils.getInfoPart(message).getData());
        if (TextUtils.isEmpty(dimsString)) {
            return null;
        }

        return parseInfo(dimsString, message);
    }

    private static GifInfo parseInfo(@Nullable final String str, @Nullable final Message message) {

        if (str == null || message == null) {
            return null;
        }

        try {
            GifInfo info = AbstractGsonUtils.getInstance().fromJson(str, GifInfo.class);

            if (TextUtils.isEmpty(info.previewPartId)) {
                info.previewPartId = new String(ThreePartGifUtils.getPreviewPart(message).getData());
            }

            if (TextUtils.isEmpty(info.fullPartId)) {
                info.fullPartId = new String(ThreePartGifUtils.getFullPart(message).getData());
            }
            return info;
        } catch (Exception ignored) {
            return null;
        }
    }
}
