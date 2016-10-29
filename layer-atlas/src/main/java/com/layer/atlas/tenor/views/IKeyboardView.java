package com.layer.atlas.tenor.views;


import com.tenor.android.core.models.Result;
import com.tenor.android.core.responses.BaseError;
import com.tenor.android.core.responses.GifsResponse;
import com.tenor.android.core.views.IBaseView;

import java.util.List;

public interface IKeyboardView extends IBaseView {
    void onReceiveSearchResultsSucceed(GifsResponse response, boolean isAppend);

    void onReceiveSearchResultsFailed(BaseError error);

    void onReceiveTrendingSucceeded(List<Result> list, String nextPageId, boolean isAppend);

    void onReceiveTrendingFailed(BaseError error);
}
