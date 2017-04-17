package com.example.alex.dudeimhungry;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by pointatnick on 3/1/16.
 */
public class YelpSetup {
    // API keys
    String CONSUMER_KEY = "COmG92hPzvo9Jo3Y-wtvag";
    String CONSUMER_SECRET = "CF_mB0o-CYb1orOtjgx-ZdSTCaM";
    String TOKEN = "ofVcS6smNc6Is42h2YBrfbOGP7AevBbi";
    String TOKEN_SECRET = "fW5zIji0LEMutS4JE2hI7AuMTXg";

    static ArrayList<Business> businesses;
    private static YelpAPI yelpAPI;
    int business_number;

    //constructor
    public YelpSetup() {
        YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        yelpAPI = apiFactory.createAPI();
    }

    public void findResults() {
        Map<String, String> params = new HashMap<>();
        params.put("radius-filter", "3000");
        Call<SearchResponse> call = yelpAPI.search("Los Angeles", params);
        business_number = 0;
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
                businesses = searchResponse.businesses();
            }
            @Override
            public void onFailure(Throwable t) {
                System.err.println("HTTP error: " + t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    public Business next()
    {
        business_number++;
        return businesses.get(business_number);
    }

    public Business prev() {
        business_number--;
        return businesses.get(business_number);
    }
}
