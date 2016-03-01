package com.example.alex.dudeimhungry;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;

/**
 * Created by pointatnick on 3/1/16.
 */
public class YelpSetup {
    String CONSUMER_KEY = 'COmG92hPzvo9Jo3Y-wtvag';
    String CONSUMER_SECRET = 'CF_mB0o-CYb1orOtjgx-ZdSTCaM';
    String TOKEN = 'ofVcS6smNc6Is42h2YBrfbOGP7AevBbi';
    String TOKEN_SECRET = 'fW5zIji0LEMutS4JE2hI7AuMTXg';

    YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    YelpAPI yelpAPI = apiFactory.createAPI();
}
