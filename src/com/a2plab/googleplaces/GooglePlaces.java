package com.a2plab.googleplaces;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.a2plab.googleplaces.query.DetailsQuery;
import com.a2plab.googleplaces.query.NearbySearchQuery;
import com.a2plab.googleplaces.query.NearbySearchQuery.Ranking;
import com.a2plab.googleplaces.query.PhotoQuery;
import com.a2plab.googleplaces.query.Query;
import com.a2plab.googleplaces.query.TextSearchQuery;
import com.a2plab.googleplaces.result.PhotoResult;
import com.a2plab.googleplaces.result.PlaceDetailsResult;
import com.a2plab.googleplaces.result.PlacesResult;
import com.a2plab.googleplaces.result.Result;

public class GooglePlaces {

    private String mApiKey = "";
    private AbstractSet<String> mSupportedPlaces;

    public GooglePlaces(String apiKey) {
        mApiKey = apiKey;
        loadSupportedPlaces();
    }

    /* ------------------------------------------------------ */
    /* NEARBYSEARCH */
    /* ------------------------------------------------------ */

    /**
     * @param types
     * @param keyword
     * @param radius
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(List<String> types, String keyword, int radius, double lat, double lon) throws IOException {
    	// Added radius to NearbySearchQuery
        NearbySearchQuery query = new NearbySearchQuery(mApiKey, lat, lon, radius);
        
        //Added Rankby to Distance by Navine
        query.setRanking(Ranking.prominence);
        
        //Added SetLanguage
        query.setLanguage("en");

        if (types != null) {
            for (String type : types) {
                if (isSupportedPlace(type))
                    query.addType(type);
            }
        }

        if (keyword != null && keyword != "") {
            query.setKeyword(keyword);
        }

        return getPlaces(query);
    }

// To build query without Radius and RankBy Distance    
    /**
     * @param types
     * @param keyword
     * @param rankby
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(List<String> types, String keyword, String rankby, double lat, double lon) throws IOException {
    	// Added rankby to NearbySearchQuery
        NearbySearchQuery query = new NearbySearchQuery(mApiKey, lat, lon, rankby);
        
        //Added Rankby to Distance by Navine
        //query.setRanking(Ranking.Prominence);
        
        //Added SetLanguage
        query.setLanguage("en");

        if (types != null) {
            for (String type : types) {
                if (isSupportedPlace(type))
                    query.addType(type);
            }
        }

        if (keyword != null && keyword != "") {
            query.setKeyword(keyword);
        }

        return getPlaces(query);
    }


    
    /**
     * @param types
     * @param radius
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(List<String> types, int radius, double lat, double lon) throws IOException {
        return getNearbyPlaces(types, null, radius, lat, lon);
    }

    /**
     * @param type
     * @param keyword
     * @param radius
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(String type, String keyword, int radius, double lat, double lon) throws IOException {
        return getNearbyPlaces(Arrays.asList(type), keyword, radius, lat, lon);
    }

    /**
     * 
     * DEFAULT NEARBY
     * 
     * @param sensor
     * @param radius
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(double lat, double lon) throws IOException {
        return getPlaces(new NearbySearchQuery(mApiKey, lat, lon));
    }

    /**
     * 
     * DEFAULT NEARBY
     * 
     * @param sensor
     * @param radius
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(int radius, double lat, double lon) throws IOException {
        return getPlaces(new NearbySearchQuery(mApiKey, lat, lon));
    }

    /**
     * 
     * DEFAULT NEARBY
     * 
     * @param sensor
     * @param radius
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    public Result getNearbyPlaces(int radius, double lat, double lon, boolean sensor) throws IOException {
        NearbySearchQuery query = new NearbySearchQuery(mApiKey, lat, lon);
        query.setSensor(sensor);
        return getPlaces(query);
    }

    /* ------------------------------------------------------ */
    /* TEXTSEARCH */
    /* ------------------------------------------------------ */

