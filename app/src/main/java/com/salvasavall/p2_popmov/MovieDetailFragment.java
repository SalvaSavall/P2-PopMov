package com.salvasavall.p2_popmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");

            TextView textTitle = (TextView) rootView.findViewById(R.id.textTitle);
            textTitle.setText(movie.getTitle());

            ImageView imageThumbnail = (ImageView) rootView.findViewById(R.id.imageThumbnail);
            Picasso.with(getContext()).load(movie.getThumbnail()).into(imageThumbnail);

            TextView textSynopsis = (TextView) rootView.findViewById(R.id.textSynopsis);
            textSynopsis.setText(movie.getSynopsis());

            TextView textReleaseDate = (TextView) rootView.findViewById(R.id.textReleaseDate);
            textReleaseDate.setText(movie.getRelease());

            TextView textRating = (TextView) rootView.findViewById(R.id.textRating);
            textRating.setText(String.valueOf(movie.getRating())+"/10");

            //Trailers views
            LinearLayout layoutDetailTrailers = (LinearLayout) rootView.findViewById(R.id.layoutDetailTrailers);
            for(Trailer t : movie.getTrailers()) {
                View viewTrailer = inflater.inflate(R.layout.trailer_item, null);

                TextView textView = (TextView) viewTrailer.findViewById(R.id.textTrailer);
                textView.setText(t.getName());

                ImageView imageTrailer = (ImageView) viewTrailer.findViewById(R.id.imageTrailer);
                imageTrailer.setOnClickListener(new PlayListener(t.getKey()));

                layoutDetailTrailers.addView(viewTrailer);
            }
        }

        return rootView;
    }
}
