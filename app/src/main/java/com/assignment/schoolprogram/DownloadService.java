package com.assignment.schoolprogram;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  6266991
 */
public class DownloadService extends Service {
    public static String SCHOOLS_URL = "https://bitbucket.org/yep11jnu/se2/downloads/schools.csv";
    public static String PROGRAM_URL = "https://bitbucket.org/yep11jnu/se2/downloads/program.csv";
    public static String DATES_URL = "https://bitbucket.org/yep11jnu/se2/downloads/dates.csv";
    public static Activity activity;

    NotificationCompat.Builder mBuilder;
    // Sets an ID for the notification
    int mNotificationId = 1;
    // Gets an instance of the NotificationManager service
    NotificationManager mNotificationManager;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // Timer
    private Timer mTimer = null;

    Intent resultIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        String interval = SP.getString("sync_interval", "20");
        int sync_interval = Integer.parseInt(interval);

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, sync_interval * 60 * 1000);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    downloadFormList();
                }

            });
        }
    }

    /**
     * Starts the download task.
     */
    private void downloadFormList() {
        Log.d("BService", "Downloading Formlist");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, "There is no internet connection.", Toast.LENGTH_SHORT).show();
        } else {
            DownloadFileTask task = new DownloadFileTask(activity, "schools.csv");
            task.execute(SCHOOLS_URL);
            DownloadFileTask dates = new DownloadFileTask(activity, "dates.csv");
            dates.execute(DATES_URL);
            DownloadFileTask taskprogram = new DownloadFileTask(activity, "program.csv");
            taskprogram.execute(PROGRAM_URL);
        }
    }
}
