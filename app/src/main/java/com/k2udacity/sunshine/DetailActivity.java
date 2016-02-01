package com.k2udacity.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

// TODO Inject data by ButterKnife
public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private String mForecastStr;
    //UI Elements
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.movie_detail_title) TextView tvMovieTitle;
    @Bind(R.id.movie_detail_description) TextView tvMovieDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if(null == intent || !intent.hasExtra("movie")){
            Toast.makeText(DetailActivity.this,"Unable to load the movie", Toast.LENGTH_LONG);
            return;
        }

        Movie movie = (Movie) intent.getSerializableExtra("movie");
        Log.i(LOG_TAG, movie.toString());
        ImageView imageView = (ImageView)findViewById(R.id.movie_detail_image);
        Picasso.with(getBaseContext())
                .load(movie.getImageUrl())
//                .fit()
                .into(imageView);
        tvMovieTitle.setText(movie.getOriginalTitle());
//        TextView tvDesc = ((TextView) findViewById(R.id.movie_detailr_description));
        tvMovieDesc.setText(movie.getOverview());
        tvMovieDesc.setMovementMethod(new ScrollingMovementMethod());

        String year = (String) android.text.format.DateFormat.format("yyyy", movie.getReleaseDate()); //2013
        ((TextView) findViewById(R.id.movie_detail_release_yr))
                .setText(year);
        ((TextView) findViewById(R.id.movie_detail_duration))
                .setText("120min");
        ((TextView) findViewById(R.id.movie_detail_rating))
                .setText(movie.getRating()+"/10");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //TODO Uncomment below to Disabling the share intent
//        getMenuInflater().inflate(R.menu.menu_detail, menu);

//        MenuItem menuItem = menu.findItem(R.id.action_share);
//        android.support.v7.widget.ShareActionProvider mShareActionProvider = ( android.support.v7.widget.ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);
//        if(mShareActionProvider!=null){
//            mShareActionProvider.setShareIntent(createShareForecastIntent());
//        }else{
//            Log.d(LOG_TAG,"Share action provider is null");
//        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_share) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr+FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }


}
