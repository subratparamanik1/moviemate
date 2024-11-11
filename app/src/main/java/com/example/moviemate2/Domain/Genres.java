package com.example.moviemate2.Domain;


import java.util.List;


public class Genres {

    private List<GenresItem> genres;

    public List<GenresItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresItem> genres) {
        this.genres = genres;
    }
}
