package com.vitap.wified;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static android.content.Context.POWER_SERVICE;

public class commons {

    static WifiManager wifiManager;
    static WifiInfo wifiInfo ;
    static NotificationManager mNotificationManager ;



    public static void sendNotification(Context parent ,String title,String body){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(parent.getApplicationContext(), "notify_001");

            mBuilder.setSmallIcon(R.mipmap.wifi);
            mBuilder.setContentTitle("Wified - "+title);
            mBuilder.setContentText(body);
            mBuilder.setPriority(Notification.PRIORITY_MAX);

            mNotificationManager =
                    (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);

            //= Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                String channelId = "notify_001";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            mNotificationManager.notify(0, mBuilder.build());
            try{Looper.prepare();}catch (Exception e){;}
            Toast.makeText(parent.getApplicationContext(),body,Toast.LENGTH_LONG).show();

    }

    //Just a copy of the previous function to open main activity window with on touch on the notification
    public static void sendNotification(Context parent ,String title,String body,int JumpBack){

        PendingIntent jumpingJapang = PendingIntent.getActivity(parent, 0,
                new Intent(parent, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(parent.getApplicationContext(), "notify_001");

        mBuilder.setSmallIcon(R.mipmap.wifi);
        mBuilder.setContentTitle("Wified - "+title);
        mBuilder.setContentText(body);
        mBuilder.setContentIntent(jumpingJapang);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager =
                (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);

        //= Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
        try{Looper.prepare();}catch (Exception e){;}
        Toast.makeText(parent.getApplicationContext(),body,Toast.LENGTH_LONG).show();

    }

    public static int giveWifiSignalStrength(Context context){
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 10);
    }

    public static void initiateBackgroundRunPermission(Context parent){
        try {
            Intent intent = new Intent();
            String packageName = parent.getPackageName();
            PowerManager pm = (PowerManager) parent.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                parent.startActivity(intent);
            }
        }
        catch(Exception e){;}
    }
}
