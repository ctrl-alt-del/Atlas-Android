package com.layer.atlas.tenor.presenters;

import com.layer.atlas.tenor.views.IKeyboardView;
import com.tenor.android.sdk.presenters.IBasePresenter;
import com.tenor.android.sdk.responses.GifsResponse;

import retrofit2.Call;


public interface IGifPresenter extends IBasePresenter<IKeyboardView> {
    Call<GifsResponse> search(String query, String locale, int limit, String pos, String type, boolean isAppend);

    Call<GifsResponse> getTrending(int limit, String pos, String type, boolean isAppend);
}
