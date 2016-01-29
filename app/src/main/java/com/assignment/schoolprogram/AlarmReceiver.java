package com.assignment.schoolprogram;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 6266991
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
  
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vib = SP.getBoolean("vibration", true);
        boolean sound = SP.getBoolean("sound",true);
        if (vib) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
        if (sound) {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
            ringtone.play();
        }
        String activityName = intent.getStringExtra("ACTIVITY_NAME");
        Toast.makeText(context, "The event " + activityName + " will be starting in 10 minutes", Toast.LENGTH_LONG).show();
    }

    public void setAlarm(Activity context, HashMap<String, String> alarmTimes, int day, int month, int year) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        int i = 0;
        for (String key : alarmTimes.keySet()) {
            String start = alarmTimes.get(key);
            String[] toks = start.trim().split(":");
            int hour = Integer.parseInt(toks[0]);
            int min = Integer.parseInt(toks[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);

            // 10 minutes before event happens
            calendar.add(Calendar.MINUTE, -10);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("ACTIVITY_NAME", key);
            // Loop counter `i` is used as a `requestCode`
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, intent, 0);

            Calendar now = Calendar.getInstance();
            if (calendar.after(now)) {
                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);
            }
            intentArray.add(pendingIntent);
        }
        // Enable BootReceiver to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
