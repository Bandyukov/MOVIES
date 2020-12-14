package com.example.movies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {
    private static final String API_KEY = "c4711720f8c269eff09b8acc50d352a8",
            BASE_URL = "https://api.themoviedb.org/3/discover/movie",
            BASE_URL_VIDEO = "https://api.themoviedb.org/3/movie/%s/videos",
            BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews",
            PARAMS_API_KEY = "api_key", PARAMS_SORT_BY = "sort_by", PARAMS_LANGUAGE = "language", PARAMS_PAGE = "page",
            PARAMS_MIN_VOTE_COUNT = "vote_count.gte", MIN_VOTE_COUNT = "1000",
            LANGUAGE_VALUE = "ru-RU", SORT_BY_POPULARITY = "popularity.desc", SORT_BY_TOP_RATED = "vote_average.desc";

    public static final int popularity = 0, top_rated = 1;

    public static URL buildURL(int sortBy, int page) {
        URL result = null;
        String METHOD_OF_SORT;
        if (sortBy == popularity)
            METHOD_OF_SORT = SORT_BY_POPULARITY;
        else
            METHOD_OF_SORT = SORT_BY_TOP_RATED;

        Uri uri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, METHOD_OF_SORT)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page)).build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static URL buildURLToVideo(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEO, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildURLToReviews(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        URL url = buildURL(sortBy, page);
        JSONObject result = null;
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForVideo(int id) {
        URL url = buildURLToVideo(id);
        JSONObject result = null;
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForReviews(int id) {
        URL url = buildURLToReviews(id);
        JSONObject result = null;
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null)
                onStartLoadingListener.onStartLoading();
            forceLoad();

        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null)
                return  null;
            String urlAsAString = bundle.getString("url");
            URL url = null;

            try {
                url = new URL(urlAsAString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            JSONObject object = null;

            if (url == null)
                return null;

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                StringBuilder result = new StringBuilder();

                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }

                object = new JSONObject(result.toString());
                return object;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

            return null;
        }
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject object = null;

            if (urls == null || urls.length == 0)
                return null;

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                StringBuilder result = new StringBuilder();

                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }

                object = new JSONObject(result.toString());
                return object;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

            return null;
        }
    }
}
