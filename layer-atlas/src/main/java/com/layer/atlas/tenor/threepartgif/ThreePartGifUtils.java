package com.layer.atlas.tenor.threepartgif;

import android.content.Context;
import android.text.TextUtils;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.tenor.android.core.models.Media;
import com.tenor.android.core.models.Result;
import com.tenor.android.core.utils.AbstractGifUtils;
import com.tenor.android.core.utils.AbstractGsonUtils;

import java.io.IOException;

public class ThreePartGifUtils {

    public static final String MIME_TYPE_GIF = "image/gif";
    public static final String MIME_TYPE_GIF_PREVIEW = "image/gif+preview";
    public static final String MIME_TYPE_GIF_INFO = "application/json+gifSize";

    public static final int PART_INDEX_FULL = 0;
    public static final int PART_INDEX_PREVIEW = 1;
    public static final int PART_INDEX_INFO = 2;

    public static final int PREVIEW_COMPRESSION_QUALITY = 75;
    public static final int PREVIEW_MAX_WIDTH = 512;
    public static final int PREVIEW_MAX_HEIGHT = 512;

    public static MessagePart getInfoPart(Message message) {
        return message.getMessageParts().get(PART_INDEX_INFO);
    }

    public static MessagePart getPreviewPart(Message message) {
        return message.getMessageParts().get(PART_INDEX_PREVIEW);
    }

    public static MessagePart getFullPart(Message message) {
        return message.getMessageParts().get(PART_INDEX_FULL);
    }

    /**
     * Creates a new ThreePartGif Message.  The full gif is attached untouched, while the
     * preview is created from the full gif by loading, resizing, and compressing.
     *
     * @param client the {@link LayerClient}
     * @param result the {@link Result}
     * @return the {@link Message}
     */
    public static Message newThreePartGifMessage(Context context, LayerClient client, Result result) throws IOException {
        if (client == null) throw new IllegalArgumentException("Null LayerClient");
        if (result == null) throw new IllegalArgumentException("Null result");

        MessagePart full = null;
        final String gifUrl = AbstractGifUtils.getGifUrl(result);
        if (!TextUtils.isEmpty(gifUrl)) {
            full = client.newMessagePart(MIME_TYPE_GIF, gifUrl.getBytes());
        }

        MessagePart preview = null;
        final String tinyGifUrl = AbstractGifUtils.getTinyGifUrl(result);
        if (!TextUtils.isEmpty(tinyGifUrl)) {
            preview = client.newMessagePart(MIME_TYPE_GIF_PREVIEW, tinyGifUrl.getBytes());
        }

        final Media gif = AbstractGifUtils.getMedia(result, AbstractGifUtils.MEDIA_GIF);
        MessagePart info = null;
        if (gif != null) {
            GifInfo gifInfo = new GifInfo();
            gifInfo.contentId = result.getId();
            gifInfo.width = gif.getWidth();
            gifInfo.height = gif.getHeight();
            info = client.newMessagePart(MIME_TYPE_GIF_INFO, AbstractGsonUtils.getInstance().toJson(gifInfo).getBytes());
        }

        MessagePart[] parts = new MessagePart[3];
        parts[PART_INDEX_FULL] = full;
        parts[PART_INDEX_PREVIEW] = preview;
        parts[PART_INDEX_INFO] = info;
        return client.newMessage(parts);
    }
}
