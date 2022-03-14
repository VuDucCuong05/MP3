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
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    ImageView imgPlayOrPause;
    ImageView imgClear;

    ImageView imgSong;
    TextView tvName;
    TextView tvSong;
    SeekBar sbTimePm3;
    TextView tvTimeLeft;
    TextView tvTimeRight;
    private Handler handler;
    private Runnable updateTime;

    private Song mSong;

    // thực hình rằng buộn
    private MyServiceMp3 mMyServiceMp3;
    private boolean isServiceConnection;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyServiceMp3.MyBinder myBinder = (MyServiceMp3.MyBinder) iBinder;
//            thực hiện liên Myservice ở activity tới Myservice ỏ class Service
            mMyServiceMp3 = myBinder.getMyServiceMp3();
            isServiceConnection = true;
            showInforSong();
            // seekbar
            updateTimeSong();
            onStopTrackingTouch();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceConnection = false;
        }
    };
    // get data action of notification
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null){
                return;
            }
            // next song
//            mSong = (Song) bundle.getSerializable("object_song");
            showInforSong();

            //set status of button play or pause
            int actionMusic = bundle.getInt("action_music");
            handleMusic(actionMusic);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createViewMp3();

        // đăng ký lăng nghe activon ở service thông qua broadcast resever tới activity
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("sendDataToActivity"));

        // get 1 object to activity2
        getObjetcSong();
        // start
        clickStartSerive(mSong);
        // stop fore
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPause();
            }
        });
        // tuongw tacs
        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMyServiceMp3.isPlayIng()){
                    mMyServiceMp3.pauseMusic();
                }else{
                    mMyServiceMp3.resumeMusic();
                }
                setStatusImgViewPlayOrPause();
            }
        });

    }

    private void createViewMp3() {
        imgPlayOrPause = findViewById(R.id.img_play_or_pause);
        imgClear = findViewById(R.id.img_clear);
        imgSong = findViewById(R.id.img_song);
        tvName = findViewById(R.id.tv_name);
        tvSong = findViewById(R.id.tv_song);
        sbTimePm3 = findViewById(R.id.sb_mp3);
        tvTimeLeft = findViewById(R.id.tv_time_leff);
        tvTimeRight = findViewById(R.id.tv_time_right);
    }
    private void getObjetcSong() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Song song = (Song) bundle.get("object_song");
            if(song != null){
                mSong = (Song) bundle.getSerializable("object_song");
            }
        }
    }
    private void clickStartSerive(Song song) {
        Intent intent = new Intent(this,MyServiceMp3.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song",song);
        intent.putExtras(bundle);

        startService(intent);

        bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    private void clickPause() {
        Intent intent = new Intent(this,MyServiceMp3.class);
        stopService(intent);
        if(isServiceConnection){
            unbindService(mServiceConnection);
            isServiceConnection = false;
        }

        Intent intent1 = new Intent(MainActivity.this,MainActivity2.class);
        startActivity(intent1);
        finishAffinity();
    }

    private  void updateTimeSong() {
        if(isServiceConnection){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isServiceConnection){
                        if(sbTimePm3.getMax() == 100){
                            setTimeTotal();
                        }
                        int timeleff = mMyServiceMp3.getMusicCurPos();
                        sbTimePm3.setProgress(timeleff);
                        SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
                        tvTimeLeft.setText(dinhdang.format(timeleff));
                    }
                    handler.postDelayed(this,500);
                }
            },100);
        }
    }

    private  void setTimeTotal(){
        SimpleDateFormat dingDangGio = new SimpleDateFormat("mm:ss");
        tvTimeRight.setText(dingDangGio.format(mMyServiceMp3.getMusicDuration())+"");
        sbTimePm3.setMax(mMyServiceMp3.getMusicDuration());
    }

    private void onStopTrackingTouch() {
        sbTimePm3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMyServiceMp3.seekToPos(seekBar.getProgress());
            }
        });
    }

    private void handleMusic(int actionMusic) {
        switch (actionMusic){
            case MyServiceMp3.ACTION_START:
                setStatusImgViewPlayOrPause();
                break;
            case MyServiceMp3.ACTION_PAUSE:
                setStatusImgViewPlayOrPause();
                break;
            case MyServiceMp3.ACTION_RESUME:
                setStatusImgViewPlayOrPause();
                break;
            case MyServiceMp3.ACTION_CLEAR:
                clickPause();
                break;
        }
    }
    private void showInforSong()  {
        imgSong.setImageResource(mMyServiceMp3.getmSong().getImage());
        tvName.setText(mMyServiceMp3.getmSong().getName());
        tvSong.setText(mMyServiceMp3.getmSong().getSinger());
        setStatusImgViewPlayOrPause();
    }
    private void setStatusImgViewPlayOrPause(){
        if(mMyServiceMp3 == null){
            return;
        }
        if(mMyServiceMp3.isPlayIng()){
            imgPlayOrPause.setImageResource(R.drawable.ic_pause_circle);
        }else{
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }


}