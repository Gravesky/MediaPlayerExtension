package com.example.musicplayerexc;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MyNotificationListenerService extends NotificationListenerService {

    private static final class ApplicationPackageNames {
        public static final String QQ_MUSIC = "com.tencent.qqmusic";
    }

    public static final class InterceptedNotificationCode {
        public static final int QQ_MUSIC_CODE = 1;
        public static final int OTHER_NOTIFICATIONS_CODE = 0; // We ignore all notification with code == 0
    }

    private final String TAG = "[SERVICE-DEBUG] ";
    private final String TAG1 = "[CLASS-NAME] ";

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "UNBIND.");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "REBIND.");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Listener created.");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"Listener is destroyed.");
        super.onDestroy();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        //当连接成功时调用，一般在开启监听后会回调一次该方法
        Log.i(TAG, "Listener connected");
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "Listener disconnecting");
        super.onListenerDisconnected();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"BIND");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //当收到一条消息时回调，sbn里面带有这条消息的具体信息

        Log.i(TAG, "Notification posted");

        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE){
            if (sbn.getNotification().bigContentView != null) {
                Log.i(TAG, "Matched notification found.");
                ViewGroup view = (ViewGroup) sbn.getNotification().bigContentView.apply(this, null);
                /*String pack = sbn.getPackageName();
                Bundle extras = sbn.getNotification().extras;
                int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);*/
                extractInfoFromView(view, notificationCode);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //当移除一条消息的时候回调，sbn是被移除的消息
        Log.i(TAG,"Notification removed");

        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        if (activeNotifications[i].getNotification().bigContentView != null) {
                            Log.i(TAG, "Matched notification found.");
                            ViewGroup view = (ViewGroup) activeNotifications[i].getNotification().bigContentView.apply(this, null);
                            /*String pack = sbn.getPackageName();
                            Bundle extras = sbn.getNotification().extras;
                            int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);*/
                            extractInfoFromView(view, notificationCode);
                            break;
                        }
                    }
                }
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.QQ_MUSIC)){
            return(InterceptedNotificationCode.QQ_MUSIC_CODE);
        }
        /*else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(InterceptedNotificationCode.INSTAGRAM_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(InterceptedNotificationCode.WHATSAPP_CODE);
        }*/
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }

    private void extractInfoFromView(ViewGroup theView, int appCode/*, int iconId, String pName*/) {
        TextView txMusciName = (TextView) ((ViewGroup) theView.getChildAt(1)).getChildAt(0);
        TextView txMusicSinger = (TextView) ((ViewGroup) theView.getChildAt(1)).getChildAt(1);
        String  qqMusicName = (String) txMusciName.getText();
        String  qqMuiscSinger = (String) txMusicSinger.getText();

        ImageView imgAlbumCover = (ImageView) ((ViewGroup) theView.getChildAt(0)).getChildAt(0);
        //Log.i(TAG,((ViewGroup) theView.getChildAt(0)).getChildAt(0).getAccessibilityClassName().toString());
        /*Drawable drawable = imgAlbumCover.getDrawable();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),drawable.getAlpha());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();*/

        Drawable drawable = imgAlbumCover.getDrawable();
        if(drawable == null){
            Log.i(TAG,"NO DRAWABLE GET.");
        }
        else{
            Log.i(TAG,"DRAWABLE GET.");
        }
        Bitmap bmp = convertToBitmap(drawable,200,200);
        byte[] product = bitmap2ByteArray(bmp);
        int check0 = product.length;
        Log.i(TAG,"Sending "+check0+" bytes.");
        //Log.i(TAG,"This img has "+image.getByteCount()+" byte.");
        // 此处因为这个服务比较特殊，拿到数据之后想要更新UI，则可以通过发广播的方式。
        Intent intent = new  Intent("com.example.musicplayerexc");
        intent.putExtra("Notification Code", appCode);
        intent.putExtra("qqMusicName", qqMusicName);
        intent.putExtra("qqMuiscSinger", qqMuiscSinger);
        intent.putExtra("imgCheck0", check0);
        intent.putExtra("albumCover", product);
        sendBroadcast(intent);

    }

    //DDMS not working very well, so this stupid thing might provides some help.
    //COUNT START DEFAULT AT 0
    private void viewInspectRecur(ViewGroup v, int count){
        int temp = count;
        if(v.getChildCount() > 0){
            for(int i = 0; i<v.getChildCount(); i++){
                ViewGroup subChild = (ViewGroup) v.getChildAt(i);
                CharSequence result = subChild.getChildAt(i).getAccessibilityClassName();
                Log.i(TAG1,"Child at LAYER - "+temp+" index "+i+" is  "+result.toString());
                viewInspectRecur(subChild, temp+1);
            }
        }
    }

    //Source:https://msol.io/blog/android/android-convert-drawable-to-bitmap/
    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public byte[] bitmap2ByteArray(Bitmap bmp){
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        return bStream.toByteArray();
    }

}



