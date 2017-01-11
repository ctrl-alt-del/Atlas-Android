package com.layer.atlas.tenor.adapters;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.atlas.R;
import com.layer.atlas.tenor.rvholders.GifItemRVVH;
import com.layer.atlas.tenor.rvitem.ResultRVItem;
import com.layer.atlas.tenor.threepartgif.GifSender;
import com.tenor.android.sdk.models.Result;
import com.tenor.android.sdk.rvwidgets.AbstractRVItem;
import com.tenor.android.sdk.rvwidgets.ListRVAdapter;
import com.tenor.android.sdk.rvwidgets.StaggeredGridLayoutItemViewHolder;
import com.tenor.android.sdk.utils.AbstractListUtils;

import java.util.List;
import java.util.Map;

public class GifAdapter<CTX> extends ListRVAdapter<CTX, AbstractRVItem, StaggeredGridLayoutItemViewHolder<CTX>> {

    private static int ITEM_HEIGHT;
    public final static int TYPE_GIF_ITEM = 0;
    private Map<String, Integer> mWidths;
    private GifSender mGifSender;
    private OnDismissPopupWindowListener mListener;

    public GifAdapter(CTX context) {
        super(context);
        mWidths = new ArrayMap<>();
        if (hasContext()) {
            ITEM_HEIGHT = (int) getContext().getResources().getDimension(R.dimen.tenor_gif_adapter_height);
        }
    }

    public GifAdapter<CTX> setDismissPopupWindowListener(@Nullable final OnDismissPopupWindowListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public StaggeredGridLayoutItemViewHolder<CTX> onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            default:
                view = inflater.inflate(R.layout.tenor_gif_item_rvvh, null);
                return new GifItemRVVH<>(view, getCTX(), mGifSender, mListener);
        }
    }

    public void setGifSender(GifSender gifSender) {
        mGifSender = gifSender;
    }

    @Override
    public void onBindViewHolder(final StaggeredGridLayoutItemViewHolder<CTX> viewHolder, int position) {

        if (viewHolder instanceof GifItemRVVH) {
            final GifItemRVVH holder = (GifItemRVVH) viewHolder;

            if (getList().get(position).getType() != TYPE_GIF_ITEM) {
                return;
            }

            final ResultRVItem resultRVItem = (ResultRVItem) getList().get(position);

            holder.setImage(resultRVItem.getResult());
            if (mWidths.containsKey(resultRVItem.getId())) {
                holder.setParams(mWidths.get(resultRVItem.getId()), ITEM_HEIGHT);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getList().get(position).getType();
    }

    @Override
    public int getItemCount() {
        return getList().size();
    }

    @Override
    public void insert(@Nullable List<AbstractRVItem> list, boolean isAppend) {
        if (AbstractListUtils.isEmpty(list)) {
            notifyDataSetChanged();
            return;
        }

        if (!isAppend) {
            getList().clear();
            mWidths.clear();
        }

        getList().addAll(list);
        for (AbstractRVItem item : list) {
            cacheItemWidth(item);
        }

        if (!isAppend) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeChanged(getItemCount(), list.size());
        }
    }

    protected boolean cacheItemWidth(AbstractRVItem item) {
        if (item == null || item.getType() != TYPE_GIF_ITEM) {
            return false;
        }

        final Result result = ((ResultRVItem) item).getResult();
        if (!mWidths.containsKey(result.getId())) {
            mWidths.put(result.getId(), (int) (ITEM_HEIGHT * result.getAspectRatio()));
        }
        return false;
    }
}
