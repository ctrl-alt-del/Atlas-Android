package com.layer.atlas.tenor.adapter;


import com.layer.atlas.tenor.messagetype.threepartgif.GifSender;

public interface IGifAdapter {
    void setOnSendGifListener(OnSendGifListener listener);

    void setGifSender(GifSender sender);

    void setQuery(String query);
}
