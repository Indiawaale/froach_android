/**
 * 
 */
package com.foodroacher.app.android.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author vishal.gaurav@hotmail.com
 *
 */
public final class NetworkUtils {
    public static final String BASE_URL = "http://foodroach.cloudapp.net";
    public static final String REGISTER_API = "/register";
    public static final String REGISTER_GCM_API = "/gcm";
    public static final String LOCATION_API = "/location";
    public static final String QUERY_START = "?";
    public static final String EQUAL = "=";
    public static final String AND = "&";

    public static final String KEY_USER = "userid";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_COLLEGEID = "collegeid";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";
    public static final String KEY_EVENTS = "hits";
    public static final String KEY_GCM = "gcm";



    public static boolean isNetworkConnected(Context context) {
        boolean result = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            result = true;
        }
        return result;
    }

    public static RegistrationResult registerUser(Context context, String email, String password, String school) {
        RegistrationResult result = null ;
        String url = BASE_URL + REGISTER_API + QUERY_START +
                     KEY_USER + EQUAL + email + AND +  
                     KEY_PASSWORD + EQUAL + password + AND + 
                     KEY_COLLEGEID + EQUAL + school ;
        try {
            if(downloadUrl(url) != null){
                result = new RegistrationResult(email);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public static boolean registerUserGcm(Context context, String email, String gcmKey) {
        boolean result = false ;
        String url = BASE_URL + REGISTER_GCM_API + QUERY_START +
                     KEY_USER + EQUAL + email + AND +  
                     KEY_GCM + EQUAL + gcmKey ;
        try {
            if(downloadUrl(url) != null){
                result = true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public static List<FoodEvents> getFoodEvents(Context context, String userId, String latitude, String longitude) {
        List<FoodEvents> events = null ;
        String url = BASE_URL + LOCATION_API + QUERY_START +
                     KEY_USER + EQUAL + userId + AND +  
                     KEY_LAT + EQUAL + latitude + AND + 
                     KEY_LONG + EQUAL + longitude ;
        try {
            String result = downloadUrl(url);
            if(result!=null && !result.isEmpty()){
                events = new ArrayList<FoodEvents>();
                parseEvents(result, events);
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return events;
    }
    private static void parseEvents(String result, List<FoodEvents> events) {
        try {
            JSONObject jObJect = new JSONObject(result);
            JSONArray array = jObJect.getJSONArray(KEY_EVENTS);
            int count = array.length();
            for (int i = 0; i < count; i++) {
                JSONObject foodEventJson = array.getJSONObject(i);
                FoodEvents newEvent = new FoodEvents(foodEventJson);
                events.add(newEvent);
            }
        } catch (JSONException e) {
            events.clear();
        }
        
    }

    private static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String contentAsString = null;
        // Only display the first 500 characters of the retrieved
        // web page content.            
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if(response == 200){
            is = conn.getInputStream();
            // Convert the InputStream into a string
             contentAsString =  readIt(is);
            }
            return contentAsString;
            
            
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (is != null) {
                is.close();
            } 
        }
    }
    private static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}
