package com.layer.atlas.tenor.rvholders;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.layer.atlas.R;
import com.layer.atlas.tenor.adapters.OnDismissPopupWindowListener;
import com.layer.atlas.tenor.threepartgif.GifLoaderClient;
import com.layer.atlas.tenor.threepartgif.GifSender;
import com.layer.atlas.tenor.threepartgif.ThreePartGifUtils;
import com.tenor.android.core.constant.MediaCollectionFormat;
import com.tenor.android.core.model.impl.Media;
import com.tenor.android.core.model.impl.MediaCollection;
import com.tenor.android.core.model.impl.Result;
import com.tenor.android.core.util.AbstractListUtils;
import com.tenor.android.core.view.IBaseView;
import com.tenor.android.core.widget.viewholder.StaggeredGridLayoutItemViewHolder;

import java.util.List;

public class GifSelectionViewHolder<CTX extends IBaseView> extends StaggeredGridLayoutItemViewHolder<CTX> {

    private final ImageView mImageView;
    private final GifLoaderClient mGifLoaderClient;

    private Result mResult;
    private GifSender mGifSender;
    private OnDismissPopupWindowListener mListener;

    public GifSelectionViewHolder(View itemView, CTX context,
                                  @NonNull GifLoaderClient gifLoaderClient,
                                  @Nullable final GifSender gifSender,
                                  @Nullable final OnDismissPopupWindowListener listener) {
        super(itemView, context);
        mGifLoaderClient = gifLoaderClient;
        mGifSender = gifSender;
        mListener = listener;
        mImageView = (ImageView) itemView.findViewById(R.id.tgi_iv_item);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGifSender == null || !hasRef()) {
                    return;
                }
                mGifSender.send(mResult);

                if (mListener != null) {
                    mListener.dismiss();
                }
            }
        });
    }

    public void setImage(@Nullable Result result, GifLoaderClient.Callback callback) {
        if (result == null) {
            return;
        }

        mResult = result;
        // normal load to display
        List<MediaCollection> mediaCollections = result.getMedias();
        if (AbstractListUtils.isEmpty(mediaCollections)) {
            return;
        }


        final Media tinyGif = mediaCollections.get(0).get(MediaCollectionFormat.GIF_TINY);

        final int placeholderColor = Color.parseColor(result.getPlaceholderColorHex());

        final Media media = ThreePartGifUtils.getTinyGif(result);

        if (media != null) {
//            final float density = AbstractUIUtils.getScreenDensity(getActivity());
//            payload.setWidth(Math.round(media.getWidth() * density));
//            payload.setHeight(Math.round(media.getHeight() * density));
            mGifLoaderClient.load(mImageView, media.getUrl(), callback);
        }
    }
}
