package com.k2udacity.sunshine;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#MovieApp";
    private String mForecastStr;

//    private OnFragmentInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    private Movie movie;

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(Movie movie) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setMovie(movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_share) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        rootView = rootView;
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        MainActivity ma = (MainActivity) getActivity();
        Movie movie = ma.getMovie();
        if(movie!=null){
            update(movie,rootView);
        }else{
            Log.w(LOG_TAG, "Movie yet not set");
        }


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }


    public void update(Movie movie) {
        Log.i(LOG_TAG,"Inside Update");
        if(movie==null)
            return ;
        Log.i(LOG_TAG, movie.toString());
        View rootView = getView();
        if(rootView==null){
            Log.w(LOG_TAG,"Fragment view is still not ready");
            return;
        }
        ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_detail_image);
        Picasso.with(getActivity())
                .load(movie.getImageUrl())
//                .fit()
                .into(imageView);
        ((TextView) rootView.findViewById(R.id.movie_detail_title))
                .setText(movie.getOriginalTitle());
        ((TextView) rootView.findViewById(R.id.movie_detail_description1))
                .setText(movie.getOverview());
        String year = (String) android.text.format.DateFormat.format("yyyy", movie.getReleaseDate()); //2013
        ((TextView) rootView.findViewById(R.id.movie_detail_release_yr1))
                .setText(year);
        ((TextView) rootView.findViewById(R.id.movie_detail_duration1))
                .setText("120min");
        ((TextView) rootView.findViewById(R.id.movie_detail_rating1))
                .setText(movie.getRating()+"/10");


        return ;
    }

    public void update(Movie movie, View rootView) {
        Log.i(LOG_TAG,"Inside Update");
        if(movie==null)
            return ;
        Log.i(LOG_TAG, movie.toString());
        if(rootView==null){
            Log.w(LOG_TAG,"Fragment view is still not ready");
            return;
        }
        ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_detail_image);
        Picasso.with(getActivity())
                .load(movie.getImageUrl())
//                .fit()
                .into(imageView);
        ((TextView) rootView.findViewById(R.id.movie_detail_title))
                .setText(movie.getOriginalTitle());
        TextView tvDesc =  ((TextView) rootView.findViewById(R.id.movie_detail_description));
        tvDesc.setText(movie.getOverview());
        tvDesc.setMovementMethod(new ScrollingMovementMethod());
        String year = (String) android.text.format.DateFormat.format("yyyy", movie.getReleaseDate()); //2013
        ((TextView) rootView.findViewById(R.id.movie_detail_release_yr))
                .setText(year);
        ((TextView) rootView.findViewById(R.id.movie_detail_duration))
                .setText("120min");
        ((TextView) rootView.findViewById(R.id.movie_detail_rating))
                .setText(movie.getRating()+"/10");


        return ;
    }
}
