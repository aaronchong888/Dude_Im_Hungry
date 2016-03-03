package com.example.alex.dudeimhungry;

import android.location.Location;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by pointatnick on 3/1/16.
 */
public class YelpSetup {
    // API keys
    String CONSUMER_KEY = "COmG92hPzvo9Jo3Y-wtvag";
    String CONSUMER_SECRET = "CF_mB0o-CYb1orOtjgx-ZdSTCaM";
    String TOKEN = "ofVcS6smNc6Is42h2YBrfbOGP7AevBbi";
    String TOKEN_SECRET = "fW5zIji0LEMutS4JE2hI7AuMTXg";

    private YelpAPI yelpAPI;

    public void setUp() {
        // Creating the API object from YelpAPI library
        YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        yelpAPI = apiFactory.createAPI();
    }

    public void searchByCoordinate() throws IOException {
        //Location myLoc;
        // TODO: Update to use live coordinates
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(37.789)
                .longitude(-122.399).build();

        // Creating the map to call from
        Map<String, String> params = new HashMap<>();

        // Search in a 10-mile radius
        params.put("radius-filter", "16000");

        Call<SearchResponse> call = yelpAPI.search("Los Angeles", params);
        Response<SearchResponse> response = call.execute();
    }
}
