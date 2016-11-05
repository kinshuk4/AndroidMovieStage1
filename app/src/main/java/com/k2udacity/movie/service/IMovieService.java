package com.k2udacity.movie.service;

import com.k2udacity.sunshine.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by kchandra on 03/03/16.
 */
public interface IMovieService {
    @GET("users/{user}/repos")
    Call<List<Movie>> listMovies(@Path("user") String user);
}
