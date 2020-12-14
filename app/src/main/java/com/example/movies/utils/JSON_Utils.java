package com.example.movies.utils;

import com.example.movies.data.models.Movie;
import com.example.movies.data.models.Review;
import com.example.movies.data.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSON_Utils {
    /*
    private int id, voteCount;
    private String title, originalTitle, overview, posterPath, backdropPath, releaseDate;
    private double voteAverage;\
    */

    private static final String KEY_RESULT = "results", KEY_VOTE_COUNT = "vote_count", KEY_ID = "id",
            KEY_TITLE = "title", KEY_ORIGINAL_TITLE = "original_title", KEY_OVERVIEW = "overview",
            KEY_POSTER_PATH = "poster_path", KEY_BACKDROP_PATH = "backdrop_path", KET_VOTE_AVERAGE = "vote_average",
            KEY_RELEASE_DATE = "release_date", KEY_AUTHOR = "author", KEY_CONTENT = "content",
            KEY_OF_VIDEO = "key", KEY_NAME = "name",
            BASE_POSTER_URL = "https://image.tmdb.org/t/p/",
            SMALL_POSTER_SIZE = "w185", BIG_POSTER_SIZE = "w780",
            BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();

        if (jsonObject == null)
            return result;

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);

                int id = objectMovie.getInt(KEY_ID), voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE), originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE),
                        overview = objectMovie.getString(KEY_OVERVIEW),
                        posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH),
                        bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH),
                        backdropPath = objectMovie.getString(KEY_BACKDROP_PATH), releaseDate = objectMovie.getString(KEY_RELEASE_DATE);
                double voteAverage = objectMovie.getDouble(KET_VOTE_AVERAGE);

                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath,
                        bigPosterPath, backdropPath, releaseDate, voteAverage);
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();

        if (jsonObject == null)
            return result;

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1Review = jsonArray.getJSONObject(i);
                String author = jsonObject1Review.getString(KEY_AUTHOR),
                        content =  jsonObject1Review.getString(KEY_CONTENT);
                Review review = new Review(author, content);
                result.add(review);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();

        if (jsonObject == null)
            return result;

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1Trailer = jsonArray.getJSONObject(i);
                String key = BASE_YOUTUBE_URL + jsonObject1Trailer.getString(KEY_OF_VIDEO),
                        name = jsonObject1Trailer.getString(KEY_NAME);

                Trailer trailer = new Trailer(key, name);
                result.add(trailer);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
