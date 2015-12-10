package com.vjdhama.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vjdhama on 09/12/15.
 */
public class PomodoroService extends Service {

    public static final String PAMADORO_SERVICE = "PomodoroService";
    public static final String SERVICE_PREFERENCES = "ServicePreferences";
    public static final String START_TIME = "StartTime";
    public static final int NOTIFICATION_ID = 1;
    Timer timer;
    long startNewTime;
    boolean isTimerOn;
    SharedPreferences sharedPreferences;
    Notification notification;

    public PomodoroService() {
        Log.d(PAMADORO_SERVICE, " Pomodoro Service Constructor");
        this.timer = new Timer();
        this.isTimerOn = false;
    }

    private void startTimer() {

        Log.d(PAMADORO_SERVICE, " StartTimer");

        timer.schedule(new MyTimerTask(startNewTime), 0, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(PAMADORO_SERVICE, "OnStartCommand");

        if (isTimerOn) return START_STICKY;

        isTimerOn = true;
        sharedPreferences = getSharedPreferences(SERVICE_PREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.contains(START_TIME)) {
            startNewTime = sharedPreferences.getLong(START_TIME, new Date().getTime());
        } else {
            startNewTime = new Date().getTime();
            sharedPreferences.edit().putLong(START_TIME, startNewTime).apply();
        }
        startForgroundService("00:00");
        startTimer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startForgroundService(String message){

        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Pomodoro Service")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_stat_pomodoro)
                .setContentIntent(resultPendingIntent);

        notification = notificationBuilder.build();

        int notifyId = 1;
        notificationManager.notify(notifyId, notification);

        startForeground(NOTIFICATION_ID, notification);
    }

    public class MyTimerTask extends TimerTask {
        long startTime;

        public MyTimerTask(long startNewTime) {
            this.startTime = startNewTime;
        }

        @Override
        public void run() {
            long elapsedSecs = (new Date().getTime() - startTime) / 1000;
            final long seconds = elapsedSecs % 60;
            final long minutes = elapsedSecs / 60;
            Intent timerUpdateIntent = new Intent(MainActivity.BROADCAST_ACTION);

            if (seconds >= 9) {
                if (timer != null) {
                    Intent intent = new Intent(PomodoroService.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra(MainActivity.TIMER_STATUS, true);

                    Log.d(PAMADORO_SERVICE, String.valueOf(isTimerOn));

                    startActivity(intent);

                    timer.cancel();
                    sharedPreferences.edit().remove(START_TIME).apply();
                    stopForeground(true);
                }

                isTimerOn = false;

            }else {
                startForgroundService("Time " + minutes + ":" + seconds);
                timerUpdateIntent.putExtra(MainActivity.TIMER_STATUS, isTimerOn);
            }
            timerUpdateIntent.putExtra(MainActivity.MINUTE, minutes);
            timerUpdateIntent.putExtra(MainActivity.SECOND, seconds);

            Log.d(PAMADORO_SERVICE, " Minutes : " + minutes);
            Log.d(PAMADORO_SERVICE, " Seconds : " + seconds);

            LocalBroadcastManager.getInstance(PomodoroService.this).sendBroadcast(timerUpdateIntent);
        }
    }
}
