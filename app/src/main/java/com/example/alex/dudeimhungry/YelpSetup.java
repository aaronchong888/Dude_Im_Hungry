package com.example.alex.dudeimhungry;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;
import com.yelp.clientlib.entities.Business;

import java.io.*;
import java.util.*;
import java.lang.*;

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
        // TODO: Update to use live coordinates
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(37.789)
                .longitude(-122.399).build();

        // Creating the map to call from
        Map<String, String> params = new HashMap<>();

        // Search in a 10-mile radius
        // TODO: Narrow search results? Fast food only?
        params.put("radius-filter", "16000");
        Call<SearchResponse> call = yelpAPI.search("Los Angeles", params);
        SearchResponse searchResponse = call.execute().body();

        // Results
        int totalResults = searchResponse.total();
        ArrayList<Business> businesses = searchResponse.businesses();

        // Get name, rating, distance
        // Display one-by-one in order that Yelp returns data to us
        // TODO: Loop through businesses if user hits "next restaurant" or something
        String businessName = businesses.get(0).name();
        Double rating = businesses.get(0).rating(); // Display with stars?
        Double busLat = businesses.get(0).location().coordinate().latitude();
        Double busLong = businesses.get(0).location().coordinate().longitude();
        //Double dist = distance(myLat, myLong, busLat, busLong);
    }

    // Calculates distance between two coordinates in miles
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                      Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
