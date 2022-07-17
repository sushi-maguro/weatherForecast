package com.websarva.wings.android.travelassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class TouristSpotActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spot);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    //戻るボタンが記述されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal=true;
        int itemId=item.getItemId();
        if (itemId==android.R.id.home){
            finish();
        }else{
            returnVal=super.onOptionsItemSelected(item);
        }
        return returnVal;
    }
}