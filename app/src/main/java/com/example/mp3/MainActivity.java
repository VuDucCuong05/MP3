package com.example.mp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

    // thực hình rằng buộn
    private MyServiceMp3 mMyServiceMp3;
    private boolean isPlaying;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyServiceMp3.MyBinder myBinder = (MyServiceMp3.MyBinder) iBinder;
//            thực hiện liên Myservice ở activity tới Myservice ỏ class Service
            mMyServiceMp3 = myBinder.getMyServiceMp3();
            Song song = new Song("Mỹ Tâm","Hẹn ước từ hư vô",R.drawable.mytam,R.raw.mytammp3);
            mMyServiceMp3.startMp3(song);
            isPlaying = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isPlaying= false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgPlayOrPause = findViewById(R.id.img_play_or_pause);
        imgClear = findViewById(R.id.img_clear);

        imgSong = findViewById(R.id.img_song);
        tvName = findViewById(R.id.tv_name);
        tvSong = findViewById(R.id.tv_song);

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickPause();
            }
        });
        imgSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlayPm3();
            }
        });
    }



    private void clickPause() {
        if(isPlaying){
            unbindService(mServiceConnection);
            isPlaying = false;
        }
    }

    private void clickPlayPm3() {

        Intent intent = new Intent(this,MyServiceMp3.class);
        bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}