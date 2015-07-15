package com.example.rachel.wygt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Rachel on 10/24/14.
 */

public class GpsTrackerBootReceiver extends BroadcastReceiver {
    private static final String TAG = "GpsTrackerBootReceiver";
    @Override

    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent gpsTrackerIntent = new Intent("com.example.wygt.alarm");
        gpsTrackerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,MyApplication.REQUESTCODE, gpsTrackerIntent, 0);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());
        String interval = sharedPreferences.getString("prefSyncFrequency", "5");
        int intervalInMinutes = Integer.parseInt(interval);

        Log.d(TAG, "BootRecievedYO");
        Log.d("GPS-MyActivity", "Shared Preferences interval: " + intervalInMinutes);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000, // 60000 = 1 minute
                pendingIntent);


     //   Toast.makeText(context, "Booting Completed", Toast.LENGTH_LONG).show();

    }
}
