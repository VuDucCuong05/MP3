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
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Song song = (Song) bundle.get("object_song");
            if(song != null){
                mSong = song;
                startMp3(mSong);
                sendNotification(mSong);
            }
        }
        // nhận dữ liệu action từ broadrecever (bắt sự kiến trên notificaiton)
        int actionMusic = intent.getIntExtra("action_music_service",0);
        handleActionMusic(actionMusic);
        return START_NOT_STICKY;
    }

    private void sendNotification(Song song) {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.mytam);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(song.getName())
                .setContentText(song.getSinger())
                .setLargeIcon(bitmap)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1,3)
                );
        if(isPlayIng){
            notificationBuilder
                    .addAction(R.drawable.ic_skip_previous, "Previous", null) // #0 sự kiện
                    .addAction(R.drawable.ic_pause_circle, "Pause", getPendingIntent(this,ACTION_PAUSE))  // #1
                    .addAction(R.drawable.ic_skip_next, "Next", null)   // #2
                    .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this,ACTION_CLEAR))// #3
                    .setContentIntent(pendingIntent);
        }else{
            notificationBuilder
                    .addAction(R.drawable.ic_skip_previous, "Previous", null) // #0 sự kiện
                    .addAction(R.drawable.ic_play, "Pause", getPendingIntent(this,ACTION_RESUME))  // #1
                    .addAction(R.drawable.ic_skip_next, "Next", null)  // #2
                    .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this,ACTION_CLEAR))
                    .setContentIntent(pendingIntent);

        }
        Notification notification = notificationBuilder.build();
        startForeground(1,notification);
    }

    // send data action to broadrecever
    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this, MyReceier.class);
        intent.putExtra("action_music",action);
        return PendingIntent.getBroadcast(context.getApplicationContext(),action,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
//                sendActiconToActivity(ACTION_CLEAR);
                break;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
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

    public void pauseMusic() {
        if(mediaPlayer != null && isPlayIng){
            mediaPlayer.pause();
            isPlayIng = false;
            // gửi lại notification để update view
            sendNotification(mSong);
            sendActiconToActivity(ACTION_PAUSE);
        }
    }
    public void resumeMusic() {
        if(mediaPlayer != null && !isPlayIng){
            mediaPlayer.start();
            isPlayIng = true;
            // gửi lại notification để update view
            sendNotification(mSong);
            sendActiconToActivity(ACTION_PAUSE);
        }
    }
    private void sendActiconToActivity(int action){
        Intent intent = new Intent("sendDataToActivity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song",mSong);
        bundle.putBoolean("status",isPlayIng);
        bundle.putInt("action_music",action);

        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
    public int getMusicDuration(){
        return mediaPlayer.getDuration();
    }
    public int getMusicCurPos(){
        if(mediaPlayer == null){
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }
    public void seekToPos(int pos){
        mediaPlayer.seekTo(pos);
    }

    public Song getmSong() {
        return mSong;
    }

    public boolean isPlayIng() {
        return isPlayIng;
    }
}
