package com.layer.atlas.tenor.threepartgif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.atlas.R;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * ThreePartGif handles gif Messages with three parts: full gif, preview gif, and
 * gif metadata.
 */
public class ThreePartGifCellFactory extends AtlasCellFactory<GifCellHolder, GifInfo> {

    private final LayerClient mLayerClient;
    private final GifLoaderClient mGifLoaderClient;
    private GifCellHolder.OnLoadGifCallback mOnLoadGifCallback;

    public ThreePartGifCellFactory(LayerClient layerClient, GifLoaderClient gifLoaderClient) {
        super(256 * 1024);
        mLayerClient = layerClient;
        mGifLoaderClient = gifLoaderClient;
    }

    @Override
    public boolean isBindable(Message message) {
        return isType(message);
    }

    @Override
    public GifCellHolder createCellHolder(ViewGroup cellView, boolean isMe, LayoutInflater layoutInflater) {
        return new GifCellHolder(layoutInflater.inflate(R.layout.atlas_message_item_cell_image, cellView, true),
                mLayerClient, mGifLoaderClient);
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
        cellHolder.render(info, message, mOnLoadGifCallback);
    }

    public void setGifCellHolderOnLoadGifCallback(@NonNull GifCellHolder.OnLoadGifCallback callback) {
        mOnLoadGifCallback = callback;
    }

    @Override
    public void onScrollStateChanged(int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                mGifLoaderClient.pause();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
            case RecyclerView.SCROLL_STATE_SETTLING:
                mGifLoaderClient.resume();
                break;
        }
    }

    //==============================================================================================
    // Static utilities
    //==============================================================================================
    @Override
    public boolean isType(Message message) {
        List<MessagePart> parts = message.getMessageParts();
        return parts.size() == 3
                && parts.get(ThreePartGifUtils.PART_INDEX_FULL).getMimeType().equals(ThreePartGifUtils.MIME_TYPE_GIF)
                && parts.get(ThreePartGifUtils.PART_INDEX_PREVIEW).getMimeType().equals(ThreePartGifUtils.MIME_TYPE_GIF_PREVIEW)
                && parts.get(ThreePartGifUtils.PART_INDEX_INFO).getMimeType().equals(ThreePartGifUtils.MIME_TYPE_GIF_INFO);
    }

    @Override
    public String getPreviewText(Context context, Message message) {
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

        GifInfo info = new GifInfo();
        try {
            JSONObject infoObject = new JSONObject(str);
            info.orientation = infoObject.getInt("orientation");
            info.width = infoObject.getInt("width");
            info.height = infoObject.getInt("height");
            info.contentId = infoObject.getString("contentId");

            if (TextUtils.isEmpty(info.previewPartId)) {
                info.previewPartId = new String(ThreePartGifUtils.getPreviewPart(message).getData());
            }

            if (TextUtils.isEmpty(info.fullPartId)) {
                info.fullPartId = new String(ThreePartGifUtils.getFullPart(message).getData());
            }
            return info;
        } catch (JSONException e) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e(e.getMessage(), e);
            }
        }
        return null;
    }
}
