package com.layer.tenor.messagetype.threepartgif;

import android.os.Parcel;
import android.os.Parcelable;

import com.layer.atlas.messagetypes.AtlasCellFactory;

public class GifInfo implements AtlasCellFactory.ParsedContent, Parcelable {

    public int orientation;
    public int width;
    public int height;
    public String contentId;
    public String fullPartId;
    public String previewPartId;

    @Override
    public int sizeOf() {

        int contentIdSize = 0;
        if (contentId != null) {
            contentIdSize = contentId.getBytes().length;
        }

        int fullPartIdSize = 0;
        if (fullPartId != null) {
            fullPartIdSize = fullPartId.getBytes().length;
        }

        int previewPartIdSize = 0;
        if (previewPartId != null) {
            previewPartIdSize = previewPartId.getBytes().length;
        }

        return ((Integer.SIZE + Integer.SIZE + Integer.SIZE) / Byte.SIZE) + contentIdSize
                + fullPartIdSize + previewPartIdSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orientation);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(contentId);
        dest.writeString(fullPartId);
        dest.writeString(previewPartId);
    }

    public static final Parcelable.Creator<GifInfo> CREATOR
            = new Parcelable.Creator<GifInfo>() {
        public GifInfo createFromParcel(Parcel in) {
            GifInfo info = new GifInfo();
            info.orientation = in.readInt();
            info.width = in.readInt();
            info.height = in.readInt();
            info.contentId = in.readString();
            info.fullPartId = in.readString();
            info.previewPartId = in.readString();
            return info;
        }

        public GifInfo[] newArray(int size) {
            return new GifInfo[size];
        }
    };
}
