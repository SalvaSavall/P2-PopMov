package com.salvasavall.p2_popmov;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    String author;
    String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<Review> CREATOR =
            new Parcelable.Creator<Review>() {

                @Override
                public Review createFromParcel(Parcel source) {
                    return new Review(source);
                }

                @Override
                public Review[] newArray(int size) {
                    return new Review[0];
                }
            };

    private Review(Parcel source) {
        this.author = source.readString();
        this.content = source.readString();
    }
}
