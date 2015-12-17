package com.salvasavall.p2_popmov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */

public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;
    ArrayList<Movie> movies = new ArrayList<>();
    String oldSortBy = null;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState!=null && savedInstanceState.containsKey("movies")
                && savedInstanceState.containsKey("sortBy")) {
            movies = savedInstanceState.getParcelableArrayList("movies");
            oldSortBy = savedInstanceState.getString("sortBy");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.key_sort_settings),
                getString(R.string.default_sort_settings));
        if(oldSortBy == null || !oldSortBy.equals(sortBy)) {
            updateMovies(sortBy);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                updateMovies(oldSortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        movieAdapter = new MovieAdapter(getActivity(), movies);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieClicked = movies.get(i);
                Intent detailIntent = new Intent(getContext(), MovieDetail.class);
                detailIntent.putExtra("movie", movieClicked);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movies);
        outState.putString("sortBy",oldSortBy);
        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    private void updateMovies(String sortBy) {
        if(isNetworkAvailable()) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(sortBy);
            oldSortBy = sortBy;
        } else {
            Toast.makeText(getActivity(), getString(R.string.message_without_connection), Toast.LENGTH_LONG).show();
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String MDB_RESULTS = "results";
            final String MDB_ID = "id";
            final String MDB_TITLE = "original_title";
            final String MDB_SYNOPSIS = "overview";
            final String MDB_RELEASE = "release_date";
            final String MDB_POSTER = "poster_path";
            final String MDB_RATING = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesJArray = moviesJson.getJSONArray(MDB_RESULTS);

            ArrayList<Movie> movieArr = new ArrayList<>();

            for(int i = 0; i < moviesJArray.length(); i++) {
                JSONObject movieJ = moviesJArray.getJSONObject(i);

                int id;
                String title;
                String synopsis;
                String release;
                String poster;
                Double rating;

                id = movieJ.getInt(MDB_ID);
                title = movieJ.getString(MDB_TITLE);
                synopsis = movieJ.getString(MDB_SYNOPSIS);
                release = movieJ.getString(MDB_RELEASE);
                poster = movieJ.getString(MDB_POSTER);
                rating = movieJ.getDouble(MDB_RATING);

                Movie movie = new Movie(id, title, synopsis, release,
                        poster, rating);

                //Get trailers
                String trailerJsonStr = getTrailerJson(movie.getId());
                ArrayList<Trailer> trailerArr = getTrailerDataFromJson(trailerJsonStr);
                movie.setTrailers(trailerArr);

                movieArr.add(movie);
            }

            return movieArr;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            String sortBy = params[0];
            String sortOrder = ".desc";
            String apiKey = getString(R.string.api_key_moviedb);

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy + sortOrder)
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if(buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if(movies != null) {
                movieAdapter.setNotifyOnChange(false);
                movieAdapter.clear();
                for(Movie movie : movies) {
                    movieAdapter.add(movie);
                }
                movieAdapter.notifyDataSetChanged();
            }
        }

        private String getTrailerJson(int movieId) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailerJsonStr = null;

            String apiKey = getString(R.string.api_key_moviedb);

            try {
                final String TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String VIDEO_PARAM = "/videos?";
                final String API_PARAM = "api_key";

                String base = TRAILER_BASE_URL + Integer.toString(movieId) + VIDEO_PARAM;

                Uri builtUri = Uri.parse(base).buildUpon()
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if(buffer.length() == 0) {
                    return null;
                }
                trailerJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }
                }
            }

            return trailerJsonStr;
        }

        private ArrayList<Trailer> getTrailerDataFromJson(String trailersJsonStr)
                throws JSONException {

            final String TRAILER_RESULTS = "results";
            final String TRAILER_NAME = "name";
            final String TRAILER_KEY = "key";

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray trailersJArray = trailersJson.getJSONArray(TRAILER_RESULTS);

            ArrayList<Trailer> trailerArr = new ArrayList<>();

            for(int i = 0; i < trailersJArray.length(); i++) {
                JSONObject trailerJ = trailersJArray.getJSONObject(i);

                String name;
                String key;

                name = trailerJ.getString(TRAILER_NAME);
                key = trailerJ.getString(TRAILER_KEY);

                trailerArr.add(new Trailer(name, key));
            }

            return trailerArr;
        }
    }
}
