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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button startButton = (Button) findViewById(R.id.timerButton);
        txtClicks = (TextView) findViewById(R.id.time_text_view);
        timer = new Timer();

        startButton.setOnClickListener(new MyOnClickListener(startButton));

    }

    class MyTimerTask extends TimerTask{
        long startTime = new Date().getTime();

        @Override
        public void run() {
            long elapsedSecs = (new Date().getTime() - startTime) / 1000;
            final long seconds = elapsedSecs % 60;
            final long minutes = elapsedSecs / 60;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    txtClicks.setText(String.format("%02d:%02d", minutes, seconds));
                    if (seconds == 10) {
                        Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                        timer.cancel();
                    }
                }
            });

        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final Button startButton;

        public MyOnClickListener(Button startButton) {
            this.startButton = startButton;
        }

        @Override
        public void onClick(View v) {
            Log.d("Timer", startButton.getText().toString());
            if (startButton.getText().toString().trim().equals("Start")){
                if (timer == null){
                    timer = new Timer();
                }
                timer.schedule(new MyTimerTask(), 0, 1000);
                startButton.setText("Stop");
            } else {
                startButton.setText("Start");
                timer.cancel();
            }
        }
    }
}
