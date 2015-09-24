package com.salvasavall.p2_popmov;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Salva on 26/08/2015.
 */
public class Movie implements Parcelable {

    int id;
    String title, synopsis, release, poster;
    Double rating;

    private final int posterSize = 185;
    private final int thumbnailSize = 154;

    public Movie(int id, String title, String synopsis, String release,
                 String poster, Double rating) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.release = release;
        this.poster = poster;
        this.rating = rating;
    }

    public Movie() {
    }

    public String getTitle() { return title; }

    public String getPoster() {
        return getImage(posterSize);
    }

    public String getThumbnail() {
        return getImage(thumbnailSize);
    }

    private String getImage(int size) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(size) + "/" + poster;
    }

    public String getSynopsis() { return synopsis; }
    public String getRelease() { return release; }
    public double getRating() { return rating; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(release);
        dest.writeString(poster);
        dest.writeDouble(rating);
    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>() {

                @Override
                public Movie createFromParcel(Parcel source) {
                    return new Movie(source);
                }

                @Override
                public Movie[] newArray(int size) {
                    return new Movie[0];
                }
            };

    private Movie(Parcel source) {
        this.title = source.readString();
        this.synopsis = source.readString();
        this.release = source.readString();
        this.poster = source.readString();
        this.rating = source.readDouble();
    }
}
