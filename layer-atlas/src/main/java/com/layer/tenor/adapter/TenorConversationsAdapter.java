package com.layer.tenor.adapter;

import android.content.Context;

import com.layer.atlas.adapters.AtlasConversationsAdapter;
import com.layer.tenor.messagetype.gif.GifLoaderClient;
import com.layer.tenor.messagetype.threepartgif.ThreePartGifCellFactory;
import com.layer.atlas.util.ConversationFormatter;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;

import java.util.Collection;

public class TenorConversationsAdapter extends AtlasConversationsAdapter {
    protected final GifLoaderClient mGifLoaderClient;

    public TenorConversationsAdapter(Context context, LayerClient client, Picasso picasso,
                                     GifLoaderClient gifLoaderClient,
                                     ConversationFormatter conversationFormatter) {
        this(context, client, picasso, gifLoaderClient, null, conversationFormatter);
    }

    public TenorConversationsAdapter(Context context, LayerClient client, Picasso picasso,
                                     GifLoaderClient gifLoaderClient,
                                     Collection<String> updateAttributes, ConversationFormatter conversationFormatter) {
        super(context, client, picasso, updateAttributes, conversationFormatter);
        mGifLoaderClient = gifLoaderClient;
        addDefaultCellFactories(new ThreePartGifCellFactory(mLayerClient, mGifLoaderClient));
    }
}