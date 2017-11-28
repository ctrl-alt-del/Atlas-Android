package com.layer.atlas.tenor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layer.atlas.AtlasAvatar;
import com.layer.atlas.R;
import com.layer.atlas.adapters.AtlasBaseAdapter;
import com.layer.atlas.adapters.AtlasConversationsAdapter;
import com.layer.atlas.messagetypes.AtlasCellFactory;
import com.layer.atlas.messagetypes.generic.GenericCellFactory;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.singlepartimage.SinglePartImageCellFactory;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.atlas.tenor.threepartgif.GifLoaderClient;
import com.layer.atlas.tenor.threepartgif.ThreePartGifCellFactory;
import com.layer.atlas.util.ConversationFormatter;
import com.layer.atlas.util.ConversationStyle;
import com.layer.atlas.util.IdentityRecyclerViewEventListener;
import com.layer.atlas.util.Log;
import com.layer.atlas.util.Util;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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