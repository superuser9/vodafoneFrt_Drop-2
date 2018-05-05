package com.vodafone.frt.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.PTRRouteControllerTaskActivity;
import com.vodafone.frt.services.SyncService;

/**
 * Created by qss on 2/7/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;

    public NetworkChangeReceiver() {

    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
//        if (isConnected(context)) {
//            Toast.makeText(context, "Network Available Do operations", Toast.LENGTH_LONG).show();
            context.startService(new Intent(context, SyncService.class));
//            showNotification(context);
//        }
//        else {
//            Toast.makeText(context, "Internet connection lost", Toast.LENGTH_LONG).show();
//        context.startService(new Intent(context, SyncService.class));
//            showNotification(context);
//        }
//        else {
////            Toast.makeText(context, "Internet connection lost", Toast.LENGTH_LONG).show();
//            context.stopService(new Intent(context, SyncService.class));
////            hideNotification(context);
//        }

    }

    boolean isConnected(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    void showNotification(Context context) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.actual_end)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(String.format(context.getString(R.string.app_name)));
        Intent resultIntent = new Intent(context, PTRRouteControllerTaskActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PTRRouteControllerTaskActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(432, mBuilder.build());
    }

    void hideNotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(432);
    }

}
