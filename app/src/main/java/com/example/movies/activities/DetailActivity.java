package com.example.movies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies.R;
import com.example.movies.activities.FavoriteActivity;
import com.example.movies.activities.MainActivity;
import com.example.movies.adapters.ReviewAdapter;
import com.example.movies.adapters.TrailerAdapter;
import com.example.movies.data.models.FavoriteMovie;
import com.example.movies.data.MainViewModel;
import com.example.movies.data.models.Movie;
import com.example.movies.data.models.Review;
import com.example.movies.data.models.Trailer;
import com.example.movies.utils.JSON_Utils;
import com.example.movies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster, imageViewStar;
    private TextView textViewTitle, textViewOriginalTitle, textViewRating, textViewReleaseDate, textViewOverview;
    private int id;
    private MainViewModel viewModel;
    private Movie movie;
    private FavoriteMovie favoriteMovie;
    private RecyclerView recyclerViewTrailers, recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewStar = findViewById(R.id.imageViewAddToFavorites);
        textViewTitle = findViewById(R.id.textViewTile);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollView = findViewById(R.id.scrollViewDetailActivity);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id"))
            id = intent.getIntExtra("id", 0);
        else
            finish();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(String.format(Locale.getDefault(), "%s", movie.getVoteAverage()));

        setFavorite();

        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailer);

        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();

        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);

        JSONObject jsonObjectTrailer = NetworkUtils.getJSONForVideo(movie.getId());
        JSONObject jsonObjectReview = NetworkUtils.getJSONForReviews(movie.getId());

        ArrayList<Trailer> trailers = JSON_Utils.getTrailersFromJSON(jsonObjectTrailer);
        ArrayList<Review> reviews = JSON_Utils.getReviewsFromJSON((jsonObjectReview));

        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentShowTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentShowTrailer);
            }
        });
        scrollView.smoothScrollTo(0, 0);
    }

    public void onClickChangeFavorites(View view) {
        if (favoriteMovie == null) {
            viewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            Toast.makeText(this, "Добавленно в изранное", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavoriteMovie(favoriteMovie);
            Toast.makeText(this, "Удаленно из избранного", Toast.LENGTH_SHORT).show();
        }
        setFavorite();
    }

    public void setFavorite() {
        favoriteMovie = viewModel.getFavoriteMovieById(id);
        if (favoriteMovie == null)
            imageViewStar.setImageResource(R.drawable.favourite_add_to);
        else
            imageViewStar.setImageResource(R.drawable.favourite_remove);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFavorite = new Intent(this, FavoriteActivity.class);
                startActivity(intentToFavorite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}