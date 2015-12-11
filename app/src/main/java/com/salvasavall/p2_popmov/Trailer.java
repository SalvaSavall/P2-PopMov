package com.salvasavall.p2_popmov;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    String name;
    String key;

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
    }

    public static final Parcelable.Creator<Trailer> CREATOR =
            new Parcelable.Creator<Trailer>() {

                @Override
                public Trailer createFromParcel(Parcel source) {
                    return new Trailer(source);
                }

                @Override
                public Trailer[] newArray(int size) {
                    return new Trailer[0];
                }
            };

    private Trailer(Parcel source) {
        this.name = source.readString();
        this.key = source.readString();
    }
}
