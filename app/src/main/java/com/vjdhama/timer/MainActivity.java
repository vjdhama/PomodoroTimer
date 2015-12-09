package com.vjdhama.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final String MINUTE = "minute";
    public static final String SECOND = "second";
    public static final String TIMER_STATUS = "TimerStatus";
    public static String BROADCAST_ACTION = "com.vjdhama.timer.TimerIntent";

    TextView txtClicks;
    Timer timer;
    long startNewTime;
    Button startButton;
    boolean isTimerOn = false;
    BroadcastReceiver timerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.timerButton);
        txtClicks = (TextView) findViewById(R.id.time_text_view);

        timer = new Timer();

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean isTimerOn = intent.getBooleanExtra(TIMER_STATUS, false);
                if (isTimerOn) {
                    Long minutes = intent.getLongExtra(MINUTE, 0);
                    Long seconds = intent.getLongExtra(SECOND, 0);
                    txtClicks.setText(String.format("%02d:%02d", minutes, seconds));
                } else {
                    Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                }
            }
        };

        startButton.setOnClickListener(new MyOnClickListener());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startNewTime);
        outState.putBoolean("Times", isTimerOn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startNewTime = savedInstanceState.getLong("startTime");
        isTimerOn = savedInstanceState.getBoolean("Times", false);

        if (isTimerOn) {
            startTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter updateTimer = new IntentFilter(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver, updateTimer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void startTimer() {
        timer.schedule(new MyTimerTask(startNewTime), 0, 1000);
    }

    class MyTimerTask extends TimerTask {
        long startTime;

        public MyTimerTask(long startNewTime) {
            this.startTime = startNewTime;
        }

        @Override
        public void run() {
            long elapsedSecs = (new Date().getTime() - startTime) / 1000;
            final long seconds = elapsedSecs % 60;
            final long minutes = elapsedSecs / 60;
            Intent timerUpdateIntent = new Intent(BROADCAST_ACTION);

            if (minutes >= 1) {
                if (timer != null) {
                    timer.cancel();
                }
                isTimerOn = false;

            }
            timerUpdateIntent.putExtra(MINUTE, minutes);
            timerUpdateIntent.putExtra(SECOND, seconds);
            timerUpdateIntent.putExtra(TIMER_STATUS, isTimerOn);
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(timerUpdateIntent);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isTimerOn) return;
            isTimerOn = true;
            startNewTime = new Date().getTime();
            startTimer();
        }
    }
}
