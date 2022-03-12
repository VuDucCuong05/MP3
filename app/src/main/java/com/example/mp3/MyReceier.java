package com.example.mp3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action_music",0);
        Intent intentService = new Intent(context,MyServiceMp3.class);
        intentService.putExtra("action_music_service",actionMusic);
        context.startService(intentService);
    }
}
