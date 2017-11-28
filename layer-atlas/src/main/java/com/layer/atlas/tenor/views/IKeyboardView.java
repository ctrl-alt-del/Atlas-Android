package com.layer.atlas.tenor.views;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tenor.android.core.model.impl.Result;
import com.tenor.android.core.presenter.IBasePresenter;
import com.tenor.android.core.response.BaseError;
import com.tenor.android.core.response.impl.GifsResponse;
import com.tenor.android.core.view.IBaseView;

import java.util.List;

public interface IKeyboardView extends IBaseView {

    interface Presenter extends IBasePresenter<IKeyboardView> {
        void search(String query, String locale, int limit, String pos, String type, boolean isAppend);

        void getTrending(int limit, String pos, String type, boolean isAppend);
    }

    void onReceiveSearchResultsSucceed(@NonNull GifsResponse response, boolean isAppend);

    void onReceiveSearchResultsFailed(@Nullable BaseError error);

    void onReceiveTrendingSucceeded(@NonNull List<Result> list, @NonNull String nextPageId, boolean isAppend);

    void onReceiveTrendingFailed(@Nullable BaseError error);
}
