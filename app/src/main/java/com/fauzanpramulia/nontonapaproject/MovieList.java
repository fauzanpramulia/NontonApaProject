package com.fauzanpramulia.nontonapaproject;

import com.fauzanpramulia.nontonapaproject.model.MovieItems;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieList {
    @SerializedName("results")
    public List<MovieItems> results;
}
