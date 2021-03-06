package com.k2udacity.sunshine;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.k2udacity.movie.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.Bind;

// TODO Inject data by ButterKnife
public class MainActivity extends AppCompatActivity {
    public static final String POPULARITY_DESC = "popularity.desc";
    public static final String VOTE_AVERAGE_DESC = "vote_average.desc";
    private String LOG_TAG = MainActivity.class.getSimpleName();
    //Actual Parameters
    private static final String BASE_URL="https://api.themoviedb.org/3/movie/550?api_key=";
    private static  String API_KEY="";
    private static final String POPULAR_URL="http://api.themoviedb.org/3/discover/movie";
    private boolean isForTablet;

    //UI Related
    GridViewAdapter mGridAdapter ;
    ArrayList<Movie> mGridData;
    GridView mGridView;
    ProgressBar mProgressBar;
    Toolbar toolbar;


    private Movie movie;

    public Movie getMovie() {
        return movie;
    }

    @Override
    protected void onStart(){
        super.onStart();
        updateMovies();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View rootView = (View) findViewById(R.id.content_main);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        //Initialize with empty data
        if(mGridData==null) {
            Log.i(LOG_TAG,"Movies are still null");
            mGridData = new ArrayList<>();
        }
        Fragment fragment = (DetailFragment) getFragmentById(R.id.detail_fragment);
        isForTablet =fragment!=null;
        Log.i(LOG_TAG,"Grid Data Size"+mGridData.size());
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                Movie item = mGridData.get(position);
//                        (Movie) parent.getItemAtPosition(position);
                movie = item;
                View container = (View) findViewById(R.id.fragment_container);

                if (container != null) {
                    Log.d(LOG_TAG, "Fragment is not null");
                    updateFragment3(movie);
                } else {
                    //TODO : Setup the intent activity
                    Intent detailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
                    detailActivityIntent.putExtra("movie", movie);
                    startActivity(detailActivityIntent);
                }

            }
        });
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private String currentSort;
    public void updateMovies(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
         API_KEY = prefs.getString(getString(R.string.pref_api_key),getString(R.string.pref_default_api_key));

        if(mProgressBar!=null){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        FetchMoviesTask movieTask = new FetchMoviesTask();
        String key = POPULARITY_DESC;
        if(null == currentSort || "".equals(currentSort)){
            ;
        }else{
            switch (currentSort){
                case Constants.MOST_POPULAR:
                    key = POPULARITY_DESC;
                    break;
                case Constants.HIGHEST_RATED:
                    key = VOTE_AVERAGE_DESC;
                    break;
            }
        }
        movieTask.execute(POPULAR_URL, API_KEY, "sort_by", key);

    }

    public Fragment getFragmentById(int id){
        FragmentManager fm = getFragmentManager();
        return fm.findFragmentById(id);

    }

    public void updateFragment() {
        Movie movie = mGridData.get(0);
        updateFragment(movie);
    }
    public void updateFragment(Movie movie){
        Log.i(LOG_TAG,"Fetched Movie"+movie.toString());
        Fragment fragment1 = (DetailFragment) getFragmentById(R.id.detail_fragment);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        DetailFragment frg = DetailFragment.newInstance(movie);
        frg.update(movie);
        ft.replace(R.id.fragment_container, frg);
        ft.addToBackStack(null);
        ft.commit();
        ft.show(frg);
        fm.executePendingTransactions();
    }

    public void updateFragment3(Movie movie){
        Log.i(LOG_TAG,"3 Fetched Movie"+movie.toString());
        FragmentManager fm = getFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment1 = (DetailFragment) getFragmentById(R.id.detail_fragment);
        if(fragment1!=null){
            ft.remove(fragment1);
            ft.commit();
            fm.executePendingTransactions();

        }else{
            Fragment fragment = getFragmentManager().findFragmentByTag("search");
            if (fragment instanceof DetailFragment) {
                fragment1 = (DetailFragment) fragment;
                if(fragment1!=null){
                    ft.remove(fragment1);
                    ft.commit();
                    fm.executePendingTransactions();
                }
            }
        }
        DetailFragment frg = DetailFragment.newInstance(movie);
        ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, frg, "search");
        ft.commit();
        ft.show(frg);
        fm.executePendingTransactions();
    }

    public void updateFragment2(Movie movie){
        Log.i(LOG_TAG,"Fetched Movie"+movie.toString());
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        DetailFragment fragment = (DetailFragment) fm.findFragmentById(R.id.detail_fragment);

        if(fragment!=null) {
            fragment.update(movie);
            if (fragment.getView() != null) {
                String data = ((TextView) fragment.getView().findViewById(R.id.movie_detail_rating1)).getText().toString();
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                Log.w(LOG_TAG, "Warningggg:" + data);
            }else{
                Toast.makeText(MainActivity.this, "Null", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_sort, menu);

        MenuItem item = menu.findItem(R.id.action_sort);

        View view = MenuItemCompat.getActionView(item);
        if(view instanceof Spinner) {
            final Spinner spinner = (Spinner) view;
            updateSpinner(spinner);
        }else{
            Log.e(LOG_TAG,"Not Found the spiiner"+view.getClass().getSimpleName());
        }
        mOptionsMenu = menu;
        return true;
    }
    private Menu mOptionsMenu;
    public void updateSpinner(){
        if(mOptionsMenu==null)
            return;
        MenuItem item = mOptionsMenu.findItem(R.id.action_sort);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        updateSpinner(spinner);
    }

    public void updateSpinner(Spinner spinner){
        if(spinner==null)
            return;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.pref_sort_titles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String item = (String)parent.getSelectedItem();
                Toast.makeText(MainActivity.this,item,Toast.LENGTH_LONG);
                Log.e(LOG_TAG, "Selected " + item);
                currentSort = item;
                updateMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if(id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }
        if(id == R.id.action_sort){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_default_api_key));
        Uri geoLocation = Uri.parse("geo:0:0?").buildUpon()
                .appendQueryParameter("q",location)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else{
            Log.d(LOG_TAG, "Couldn't call " + location + " ");
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
        */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
            return format.format(date).toString();
        }
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Movie[] getMovieImageUrls(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String posterPath = "poster_path";
            final String originalTitle = "original_title";
            final String isAdult = "adult";
            final String overview = "overview";
            final String rating = "vote_average";
            final String releaseDate = "release_date";


            final String OWM_DESCRIPTION = "main";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
            Movie[] resultStrs = null;
            resultStrs = new Movie[moviesArray.length()];
            for(int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                Movie m = new Movie();

                // Get the JSON object representing the day
                JSONObject movieObject = moviesArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                m.setPosterPath(movieObject.getString(posterPath));
                m.setOriginalTitle(movieObject.getString(originalTitle));
                m.setIsAdult(movieObject.getBoolean(isAdult));
                m.setOverview(movieObject.getString(overview));
                m.setRating(movieObject.getDouble(rating));
                String dateFormat = "yyyy-mm-dd";
                String releaseDtStr = movieObject.getString(releaseDate);
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                Date date = null;
                try {
                    date = format.parse(releaseDtStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                m.setReleaseDate(date);
//                Log.d(LOG_TAG,m.toString());
                resultStrs[i] =m;
                mGridData.add(m);
                if(i==0){
                    movie = m;
                }


            }
            for (Movie m: resultStrs){
                Log.v(LOG_TAG, m.toString());
            }
            return resultStrs;
        }
        public String formatHighLows(double high, double low) {
            return ("");
        }


        @Override
        public Movie[] doInBackground(String... params) {

            //If there is no Zip Code.
            if(params.length==0){
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format="json";

            int numDays=7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL=params[0];
                final String QUERY_API="api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_API, params[1]).build();
                for(int i = 2;i<params.length;i+=2){
                    builtUri = builtUri.buildUpon().appendQueryParameter(params[i],params[i+1]).build();
                }

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI"+ builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG,"Forecast JSON String" + forecastJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try{
                return getMovieImageUrls(forecastJsonStr, numDays);
//                return forecastArray;
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }
            return null;

        }
        @Override
        public void onPostExecute(Movie[] result) {
            if(result != null){
                Log.i(LOG_TAG,"Size of movie arr"+result.length);
                mGridData = new ArrayList<Movie>(Arrays.asList(result));
                mGridAdapter.setGridData(mGridData);
                mGridAdapter.notifyDataSetChanged();
//                mGridData = Arrays.asList(result);
                mProgressBar.setVisibility(View.GONE);
                if(mGridData!=null && mGridData.size()!=0 && isForTablet)
                    updateFragment3(mGridData.get(0));

            }
        }

    }
}
