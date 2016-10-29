package com.layer.atlas.tenor.rvholders;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.layer.atlas.R;
import com.layer.atlas.tenor.GlideUtils;
import com.layer.atlas.tenor.adapters.OnDismissPopupWindowListener;
import com.layer.atlas.tenor.threepartgif.GifSender;
import com.tenor.android.core.models.GlidePayload;
import com.tenor.android.core.models.Media;
import com.tenor.android.core.models.Result;
import com.tenor.android.core.rvwidgets.StaggeredGridLayoutItemViewHolder;
import com.tenor.android.core.utils.AbstractGifUtils;
import com.tenor.android.core.utils.AbstractViewUtils;

public class GifItemRVVH<T> extends StaggeredGridLayoutItemViewHolder<T> {

    private final ImageView mImageView;
    private final ImageView mAudioIconIV;

    private Result mResult;
    private GifSender mGifSender;
    private OnDismissPopupWindowListener mListener;

    public GifItemRVVH(View itemView, T context,
                       @Nullable final GifSender gifSender,
                       @Nullable final OnDismissPopupWindowListener listener) {
        super(itemView, context);
        mGifSender = gifSender;
        mListener = listener;
        mImageView = (ImageView) itemView.findViewById(R.id.tgir_iv_trending_item);
        mAudioIconIV = (ImageView) itemView.findViewById(R.id.tgir_iv_audio);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGifSender == null || !hasActivity()) {
                    return;
                }
                mGifSender.send(getActivity(), mResult);

                if (mListener != null) {
                    mListener.dismiss();
                }
            }
        });
    }

    public GifItemRVVH setImage(@Nullable final Result result) {
        if (result == null) {
            return this;
        }

        mResult = result;
        // normal load to display
        final Media tinyGif = AbstractGifUtils.getMedia(result, AbstractGifUtils.MEDIA_TINY_GIF);
        if (tinyGif == null) {
            return this;
        }

        final int placeholderColor = Color.parseColor(result.getPlaceholderColor());

        GlidePayload payload = new GlidePayload(mImageView, tinyGif.getUrl())
                .setPlaceholder(placeholderColor)
                .setMaxRetry(3)
                .setMedia(AbstractGifUtils.getMedia(result, AbstractGifUtils.MEDIA_TINY_GIF));

        GlideUtils.loadGif(getActivity(), payload);

        if (mResult.isHasAudio()) {
            AbstractViewUtils.showView(mAudioIconIV);
        } else {
            AbstractViewUtils.hideView(mAudioIconIV);
        }
        return this;
    }
}
