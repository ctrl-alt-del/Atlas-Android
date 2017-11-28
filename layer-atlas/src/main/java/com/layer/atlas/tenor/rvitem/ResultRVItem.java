package com.layer.atlas.tenor.rvitem;

import android.support.annotation.NonNull;

import com.tenor.android.core.model.impl.Result;
import com.tenor.android.core.widget.adapter.AbstractRVItem;

public class ResultRVItem extends AbstractRVItem {

    private Result mResult;

    public ResultRVItem(int type, @NonNull final Result result) {
        super(type, result.getId());
        mResult = result;
    }

    @NonNull
    public Result getResult() {
        return mResult;
    }
}
