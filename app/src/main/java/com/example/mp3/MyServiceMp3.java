package com.example.mp3;

import static com.example.mp3.MyAppication.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MyServiceMp3 extends Service {

    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;

    private MediaPlayer mediaPlayer;
    private boolean isPlayIng;
    private Song mSong;

    private MyBinder myBinder = new MyBinder();
    public class MyBinder extends Binder{
        MyServiceMp3 getMyServiceMp3(){
            return  MyServiceMp3.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public void startMp3(Song song){
        if(mediaPlayer == null ){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),song.getResource());
        }
        mediaPlayer.start();
        isPlayIng = true;
    }

    private void pauseMusic() {
        if(mediaPlayer != null && isPlayIng){
            mediaPlayer.pause();
            isPlayIng = false;
        }
    }
    private void resumeMusic() {
        if(mediaPlayer != null && !isPlayIng){
            mediaPlayer.start();
            isPlayIng = true;
        }
    }


}
