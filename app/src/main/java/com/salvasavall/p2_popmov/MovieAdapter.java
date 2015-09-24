package com.salvasavall.p2_popmov;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
 * Created by Salva on 26/08/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context mContext;

    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView posterView;
        Movie movie = getItem(position);

        if (convertView == null) {
            posterView = new ImageView(mContext);
            posterView.setAdjustViewBounds(true);
            posterView.setPadding(0, 0, 0, 0);
            posterView.setMinimumHeight(1);
        } else {
            posterView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(movie.getPoster())
                .placeholder(R.drawable.ic_sync_black_24dp)
                .error(R.drawable.ic_image_error)
                .into(posterView);

        return posterView;
    }
}

