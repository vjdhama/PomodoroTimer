package com.vjdhama.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String MINUTE = "minute";
    public static final String SECOND = "second";
    public static final String TIMER_STATUS = "TimerStatus";
    public static String BROADCAST_ACTION = "com.vjdhama.timer.TimerIntent";

    TextView txtClicks;
    Button startButton;
    BroadcastReceiver timerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.timerButton);
        txtClicks = (TextView) findViewById(R.id.time_text_view);

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean isTimerOn = intent.getBooleanExtra(TIMER_STATUS, false);
                if (isTimerOn) {
                    Long minutes = intent.getLongExtra(MINUTE, 0);
                    Long seconds = intent.getLongExtra(SECOND, 0);
                    txtClicks.setText(String.format("%02d:%02d", minutes, seconds));
                }
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, PomodoroService.class));
            }
        });

        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter updateTimer = new IntentFilter(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver, updateTimer);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("Main", " OnNewintent");

        boolean isFinished = getIntent().getBooleanExtra(MainActivity.TIMER_STATUS, true);

        Log.d("Main", String.valueOf(isFinished));

        if (isFinished)
            Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver);
    }

}
