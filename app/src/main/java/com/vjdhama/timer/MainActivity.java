package com.vjdhama.timer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView txtClicks;
    Timer timer;
    long startNewTime;
    Button startButton;
    boolean isTimerOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.timerButton);
        txtClicks = (TextView) findViewById(R.id.time_text_view);
        timer = new Timer();

        startButton.setOnClickListener(new MyOnClickListener());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startNewTime);
        Log.d("Timer", "new " + startNewTime);
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtClicks.setText(String.format("%02d:%02d", minutes, seconds));
                    if (minutes == 1) {
                        Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                        timer.cancel();
                        isTimerOn = false;
                    }
                }
            });
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
