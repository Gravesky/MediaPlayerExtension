package com.example.musicplayerexc;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.service.notification.NotificationListenerService.requestRebind;

public class MainActivity extends AppCompatActivity {

    //Initialize variable
    TextView playerPosition,playerDuration,songName,artistName;
    SeekBar seekBar;
    ImageView btRew,btFf,btPlay,btPause,btPrev,btNext;

    /*MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;*/

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private ImageView interceptedNotificationImageView;
    private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;

    private final String TAG = "[DEBUG] ";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"OnCreate was invoked.");

        //Assign variable
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.artist_name);
        seekBar = findViewById(R.id.seek_bar);
        btRew = findViewById(R.id.bt_rew);
        btFf = findViewById(R.id.bt_ff);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);
        btNext = findViewById(R.id.bt_next);
        btPrev = findViewById(R.id.bt_prev);
        // Here we get a reference to the image we will modify when a notification is received
        interceptedNotificationImageView = (ImageView) this.findViewById(R.id.intercepted_notification_logo);

        //Trigger for rebind


        //Initialize media player
        //mediaPlayer = MediaPlayer.create(this,R.raw.dynamite);

        /*//Audio manager
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.isMusicActive();*/

        // If the user did not turn the notification listener service on we prompt him to do so
        if(!isNotificationServiceEnabled()){
            Log.i(TAG,"Notification access is NOT granted!");
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        else{
            Log.i(TAG,"Notification access is GRANTED!");
        }

        toggleNotificationListenerService();

        // Finally we register a receiver to tell the MainActivity when a notification has been received
        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.musicplayerexc"); //package name
        registerReceiver(imageChangeBroadcastReceiver,intentFilter);

        //Initialize runnable
        /*runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"Running...");
                //Set progress on seek bar
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                //Handler post delay for 0.5 second
                handler.postDelayed(this, 500);

            }
        };*/

        /*//Get duration of media player
        int duration = mediaPlayer.getDuration();
        //Convert millisecond to minutes and second
        String sDuration = convertFormat(duration);
        //Set duration on text view
        playerDuration.setText(sDuration);*/

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide play button
                btPlay.setVisibility(View.GONE);
                //Show pause button
                btPause.setVisibility(View.VISIBLE);
                /*//Start media player
                mediaPlayer.start();
                //Set max on seek bar;
                seekBar.setMax(mediaPlayer.getDuration());
                //Start handler
                handler.postDelayed(runnable, 0);*/
                BaseInputConnection mInputConnection = new BaseInputConnection(v, true);
                mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide pause button
                btPause.setVisibility(View.GONE);
                //Show play button
                btPlay.setVisibility(View.VISIBLE);
                /*//pause media player
                mediaPlayer.pause();
                //Stop handler
                handler.removeCallbacks(runnable);*/
                BaseInputConnection mInputConnection = new BaseInputConnection(v, true);
                mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
            }
        });

        btFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*//Get current position of media player
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
                }*/
            }
        });

        btRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*//Get current position of media player
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
                }*/
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO FINISH SKIP NEXT
                BaseInputConnection mInputConnection = new BaseInputConnection(v, true);
                mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
            }
        });

        btPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO FINISH SKIP BACK
                BaseInputConnection mInputConnection = new BaseInputConnection(v, true);
                mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*//Check condition
                if(fromUser){
                    //When drag the seek bar
                    //Set progress on seek bar
                    mediaPlayer.seekTo(progress);
                }
                //Set current position on text view
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));*/
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Hide pause button
                btPause.setVisibility(View.GONE);
                //Show play button
                btPlay.setVisibility(View.VISIBLE);
                //Set media player to initial position
                mediaPlayer.seekTo(0);

            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, com.example.musicplayerexc.MyNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        unregisterReceiver(imageChangeBroadcastReceiver);
        Log.i(TAG,"Closing...");
    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    /**
     * Change Intercepted Notification Image
     * Changes the MainActivity image based on which notification was intercepted
     * @param notificationCode The intercepted notification code
     */
    private void updateView(int notificationCode, String recvSongName, String recvArtistName, Bitmap cover){
        switch(notificationCode){
            case MyNotificationListenerService.InterceptedNotificationCode.QQ_MUSIC_CODE:
                interceptedNotificationImageView.setImageResource(R.drawable.ic_qq_music);
                songName.setText(recvSongName);
                artistName.setText(recvArtistName);

                interceptedNotificationImageView.setImageBitmap(cover);
                break;
            case MyNotificationListenerService.InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE:
                interceptedNotificationImageView.setImageResource(R.drawable.ic_sick);
                break;
        }
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if enabled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Image Change Broadcast Receiver.
     * We use this Broadcast Receiver to notify the Main Activity when
     * a new notification has arrived, so it can properly change the
     * notification image
     * */
    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
            String receivedSongName = intent.getStringExtra("qqMusicName");
            String receivedArtistName = intent.getStringExtra("qqMuiscSinger");
            int check0 = intent.getIntExtra("imgCheck0",-1);
            byte[] byteArray = intent.getByteArrayExtra("albumCover");
            if(byteArray.length != check0){
                Log.i(TAG,byteArray.length+" not equal to "+check0);
            }
            Bitmap albumCover = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Log.i(TAG,albumCover.getByteCount()+" byte");

            updateView(receivedNotificationCode,receivedSongName,receivedArtistName,albumCover);
        }
    }

    private Bitmap getCoverBitMap(String fName){
        Bitmap bmp = null;
        try {
            FileInputStream is = this.openFileInput(fName);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bmp == null){
            Log.i(TAG,"Output bitmap is NULL.");
        }
        return bmp;
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }

    //重新开启NotificationMonitor
    private void toggleNotificationListenerService() {
        Log.i(TAG,"Rebind triggered.");
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, com.example.musicplayerexc.MyNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, com.example.musicplayerexc.MyNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


}