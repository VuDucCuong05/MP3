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
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Song song = (Song) bundle.get("data_song");
            if(song != null){
                startMp3(song);
                createNotification(song);
                mSong = song;

            }
        }
        int actionMusic = intent.getIntExtra("action_music_service",0);
        handleActionMusic(actionMusic);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void createNotification(Song song){
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),song.getImage());

        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.custon_notification_music);
        remoteView.setTextViewText(R.id.txt_title,song.getName());
        remoteView.setTextViewText(R.id.txt_single_song,song.getSinger());
        remoteView.setImageViewBitmap(R.id.img_song,bitmap);
        remoteView.setImageViewResource(R.id.img_previous,R.drawable.ic_skip_previous);


        remoteView.setImageViewResource(R.id.img_pause1,R.drawable.ic_pause_circle);

        if(isPlayIng){
            remoteView.setOnClickPendingIntent(R.id.img_pause1,getPendingIntent(this,ACTION_PAUSE));
            remoteView.setImageViewResource(R.id.img_pause1,R.drawable.ic_pause_circle);
        }else{
            remoteView.setOnClickPendingIntent(R.id.img_pause1,getPendingIntent(this,ACTION_RESUME));
            remoteView.setImageViewResource(R.id.img_pause1,R.drawable.ic_play);
        }

        remoteView.setOnClickPendingIntent(R.id.img_next1,getPendingIntent(this,ACTION_CLEAR));


        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteView)
                .setSound(null)
                .build();
        startForeground(1,notification);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.mytam);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_music)
//                .setContentTitle(song.getName())
//                .setContentText(song.getSinger())
//                .setLargeIcon(bitmap)
//
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(0,1,2,3)
//                        );
//        if(isPlayIng){
//            notificationBuilder
//                    .addAction(R.drawable.ic_skip_previous, "Previous", null) // #0 sự kiện
//                    .addAction(R.drawable.ic_pause_circle, "Pause", getPendingIntent(this,ACTION_PAUSE))  // #1
//                    .addAction(R.drawable.ic_skip_next, "Next", null)   // #2
//                    .addAction(R.drawable.ic_clear, "Clear", null);   // #2
//        }else{
//            notificationBuilder
//                    .addAction(R.drawable.ic_skip_previous, "Previous", null) // #0 sự kiện
//                    .addAction(R.drawable.ic_play, "Pause", getPendingIntent(this,ACTION_RESUME))  // #1
//                    .addAction(R.drawable.ic_skip_next, "Next", null)  // #2
//                    .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this,ACTION_CLEAR));   // #2
//        }
//        Notification notification = notificationBuilder.build();
//        startForeground(1,notification);
    }


    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this, MyReceier.class);
        intent.putExtra("action_music",action);
        return PendingIntent.getBroadcast(context.getApplicationContext(),action,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private void startMp3(Song song){
        if(mediaPlayer == null ){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),song.getResource());
        }
        mediaPlayer.start();
        isPlayIng = true;
        sendActiconToActivity(ACTION_START);
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
                sendActiconToActivity(ACTION_CLEAR);
                break;
        }
    }

    private void pauseMusic() {
        if(mediaPlayer != null && isPlayIng){
            mediaPlayer.pause();
            isPlayIng = false;
            createNotification(mSong);
            sendActiconToActivity(ACTION_PAUSE);
        }
    }
    private void resumeMusic() {
        if(mediaPlayer != null && !isPlayIng){
            mediaPlayer.start();
            isPlayIng = true;
            createNotification(mSong);
            sendActiconToActivity(ACTION_RESUME);
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



}
