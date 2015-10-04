package com.foodroacher.app.android.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.app.FoodRoacherApp;
import com.foodroacher.app.android.network.FoodEvents;
import com.foodroacher.app.android.ui.activities.EventDetails;
import com.foodroacher.app.android.utils.MSBandUtils;
import com.google.android.gms.gcm.GcmListenerService;
import com.microsoft.band.BandIOException;
import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.pages.BarcodeType;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("body");
        FoodEvents event = getEventFromBody(message);
        if (event != null) {
            createNotification(event);
        }
    }

    private FoodEvents getEventFromBody(String body) {
        FoodEvents event = null;
        try {
            JSONObject jObJect = new JSONObject(body);
            event = new FoodEvents(jObJect);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return event;
    }

    // Creates notification based on title and body received
    private void createNotification(FoodEvents event) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher).setContentTitle(event.getTitle())
                .setContentText(event.getDescription());
        Intent resultIntent = new Intent(this, EventDetails.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra(EventDetails.EXTRA_EVENT, event);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT, null);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
        notifyMyWatch(event);
    }

    private void notifyMyWatch(FoodEvents event) {
        try {
            if (MSBandUtils.isConnected(getBaseContext())) {
                MSBandUtils.getBandClient(getBaseContext()).getNotificationManager().sendMessage(MSBandUtils.tileId, event.getTitle(), event.getDescription(),new Date(),MessageFlags.SHOW_DIALOG);
            }
        } catch (BandIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addTile() {

    }

}