package com.websarva.wings.android.travelassist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //「待ち合わせ」ボタンがクリックされたときの処理
    public void meetingClick(View view) {
        Intent intent=new Intent(MainActivity.this,MeetingActivity.class);
        startActivity(intent);
    }

    //「お天気ボタン」がクリックされたときの処理
    public void weatherClick(View view) {
        Intent intent=new Intent(MainActivity.this,WeatherSearchActivity.class);
        startActivity(intent);
    }
}