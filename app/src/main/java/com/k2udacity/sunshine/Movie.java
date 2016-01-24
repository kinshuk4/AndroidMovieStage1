package com.k2udacity.sunshine;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Date;
/**
 * Created by kchandra on 23/01/16.
 */
public class Movie implements Serializable{
    private static final String baseImgUrl = "http://image.tmdb.org/t/p/";
    private static final String size = "w185";
    private static final String bigSize = "w342";
    private String originalTitle;
    private String posterPath;
    private boolean isAdult;
    private String overview;
    private double rating;
    private Date releaseDate;

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    private LinkedList<String> trailers;


    public String getImageUrl() {
        return baseImgUrl+size+ posterPath;
    }


    public String getBigImageUrl() {
        return baseImgUrl+bigSize+ posterPath;
    }



    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setIsAdult(boolean isAdult) {
        this.isAdult = isAdult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public LinkedList<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(LinkedList<String> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "originalTitle='" + originalTitle + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", isAdult=" + isAdult +
                ", overview='" + overview + '\'' +
                ", rating=" + rating +
                ", trailers=" + trailers +
                '}';
    }
}
