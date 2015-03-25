package com.layer.atlas.atlasdefault;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.layer.atlas.model.AvatarItem;
import com.layer.atlas.adapter.ConversationListAdapter;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;

import java.util.List;
import java.util.Map;

public class DefaultConversationDataSource implements ConversationListAdapter.DataSource {
    public static String METADATA_KEY_CONVERSATION_TITLE = "conversationName";

    private final LayerClient mClient;

    public DefaultConversationDataSource(LayerClient client) {
        mClient = client;
    }

    @Override
    public String getConversationTitle(ConversationListAdapter adapter, Conversation conversation) {
        // Title by metadata
        Map<String, Object> metadata = conversation.getMetadata();
        if (metadata != null && metadata.containsKey(METADATA_KEY_CONVERSATION_TITLE)) {
            return (String) metadata.get(METADATA_KEY_CONVERSATION_TITLE);
        }

        // Title by participants
        List<String> participants = conversation.getParticipants();
        participants.remove(mClient.getAuthenticatedUserId());
        return TextUtils.join(", ", participants);
    }

    @Override
    public AvatarItem getConversationAvatarItem(ConversationListAdapter adapter, final Conversation conversation) {
        return new AvatarItem() {
            @Override
            public Bitmap getAvatarBitmap() {
                return null;
            }

            @Override
            public String getAvatarInitials() {
                return Integer.toString(conversation.getParticipants().size());
            }
        };
    }
}