    /**
     * TextSearch with sensor choice
     * 
     * @param searchText
     * @param sensor
     * @return
     * @throws ClientProtocolException
     * @throws JSONException
     * @throws IOException
     */
    public Result getTextPlaces(String searchText, boolean sensor) throws IOException {
        return getPlaces(new TextSearchQuery(mApiKey, searchText, sensor));
    }

    /**
     * TextSearch with sensor choice
     * 
     * @param searchText
     * @return
     * @throws ClientProtocolException
     * @throws JSONException
     * @throws IOException
     */
    public Result getTextPlaces(String searchText) throws IOException {
        return getPlaces(new TextSearchQuery(mApiKey, searchText, true));
    }

    /* ------------------------------------------------------ */
    /* RADAR SEARCH */
    /* ------------------------------------------------------ */

    /* ------------------------------------------------------ */
    /* EVENT SEARCH */
    /* ------------------------------------------------------ */

    /* ------------------------------------------------------ */
    /* DETAILS SEARCH */
    /* ------------------------------------------------------ */

    /**
     * @param reference
     * @return
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Result getPlaceDetails(String reference) throws IOException {
        return getPlaces(new DetailsQuery(this.mApiKey, reference), PlaceDetailsResult.class);
    }

    /**
     * @param query
     * @return
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Result getPlaceDetails(Query query) throws IOException {
        return getPlaces(query, PlaceDetailsResult.class);
    }

    /* ------------------------------------------------------ */
    /* PHOTO SEARCH */
    /* ------------------------------------------------------ */

    /**
     * @param photoreference
     * @return
     * @throws IOException
     */
    public Result getPlacesPhoto(String photoreference) throws IOException {
        return sendRequest(new PhotoQuery(mApiKey, photoreference), PhotoResult.class);

    }

    /**
     * @param photoreference
     * @param maxWidth
     * @param maxHeight
     * @return
     * @throws IOException
     */
    public Result getPlacesPhoto(String photoreference, int maxWidth, int maxHeight) throws IOException {
        PhotoQuery p = new PhotoQuery(mApiKey, photoreference);
        p.setMaxHeight(maxHeight).setMaxWidth(maxWidth);
        return sendRequest(p, PhotoResult.class);
    }

    /* ------------------------------------------------------ */
    /* GENERIC SEARCH */
    /* ------------------------------------------------------ */

    /**
     * Base PlaceResult Request with Query object
     * 
     * @param query
     * @return
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Result getPlaces(Query query) throws IOException {
        return this.getPlaces(query, PlacesResult.class);
    }

    /**
     * Generic Request with custom Parsing return type
     * 
     * @param query
     * @param resultClass
     * @return
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Result getPlaces(Query query, Class<? extends Result> resultClass) throws IOException {
        return sendRequest(query, resultClass);
    }

    /**
     * @param query
     * @param resultClass
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JSONException
     */
    private Result sendRequest(Query query, Class<? extends Result> resultClass) throws IOException {
        return query.getRequest().execute().parseAs(resultClass);
    }

    /**
     * Check if inserted string are in supported place types
     * 
     * @param placeType
     * @return
     */
    public boolean isSupportedPlace(String placeType) {
        return (mSupportedPlaces.contains(placeType));
    }

