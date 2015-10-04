package com.foodroacher.app.android.app;
/**
 * 
 */

import java.util.UUID;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * @author vishal.gaurav@hotmail.com
 *
 */
public class FoodRoacherApp extends Application {

    private BandClient client = null;
    private boolean isBandConnected = false;
    
    public BandClient getClient() {
        return client;
    }
    public boolean isBandConnected() {
        return isBandConnected;
    }
    public void setBandConnected(boolean isBandConnected) {
        this.isBandConnected = isBandConnected;
    }
    private class BandTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                isBandConnected = getConnectedBandClient();
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                case DEVICE_ERROR:
                    exceptionMessage = "Please make sure bluetooth is on and the band is in range.";
                    break;
                case UNSUPPORTED_SDK_VERSION_ERROR:
                    exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                    break;
                case SERVICE_ERROR:
                    exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                    break;
                case BAND_FULL_ERROR:
                    exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.";
                    break;
                default:
                    exceptionMessage = "Unknown error occured: " + e.getMessage();
                    break;
                }
            } catch (Exception e) {
            } 
            return null;
        }
    }
    
    public boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
       return ConnectionState.CONNECTED == client.connect().await();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        new BandTask().execute();
    }

    @Override
    public void onTerminate() {
        
        super.onTerminate();
    }
    public static void showGenericToast(Context context,String message){
        Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
    }
   public static FoodRoacherApp getApp(Context context){
       return (FoodRoacherApp)context.getApplicationContext();
   }
}
