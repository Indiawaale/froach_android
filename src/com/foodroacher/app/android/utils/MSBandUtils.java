package com.foodroacher.app.android.utils;

import java.util.List;
import java.util.UUID;

import com.foodroacher.app.android.app.FoodRoacherApp;
import com.microsoft.band.BandClient;
import com.microsoft.band.tiles.BandTile;

import android.content.Context;

public class MSBandUtils {
    public static final UUID tileId = UUID.fromString("bb0D508F-70A3-47D4-BBA3-812BADB1F8Aa");

    public static boolean doesTileExist(List<BandTile> tiles, UUID tileId) {
        for (BandTile tile : tiles) {
            if (tile.getTileId().equals(tileId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        return FoodRoacherApp.getApp(context).isBandConnected();
    }
    public static BandClient getBandClient(Context context) {
        return FoodRoacherApp.getApp(context).getClient();
    }
}
