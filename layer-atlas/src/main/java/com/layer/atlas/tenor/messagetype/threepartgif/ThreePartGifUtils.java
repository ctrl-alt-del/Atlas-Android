package com.layer.atlas.tenor.messagetype.threepartgif;

import android.text.TextUtils;

import com.layer.atlas.tenor.model.IMinimalResult;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.io.IOException;

public class ThreePartGifUtils {

    public static final String MIME_TYPE_GIF = "image/gif";
    public static final String MIME_TYPE_GIF_PREVIEW = "image/gif+preview";
    public static final String MIME_TYPE_GIF_INFO = "application/json+gifSize";

    public static final int PART_INDEX_FULL = 0;
    public static final int PART_INDEX_PREVIEW = 1;
    public static final int PART_INDEX_INFO = 2;

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
     * @param result the {@link IMinimalResult}
     * @return the {@link Message}
     */
    public static Message newThreePartGifMessage(LayerClient client, IMinimalResult result) throws IOException {
        if (client == null) throw new IllegalArgumentException("Null LayerClient");
        if (result == null) throw new IllegalArgumentException("Null result");

        MessagePart full = null;
        if (!TextUtils.isEmpty(result.getUrl())) {
            full = client.newMessagePart(MIME_TYPE_GIF, result.getUrl().getBytes());
        }

        MessagePart preview = null;
        if (!TextUtils.isEmpty(result.getPreviewUrl())) {
            preview = client.newMessagePart(MIME_TYPE_GIF_PREVIEW, result.getPreviewUrl().getBytes());
        }

        GifInfo gifInfo = new GifInfo();
        gifInfo.contentId = result.getId();
        gifInfo.previewPartId = result.getPreviewUrl();
        gifInfo.fullPartId = result.getUrl();
        gifInfo.width = result.getWidth();
        gifInfo.height = result.getHeight();
        MessagePart info = buildInfoMessagePart(client, gifInfo);

        MessagePart[] parts = new MessagePart[3];
        parts[PART_INDEX_FULL] = full;
        parts[PART_INDEX_PREVIEW] = preview;
        parts[PART_INDEX_INFO] = info;
        return client.newMessage(parts);
    }

    private static MessagePart buildInfoMessagePart(LayerClient client, GifInfo info) throws IOException {

        String intoString = "{"
                + "\"orientation\":" + info.orientation + ", "
                + "\"width\":" + info.width + ", "
                + "\"height\":" + info.height + ", "
                + "\"contentId\": \"" + info.contentId + "\", "
                + "\"fullPartId\": \"" + info.fullPartId + "\", "
                + "\"previewPartId\": \"" + info.previewPartId + "\""
                + "}";

        if (Log.isLoggable(Log.VERBOSE)) {
            Log.v("Creating gif info: " + intoString);
        }

        return client.newMessagePart(MIME_TYPE_GIF_INFO, intoString.getBytes());
    }
}
