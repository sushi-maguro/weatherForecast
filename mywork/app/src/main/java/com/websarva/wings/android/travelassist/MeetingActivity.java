package com.websarva.wings.android.travelassist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingActivity extends AppCompatActivity {
    private AlarmManager am;
    private PendingIntent pending;
    private int requestCode = 1;
    private final String[] spinnerItems = {"0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"};
    private TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner spinner = findViewById(R.id.hour);
        Spinner spinner1 = findViewById(R.id.minutes);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner1.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });


        Button buttonStart = this.findViewById(R.id.button4);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());


                String item=spinner.getSelectedItem().toString();
                int num = Integer.parseInt(item);
                String item1=spinner1.getSelectedItem().toString();
                int num1 = Integer.parseInt(item1);
                calendar.add(Calendar.SECOND ,num*3600+num1*60);

                Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);
                intent.putExtra("RequestCode",requestCode);

                pending = PendingIntent.getBroadcast(
                        getApplicationContext(),requestCode, intent, 0);

                am = (AlarmManager) getSystemService(ALARM_SERVICE);

                if (am != null) {
                    am.setExact(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), pending);

                    Toast.makeText(getApplicationContext(),
                            "alarm start", Toast.LENGTH_SHORT).show();

                    Log.d("debug", "start");
                }
            }
        });

        // アラームの取り消し
        Button buttonCancel = findViewById(R.id.button6);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent indent = new Intent(getApplicationContext(), AlarmNotification.class);
                PendingIntent pending = PendingIntent.getBroadcast(
                        getApplicationContext(), requestCode, indent, 0);

                AlarmManager am = (AlarmManager)MeetingActivity.this.
                        getSystemService(ALARM_SERVICE);
                if (am != null) {
                    am.cancel(pending);
                    Toast.makeText(getApplicationContext(),
                            "alarm cancel", Toast.LENGTH_SHORT).show();
                    Log.d("debug", "cancel");
                }
                else{
                    Log.d("debug", "null");
                }
            }
        });

        Button button = (Button)findViewById(R.id.button_mail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mailer 呼び出し
                callMailer();
            }
        });
    }

    //メール
    private void callMailer(){
        String[] addresses = {"xxx@yyy.zzz"};// 複数のアドレスを入れらる

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "test mail");
        intent.putExtra(Intent.EXTRA_TEXT, "本文です");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    public void onMapSearchButtonClick(View view) {
        // 入力欄に入力されたキーワード文字列を取得。
        EditText etSearchWord = findViewById(R.id.etSearchWord);
        String searchWord = etSearchWord.getText().toString();

        try {
            // 入力されたキーワードをURLエンコード。
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
            // マップアプリと連携するURI文字列を生成。
            String uriStr = "geo:0,0?q=" + searchWord;
            // URI文字列からURIオブジェクトを生成。
            Uri uri = Uri.parse(uriStr);
            // Intentオブジェクトを生成。
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // アクティビティを起動。
            startActivity(intent);
        }
        catch(UnsupportedEncodingException ex) {
            Log.e("MainActivity", "検索キーワード変換失敗", ex);
        }
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
