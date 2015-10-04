/**
 * 
 */
package com.foodroacher.app.android.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public static final String QUERY_START = "?";
    public static final String EQUAL = "=";
    public static final String AND = "&";

    public static final String KEY_USER = "userid";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_COLLEGEID = "collegeid";


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
    private static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String contentAsString = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
            
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
             contentAsString =  readIt(is, len);
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
    private static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
