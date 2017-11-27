package com.layer.atlas.tenor.messagetypes.text;

import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.tenor.SmartGifsUtils;
import com.layer.sdk.messaging.Message;

public class TenorTextCellFactory extends TextCellFactory {

    @Override
    public void bindCellHolder(CellHolder cellHolder, final TextInfo parsed, Message message, CellHolderSpecs specs) {
        super.bindCellHolder(cellHolder, parsed, message, specs);
        String textMessage = parsed.getString();
        SmartGifsUtils.update(textMessage, specs.position);
    }
}
