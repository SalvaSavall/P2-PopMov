package com.salvasavall.p2_popmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private String shareTrailerString;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<Trailer> arrTrailer;

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
            arrTrailer = movie.getTrailers();
            for(int i=0; i< arrTrailer.size(); i++) {
                View viewTrailer = inflater.inflate(R.layout.trailer_item, null);

                TextView textView = (TextView) viewTrailer.findViewById(R.id.textTrailer);
                textView.setText(arrTrailer.get(i).getName());

                ImageView imageTrailer = (ImageView) viewTrailer.findViewById(R.id.imageTrailer);
                imageTrailer.setOnClickListener(new PlayListener(arrTrailer.get(i).getKey()));

                layoutDetailTrailers.addView(viewTrailer);

                //Add text for share
                if(i==0) {
                    shareTrailerString = "http://www.youtube.com/watch?v=" + arrTrailer.get(i).getKey();
                }
            }
        }

        return rootView;
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareTrailerString);

        return shareIntent;
    }
}
