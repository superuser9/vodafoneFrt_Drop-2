package com.vodafone.frt.utility;

import android.util.Log;

import com.google.android.gms.maps.model.TileProvider;
import com.vodafone.frt.constants.WMSTileProvider;
import com.vodafone.frt.models.WMSProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Lepton on 13-Mar-18.
 */
public class WMSTileProviderFactory {
    protected static final String TAG = WMSTileProviderFactory.class.getSimpleName();


    static final String WMS_SERVICE_PARAMETERS = "&SRS=%s"
            + "&VERSION=1.0.0"
            + "&REQUEST=GetMap"
            + "&LAYERS=%s"
            + "&SERVICE=WMS"
            + "&BBOX=%f,%f,%f,%f"
            + "&WIDTH=256"
            + "&HEIGHT=256"
            + "&FORMAT=image/png"
            + "&reqver=0"
            + "&TRANSPARENT=true"
            + "&STYLES=%s";

    public static TileProvider getTileProvider(final WMSProvider provider) {
        TileProvider tileProvider = new WMSTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                Log.d(TAG,"Zoom Label:"+ zoom);
                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }
                double[] bbox = getBoundingBox(x, y, zoom);
                String urlStr = provider.url + String.format(Locale.US, WMS_SERVICE_PARAMETERS, provider.crs, provider.layers, bbox[MINX], bbox[MINY], bbox[MAXX], bbox[MAXY], provider.styles);
                URL url = null;
                try {
                    url = new URL(urlStr);
                    Log.d(TAG,"Url:"+ url);
                } catch (MalformedURLException e) {
                    Log.e(TAG,"MalformedURLException:"+ e);
                    throw new AssertionError(e);
                }
                return url;
            }
        };

        return tileProvider;
    }

    private static boolean checkTileExists(int x, int y, int zoom) {
        int minZoom = 14;
        int maxZoom = 21;
        if ((zoom < minZoom || zoom > maxZoom)) return false;
        else return true;
    }

}
