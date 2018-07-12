package com.ssverma.showtime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("cast")
    private List<Cast> casts;

    public int getId() {
        return id;
    }

    public List<Cast> getCasts() {
        return casts;
    }
}
