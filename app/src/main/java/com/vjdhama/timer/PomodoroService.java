package com.vjdhama.timer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vjdhama on 09/12/15.
 */
public class PomodoroService extends Service {

    public static final String PAMADORO_SERVICE = "PamadoroService";
    Timer timer;
    long startNewTime;
    boolean isTimerOn;

    public PomodoroService() {
        Log.d(PAMADORO_SERVICE," Pomodor Service Constructor");
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
        startNewTime = new Date().getTime();
        startTimer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

            if (minutes >= 1) {
                if (timer != null) {
                    timer.cancel();
                    stopSelf();
                }
                isTimerOn = false;

            }
            timerUpdateIntent.putExtra(MainActivity.MINUTE, minutes);
            timerUpdateIntent.putExtra(MainActivity.SECOND, seconds);
            timerUpdateIntent.putExtra(MainActivity.TIMER_STATUS, isTimerOn);

            Log.d(PAMADORO_SERVICE, " Minutes : " + minutes);
            Log.d(PAMADORO_SERVICE, " Seconds : " + seconds);

            LocalBroadcastManager.getInstance(PomodoroService.this).sendBroadcast(timerUpdateIntent);
        }
    }
}
