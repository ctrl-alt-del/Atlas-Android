package com.layer.atlas.tenor.adapters;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.atlas.R;
import com.layer.atlas.tenor.rvholders.GifSelectionViewHolder;
import com.layer.atlas.tenor.rvitem.ResultRVItem;
import com.layer.atlas.tenor.threepartgif.GifLoaderClient;
import com.layer.atlas.tenor.threepartgif.GifSender;
import com.tenor.android.core.model.impl.Result;
import com.tenor.android.core.util.AbstractListUtils;
import com.tenor.android.core.view.IBaseView;
import com.tenor.android.core.widget.adapter.AbstractRVItem;
import com.tenor.android.core.widget.adapter.ListRVAdapter;
import com.tenor.android.core.widget.viewholder.StaggeredGridLayoutItemViewHolder;

import java.util.List;
import java.util.Map;

public class GifAdapter<CTX extends IBaseView> extends ListRVAdapter<CTX, AbstractRVItem, StaggeredGridLayoutItemViewHolder<CTX>> {

    private static int ITEM_HEIGHT;
    public final static int TYPE_GIF_ITEM = 0;
    private Map<String, Integer> mWidths;
    private GifSender mGifSender;
    private final GifLoaderClient mGifLoaderClient;
    private final GifLoaderClient.Callback mGifLoaderClientCallback;
    private OnDismissPopupWindowListener mListener;

    public GifAdapter(CTX context, GifLoaderClient gifLoaderClient, GifLoaderClient.Callback callback) {
        super(context);
        mGifLoaderClient = gifLoaderClient;
        mGifLoaderClientCallback = callback;
        mWidths = new ArrayMap<>();
        if (hasContext()) {
            ITEM_HEIGHT = (int) getContext().getResources().getDimension(R.dimen.tenor_gif_adapter_height);
        }
    }

    public void setDismissPopupWindowListener(@Nullable final OnDismissPopupWindowListener listener) {
        mListener = listener;
    }

    @Override
    public StaggeredGridLayoutItemViewHolder<CTX> onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            default:
                view = inflater.inflate(R.layout.tenor_gif_item, null);
                return new GifSelectionViewHolder<>(view, getRef(),
                        mGifLoaderClient, mGifSender, mListener);
        }
    }

    public void setGifSender(GifSender gifSender) {
        mGifSender = gifSender;
    }

    @Override
    public void onBindViewHolder(final StaggeredGridLayoutItemViewHolder<CTX> viewHolder, int position) {

        if (viewHolder instanceof GifSelectionViewHolder) {
            final GifSelectionViewHolder holder = (GifSelectionViewHolder) viewHolder;

            if (getList().get(position).getType() != TYPE_GIF_ITEM) {
                return;
            }

            final ResultRVItem resultRVItem = (ResultRVItem) getList().get(position);

            holder.setImage(resultRVItem.getResult(), mGifLoaderClientCallback);
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
