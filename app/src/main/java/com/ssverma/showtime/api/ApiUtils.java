package com.ssverma.showtime.api;

public class ApiUtils {
    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    public static TmdbService getTmdbService() {
        return ApiClient.getClient(BASE_URL).create(TmdbService.class);
    }
}
