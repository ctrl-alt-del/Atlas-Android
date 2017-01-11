package com.layer.atlas.tenor.presenters.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.layer.atlas.tenor.presenters.IGifPresenter;
import com.layer.atlas.tenor.views.IKeyboardView;
import com.tenor.android.sdk.constants.StringConstant;
import com.tenor.android.sdk.networks.ApiClient;
import com.tenor.android.sdk.presenters.impl.BasePresenter;
import com.tenor.android.sdk.responses.BaseError;
import com.tenor.android.sdk.responses.GifsResponse;
import com.tenor.android.sdk.responses.WeakRefCallback;
import com.tenor.android.sdk.utils.AbstractListUtils;
import com.tenor.android.sdk.utils.AbstractLocaleUtils;
import com.tenor.android.sdk.utils.AbstractSessionUtils;

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

        call.enqueue(new WeakRefCallback<GifsResponse, IKeyboardView>(getWeakRef()) {
            @Override
            public void success(@NonNull IKeyboardView view, GifsResponse response) {
                view.onReceiveSearchResultsSucceed(response, isAppend);
            }

            @Override
            public void failure(@NonNull IKeyboardView view, BaseError error) {
                view.onReceiveSearchResultsFailed(error);
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

        call.enqueue(new WeakRefCallback<GifsResponse, IKeyboardView>(getWeakRef()) {
            @Override
            public void success(@NonNull IKeyboardView view, GifsResponse response) {
                if (response == null || (!isAppend && AbstractListUtils.isEmpty(response.getResults()))) {
                    // TODO: getView().onReceiveTrendingFailed();
                    return;
                }
                view.onReceiveTrendingSucceeded(response.getResults(), response.getNext(), isAppend);
            }

            @Override
            public void failure(@NonNull IKeyboardView view, BaseError error) {
                view.onReceiveTrendingFailed(error);
            }
        });
        return call;
    }
}
