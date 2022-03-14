package com.example.mp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    Button btStartMp3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btStartMp3 = findViewById(R.id.bt_start_mp3);
        btStartMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = new Song("Hẹn ước từ hư vô","Mỹ Tâm",R.drawable.mytam,R.raw.mytammp3);

                Intent intent = new Intent(MainActivity2.this,MainActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("object_song",song);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}