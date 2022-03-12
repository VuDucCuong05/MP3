package com.example.mp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView imgPlayOrPause;
    ImageView imgClear;

    ImageView imgSong;
    TextView tvName;
    TextView tvSong;

    private Song mSong;
    private boolean isPlaying;

    // nhận intent từ notification
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null){
                return;
            }
            mSong = (Song) bundle.getSerializable("object_song");
            isPlaying = bundle.getBoolean("status");
            int actionMusic = bundle.getInt("action_music");

            handleMusic(actionMusic);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // đăng ký lăng nghe
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("sendDataToActivity"));


        imgPlayOrPause = findViewById(R.id.img_play_or_pause);
        imgClear = findViewById(R.id.img_clear);

        imgSong = findViewById(R.id.img_song);
        tvName = findViewById(R.id.tv_name);
        tvSong = findViewById(R.id.tv_song);

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlayPm3();
            }
        });
    }



    private void clickPause() {
        Intent intent = new Intent(this,MyServiceMp3.class);
        stopService(intent);
    }

    private void clickPlayPm3() {
        Song song = new Song("Mỹ Tâm","Hẹn ước từ hư vô",R.drawable.mytam,R.raw.mytammp3);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data_song",song);

        Intent intent = new Intent(this,MyServiceMp3.class);
        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void handleMusic(int actionMusic) {
        switch (actionMusic){
            case MyServiceMp3.ACTION_START:
                showInforSong();
                setSatusButtonPlayOrPause();
                break;
            case MyServiceMp3.ACTION_PAUSE:
                setSatusButtonPlayOrPause();
                break;
            case MyServiceMp3.ACTION_RESUME:
                setSatusButtonPlayOrPause();
                break;
            case MyServiceMp3.ACTION_CLEAR:
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent);
                break;
        }
    }

    private void showInforSong() {
        if(mSong == null){
            return;
        }
        imgSong.setImageResource(mSong.getImage());
        tvName.setText(mSong.getName());
        tvSong.setText(mSong.getSinger());
        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    sendActionToService(MyServiceMp3.ACTION_PAUSE);
                }else{
                    sendActionToService(MyServiceMp3.ACTION_RESUME);

                }
            }
        });
        // clear

    }
    private void setSatusButtonPlayOrPause(){
        if(isPlaying){
            imgPlayOrPause.setImageResource(R.drawable.ic_pause_circle);
        }else{
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }
    private void sendActionToService(int action){
        Intent intent = new Intent(this,MyServiceMp3.class);
        intent.putExtra("action_music_service",action);
        startService(intent);
    }

}