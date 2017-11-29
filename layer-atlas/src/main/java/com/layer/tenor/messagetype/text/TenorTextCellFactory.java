package com.layer.tenor.messagetype.text;

import android.support.v7.widget.RecyclerView;

import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.tenor.util.GifSearchQueryClerk;
import com.layer.sdk.messaging.Message;

public class TenorTextCellFactory extends TextCellFactory {

    private int mTextCellPosition = RecyclerView.NO_POSITION;

    @Override
    public void bindCellHolder(CellHolder cellHolder, final TextInfo parsed, Message message, CellHolderSpecs specs) {
        super.bindCellHolder(cellHolder, parsed, message, specs);
        String textMessage = parsed.getString();
        if (specs.position > mTextCellPosition) {
            // there is newer message
            GifSearchQueryClerk.get().update(textMessage);
            mTextCellPosition = specs.position;
        }
    }
}
