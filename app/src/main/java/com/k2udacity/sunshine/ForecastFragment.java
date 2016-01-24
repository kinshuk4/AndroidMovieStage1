package com.k2udacity.sunshine;

import android.content.SharedPreferences;
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
import android.widget.GridView;
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.

 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    private static final String BASE_URL="https://api.themoviedb.org/3/movie/550?api_key=";
    private static String API_KEY="ce89c354b4048d7e8a20aee5a74860dc";
    private static final String POPULAR_URL="http://api.themoviedb.org/3/discover/movie";
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment(){
    }
    public String formatHighLows(double high, double low) {
        return ("");
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Fragment will handle Menu Options
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        API_KEY = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_default_api_key));
        weatherTask.execute(POPULAR_URL,API_KEY,"sort_by","popularity.desc");

    }

    public ImageAdapter mForecastAdapter = new ImageAdapter(getActivity());
    GridViewAdapter mGridAdapter ;
    ArrayList<Movie> mGridData;
    GridView mGridView;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //RootView has been created.
        //Populating the fragment with some dummy data's.

        //Creating dummy datas for the list view.
        //Format of data is- "Day - Weather - MaxTemp/MinTemp".


        //Initializing the Adapter.
        //The List will access the Adapter which will provide it with the data.
        //Adapter knows how to manage. It is programmed as such.
        //Syntax: ArrayAdapter<String>.
        //Parameters: Context, ID of List Item Layout, ID of TextView, list of data.

        //Get reference to to the ListView, and attach this adapter to Forecast Adapter.
//        GridView gridView = (GridView) rootView.findViewById(R.id.listView_forecast);
//        gridView.setAdapter(new ImageAdapter(getActivity()));

        mGridView = (GridView) rootView.findViewById(R.id.listView_forecast);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        //Initialize with empty data
        if(mGridData==null)
            mGridData = new ArrayList<>();
        Log.i(LOG_TAG,"Grid Data Size"+mGridData.size());
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);
        updateWeather();
        mProgressBar.setVisibility(View.VISIBLE);


//        gridView.setAdapter(mForecastAdapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String forecast = mForecastAdapter.getItem(position);
//                Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);
//            }
//        });

        return rootView;
    }
    public class FetchWeatherTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

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


            final String OWM_DESCRIPTION = "main";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
            Movie[] resultStrs = null;
//            if(numDays!=-1)
//                resultStrs = new Movie[numDays];
//            else
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
//                Log.d(LOG_TAG,m.toString());
                resultStrs[i] =m;

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
//                mForecastAdapter.clear();
//                for (Movie dayForecastStr : result){
                    mForecastAdapter.setImages(result);
//                }
                Log.i(LOG_TAG,"Size of movie arr"+result.length);
                mGridData = new ArrayList<Movie>(Arrays.asList(result));
                mGridAdapter.setGridData(mGridData);
                mGridAdapter.notifyDataSetChanged();
//                mGridData = Arrays.asList(result);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }
}
