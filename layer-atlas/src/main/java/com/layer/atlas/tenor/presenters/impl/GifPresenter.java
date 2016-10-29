package com.layer.atlas.tenor.presenters.impl;

import android.text.TextUtils;

import com.layer.atlas.tenor.presenters.IGifPresenter;
import com.layer.atlas.tenor.views.IKeyboardView;
import com.tenor.android.core.constants.StringConstant;
import com.tenor.android.core.networks.ApiClient;
import com.tenor.android.core.presenters.impl.BasePresenter;
import com.tenor.android.core.responses.BaseError;
import com.tenor.android.core.responses.GifsResponse;
import com.tenor.android.core.responses.WeakViewCallback;
import com.tenor.android.core.utils.AbstractListUtils;
import com.tenor.android.core.utils.AbstractLocaleUtils;
import com.tenor.android.core.utils.AbstractSessionUtils;

import retrofit2.Call;

public class GifPresenter extends BasePresenter<IKeyboardView> implements IGifPresenter {


    public GifPresenter(IKeyboardView view) {
        super(view);
    }

    @Override
    public Call<GifsResponse> search(String query, String locale, int limit, String pos, String type, final boolean isAppend) {

        final String qry = !TextUtils.isEmpty(query) ? query : StringConstant.EMPTY;

        Call<GifsResponse> call = ApiClient.getInstance(getView().getContext()).search(ApiClient.getApiKey(),
                qry, AbstractLocaleUtils.getCurrentLocaleName(getView().getContext()),
                AbstractSessionUtils.getKeyboardId(getView().getContext()), limit, pos);

        call.enqueue(new WeakViewCallback<GifsResponse>(getView()) {
            @Override
            public void success(GifsResponse response) {
                if (!isAlive()) {
                    return;
                }
                getView().onReceiveSearchResultsSucceed(response, isAppend);
            }

            @Override
            public void failure(BaseError error) {
                if (!isAlive()) {
                    return;
                }
                getView().onReceiveSearchResultsFailed(error);
            }
        });
        return call;
    }

    @Override
    public Call<GifsResponse> getTrending(int limit, String pos, String type, final boolean isAppend) {
        Call<GifsResponse> call = ApiClient.getInstance(getView().getContext()).getTrending(ApiClient.getApiKey(),
                limit, !TextUtils.isEmpty(pos) ? pos : StringConstant.EMPTY, type,
                AbstractLocaleUtils.getCurrentLocaleName(getView().getContext()),
                AbstractSessionUtils.getKeyboardId(getView().getContext()));

        call.enqueue(new WeakViewCallback<GifsResponse>(getView()) {
            @Override
            public void success(GifsResponse response) {
                if (!isAlive()) {
                    return;
                }

                if (response == null || (!isAppend && AbstractListUtils.isEmpty(response.getResults()))) {
                    // TODO: getView().onReceiveTrendingFailed();
                    return;
                }
                getView().onReceiveTrendingSucceeded(response.getResults(), response.getNext(), isAppend);
            }

            @Override
            public void failure(BaseError error) {
                if (!isAlive()) {
                    return;
                }
                getView().onReceiveTrendingFailed(error);
            }
        });
        return call;
    }
}