    /**
     * LIST OF SUPPORTED TYPE
     */
    private void loadSupportedPlaces() {
        mSupportedPlaces = new HashSet<String>();

        mSupportedPlaces.add("accounting");
        mSupportedPlaces.add("airport");
        mSupportedPlaces.add("amusement park");
        mSupportedPlaces.add("aquarium");
        mSupportedPlaces.add("art gallery");
        mSupportedPlaces.add("atm");
        mSupportedPlaces.add("bakery");
        mSupportedPlaces.add("bank");
        mSupportedPlaces.add("bar");
        mSupportedPlaces.add("beauty salon");
        mSupportedPlaces.add("bicycle store");
        mSupportedPlaces.add("book store");
        mSupportedPlaces.add("bowling alley");
        mSupportedPlaces.add("bus station");
        mSupportedPlaces.add("cafe");
        mSupportedPlaces.add("campground");
        mSupportedPlaces.add("car dealer");
        mSupportedPlaces.add("car rental");
        mSupportedPlaces.add("car repair");
        mSupportedPlaces.add("car_repair");
        mSupportedPlaces.add("car wash");
        mSupportedPlaces.add("casino");
        mSupportedPlaces.add("cemetery");
        mSupportedPlaces.add("church");
        mSupportedPlaces.add("city hall");
        mSupportedPlaces.add("clothing store");
        mSupportedPlaces.add("convenience store");
        mSupportedPlaces.add("courthouse");
        mSupportedPlaces.add("dentist");
        mSupportedPlaces.add("department store");
        mSupportedPlaces.add("doctor");
        mSupportedPlaces.add("electrician");
        mSupportedPlaces.add("electronics store");
        mSupportedPlaces.add("embassy");
        mSupportedPlaces.add("establishment");
        mSupportedPlaces.add("finance");
        mSupportedPlaces.add("fire station");
        mSupportedPlaces.add("florist");
        mSupportedPlaces.add("food");
        mSupportedPlaces.add("funeral home");
        mSupportedPlaces.add("furniture store");
        mSupportedPlaces.add("gas station");
        mSupportedPlaces.add("gas_station");    
        mSupportedPlaces.add("general contractor");
        mSupportedPlaces.add("grocery or supermarket");
        mSupportedPlaces.add("gym");
        mSupportedPlaces.add("hair care");
        mSupportedPlaces.add("hardware store");
        mSupportedPlaces.add("health");
        mSupportedPlaces.add("hindu temple");
        mSupportedPlaces.add("home goods store");
        mSupportedPlaces.add("hospital");
        mSupportedPlaces.add("insurance agency");
        mSupportedPlaces.add("jewelry store");
        mSupportedPlaces.add("laundry");
        mSupportedPlaces.add("lawyer");
        mSupportedPlaces.add("library");
        mSupportedPlaces.add("liquor store");
        mSupportedPlaces.add("local government office");
        mSupportedPlaces.add("locksmith");
        mSupportedPlaces.add("lodging");
        mSupportedPlaces.add("meal delivery");
        mSupportedPlaces.add("meal takeaway");
        mSupportedPlaces.add("mosque");
        mSupportedPlaces.add("movie rental");
        mSupportedPlaces.add("movie theater");
        mSupportedPlaces.add("moving company");
        mSupportedPlaces.add("museum");
        mSupportedPlaces.add("night club");
        mSupportedPlaces.add("painter");
        mSupportedPlaces.add("park");
        mSupportedPlaces.add("parking");
        mSupportedPlaces.add("pet store");
        mSupportedPlaces.add("pharmacy");
        mSupportedPlaces.add("physiotherapist");
        mSupportedPlaces.add("place of worship");
        mSupportedPlaces.add("plumber");
        mSupportedPlaces.add("police");
        mSupportedPlaces.add("post office");
        mSupportedPlaces.add("real estate agency");
        mSupportedPlaces.add("restaurant");
        mSupportedPlaces.add("rest room");
        mSupportedPlaces.add("rest_room");
        mSupportedPlaces.add("roofing contractor");
        mSupportedPlaces.add("rv park");
        mSupportedPlaces.add("school");
        mSupportedPlaces.add("shoe store");
        mSupportedPlaces.add("shopping mall");
        mSupportedPlaces.add("spa");
        mSupportedPlaces.add("stadium");
        mSupportedPlaces.add("storage");
        mSupportedPlaces.add("store");
        mSupportedPlaces.add("subway station");
        mSupportedPlaces.add("synagogue");
        mSupportedPlaces.add("taxi stand");
        mSupportedPlaces.add("toilet");
        mSupportedPlaces.add("train station");
        mSupportedPlaces.add("travel agency");
        mSupportedPlaces.add("university");
        mSupportedPlaces.add("veterinary care");
        mSupportedPlaces.add("zoo");
    }
}
