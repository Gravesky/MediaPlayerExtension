package com.example.musicplayerexc;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //Initialize variable
    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btRew,btFf,btPlay,btPause,btPrev,btNext;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seek_bar);
        btRew = findViewById(R.id.bt_rew);
        btFf = findViewById(R.id.bt_ff);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);
        btNext = findViewById(R.id.bt_next);
        btPrev = findViewById(R.id.bt_prev);

        //Initialize media player
        mediaPlayer = MediaPlayer.create(this,R.raw.dynamite);

        //Initialize runnable
        runnable = new Runnable() {
            @Override
            public void run() {
                //Set progress on seek bar
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                //Handler post delay for 0.5 second
                handler.postDelayed(this, 500);

            }
        };

        //Get duration of media player
        int duration = mediaPlayer.getDuration();
        //Convert millisecond to minutes and second
        String sDuration = convertFormat(duration);
        //Set duration on text view
        playerDuration.setText(sDuration);

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide play button
                btPlay.setVisibility(View.GONE);
                //Show pause button
                btPause.setVisibility(View.VISIBLE);
                //Start media player
                mediaPlayer.start();
                //Set max on seek bar;
                seekBar.setMax(mediaPlayer.getDuration());
                //Start handler
                handler.postDelayed(runnable, 0);
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide pause button
                btPause.setVisibility(View.GONE);
                //Show play button
                btPlay.setVisibility(View.VISIBLE);
                //pause media player
                mediaPlayer.pause();
                //Stop handler
                handler.removeCallbacks(runnable);
            }
        });

        btFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get current position of media player
                int currentPosition = mediaPlayer.getCurrentPosition();
                //Get duration of media player
                int duration = mediaPlayer.getDuration();
                //Check condition
                if(mediaPlayer.isPlaying() && duration != currentPosition){
                    //When media is playing and duration is not equal to current position
                    //Fast forward for 5 seconds
                    currentPosition = currentPosition + 5000;
                    //Set current position on text view
                    playerPosition.setText(convertFormat(currentPosition));
                    //Set progress on seek bar
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get current position of media player
                int currentPosition = mediaPlayer.getCurrentPosition();
                //Check condition
                if(mediaPlayer.isPlaying() && currentPosition> 5000){
                    //When media is playing and current position in greaterthan5
                    //Rewind for 5 seconds
                    currentPosition = currentPosition - 5000;
                    //Set current position on text view
                    playerPosition.setText(convertFormat(currentPosition));
                    //Set progress on seek bar
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Check condition
                if(fromUser){
                    //When drag the seek bar
                    //Set progress on seek bar
                    mediaPlayer.seekTo(progress);
                }
                //Set current position on text view
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Hide pause button
                btPause.setVisibility(View.GONE);
                //Show play button
                btPlay.setVisibility(View.VISIBLE);
                //Set media player to initial position
                mediaPlayer.seekTo(0);

            }
        });
    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d"
        , TimeUnit.MILLISECONDS.toMinutes(duration)
        , TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}