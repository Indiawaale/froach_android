/**
 * 
 */
package com.foodroacher.app.android.network;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author vishal.gaurav@hotmail.com
 *
 */
public final class FoodEvents implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8335836590127673560L;
    public static final String JSON_KEY_ID = "objectID";
    public static final String JSON_KEY_LATITUDE = "lat";
    public static final String JSON_KEY_LONGITUDE = "long";
    public static final String JSON_KEY_LOCATION_NAME = "location";
    public static final String JSON_KEY_START_TIME = "start_time";
    public static final String JSON_KEY_END_TIME = "end_time";
    public static final String JSON_KEY_TITLE = "event";
    public static final String JSON_KEY_DESCRIPTION = "description";




    private long id;
    private double latitude;
    private double longitude;
    private long startTime;
    private long endTime;
    private String title;
    private String description;
    
    public FoodEvents(JSONObject json) throws JSONException{
     this.id = json.getLong(JSON_KEY_ID);
     this.latitude= json.getDouble(JSON_KEY_LATITUDE);
     this.longitude= json.getDouble(JSON_KEY_LONGITUDE);
     this.startTime= json.getLong(JSON_KEY_START_TIME);
     this.endTime= json.getLong(JSON_KEY_END_TIME);
     this.title= json.getString(JSON_KEY_TITLE);
     this.description= json.getString(JSON_KEY_DESCRIPTION);
    }
    
    public FoodEvents(long id, double latitude, double longitude, long startTime, long endTime, String title, String description) {
        super();
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
