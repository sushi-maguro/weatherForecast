package com.websarva.wings.android.travelassist;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.core.os.HandlerCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherSearchActivity extends AppCompatActivity {

    //ログに記載するタグ用の文字列。
    private static final String DEBUG_TAG = "AsyncSample";
    //お天気情報のURL。
    private static final String WEATHERINFO_URL = "https://api.openweathermap.org/data/2.5/weather?";
    private static final String APP_ID = "79a7c8673b5fbf7a24ca3a7a447a93fc";
    private String accessUrl = "https://www.google.com/search?q=%E5%85%A8%E5%9B%BD%E3%81%AE%E8%A6%B3%E5%85%89%E5%9C%B0&rlz=1C5CHFA_enJP970JP970&sxsrf=AOaemvKJjbkuP8-KK00WMN6JIZfBVEUqWA%3A1642853465811&ei=WfTrYZrtMKiB1e8Popa62AY&oq=zennkokunokannkouti&gs_lcp=Cgdnd3Mtd2l6EAEYADIHCAAQgAQQBDIHCAAQgAQQBDIHCAAQgAQQBDIGCAAQBBAeOgcIIxDqAhAnOgQIIxAnOg0IABCABBCxAxCDARAEOgoIABCABBCxAxAEOgsIABCABBCxAxCDAToGCAAQBBADOgcIIxCxAhAnSgQIQRgASgQIRhgAUKYQWMYqYOQxaAFwAHgAgAFciAGfDJIBAjE5mAEAoAEBsAEKwAEB&sclient=gws-wiz";
    private WebView webView;


    /**
     * 緯度フィールド。
     */
    private double _latitude = 0;
    private double _latitude1 = 0;
    /**
     * 経度フィールド
     */
    private double _longitude = 0;
    private double _longitude1 = 0;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_search);
        Button button1 = findViewById(R.id.spotButton);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        // WebView
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.activity_tourist_spot);
                webView = findViewById(R.id.webView1);

                webView.getSettings().setJavaScriptEnabled(true);

                webView.getSettings().setDomStorageEnabled(true);

                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

                webView.loadUrl(accessUrl);
            }
        });
    }


    @UiThread
    private void receiveWeatherInfo1(final String urlFull){
        Looper mainLooper=Looper.getMainLooper();
        Handler handler= HandlerCompat.createAsync(mainLooper);
        WeatherInfoBackgroundReceiver backgroundReceiver = new WeatherInfoBackgroundReceiver(handler,urlFull);
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundReceiver);
    }

    @UiThread
    private void receiveWeatherInfo2(final String urlFull){
        Looper mainLooper=Looper.getMainLooper();
        Handler handler= HandlerCompat.createAsync(mainLooper);
        WeatherInfoBackgroundReceiver2 backgroundReceiver = new WeatherInfoBackgroundReceiver2(handler,urlFull);
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundReceiver);
    }


    private class WeatherInfoBackgroundReceiver implements Runnable{

        private final Handler _handler;
        private final String _urlFull;
        //コンストラクタ
        public WeatherInfoBackgroundReceiver(Handler handler,String urlFull){
            _handler=handler;
            _urlFull=urlFull;
        }

        @WorkerThread
        @Override
        public void run(){
            // HTTP接続を行うHttpURLConnectionオブジェクトを宣言。finallyで解放するためにtry外で宣言。
            HttpURLConnection con = null;
            // HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言。
            InputStream is = null;
            // 天気情報サービスから取得したJSON文字列。天気情報が格納されている。
            String result = "";
            try {
                // URLオブジェクトを生成。
                URL url = new URL(_urlFull);
                // URLオブジェクトからHttpURLConnectionオブジェクトを取得。
                con = (HttpURLConnection) url.openConnection();
                // 接続に使ってもよい時間を設定。
                con.setConnectTimeout(1000);
                // データ取得に使ってもよい時間。
                con.setReadTimeout(1000);
                // HTTP接続メソッドをGETに設定。
                con.setRequestMethod("GET");
                // 接続。
                con.connect();
                // HttpURLConnectionオブジェクトからレスポンスデータを取得。
                is = con.getInputStream();
                // レスポンスデータであるInputStreamオブジェクトを文字列に変換。
                result = is2String(is);
            }
            catch(MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            }
            // タイムアウトの場合の例外処理。
            catch(SocketTimeoutException ex) {
                Log.w(DEBUG_TAG, "通信タイムアウト", ex);
            }
            catch(IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            }
            finally {
                // HttpURLConnectionオブジェクトがnullでないなら解放。
                if(con != null) {
                    con.disconnect();
                }
                // InputStreamオブジェクトがnullでないなら解放。
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            WeatherInfoPostExecuter postExecuter=new WeatherInfoPostExecuter(result);
            _handler.post(postExecuter);
        }

        private String is2String(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while(0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            return sb.toString();
        }
    }

    private class WeatherInfoBackgroundReceiver2 implements Runnable{

        private final Handler _handler;
        private final String _urlFull;
        //コンストラクタ
        public WeatherInfoBackgroundReceiver2(Handler handler,String urlFull){
            _handler=handler;
            _urlFull=urlFull;
        }

        @WorkerThread
        @Override
        public void run(){
            // HTTP接続を行うHttpURLConnectionオブジェクトを宣言。finallyで解放するためにtry外で宣言。
            HttpURLConnection con = null;
            // HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言。
            InputStream is = null;
            // 天気情報サービスから取得したJSON文字列。天気情報が格納されている。
            String result = "";
            try {
                // URLオブジェクトを生成。
                URL url = new URL(_urlFull);
                // URLオブジェクトからHttpURLConnectionオブジェクトを取得。
                con = (HttpURLConnection) url.openConnection();
                // 接続に使ってもよい時間を設定。
                con.setConnectTimeout(1000);
                // データ取得に使ってもよい時間。
                con.setReadTimeout(1000);
                // HTTP接続メソッドをGETに設定。
                con.setRequestMethod("GET");
                // 接続。
                con.connect();
                // HttpURLConnectionオブジェクトからレスポンスデータを取得。
                is = con.getInputStream();
                // レスポンスデータであるInputStreamオブジェクトを文字列に変換。
                result = is2String(is);
            }
            catch(MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            }
            // タイムアウトの場合の例外処理。
            catch(SocketTimeoutException ex) {
                Log.w(DEBUG_TAG, "通信タイムアウト", ex);
            }
            catch(IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            }
            finally {
                // HttpURLConnectionオブジェクトがnullでないなら解放。
                if(con != null) {
                    con.disconnect();
                }
                // InputStreamオブジェクトがnullでないなら解放。
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            WeatherInfoPostExecuter2 postExecuter=new WeatherInfoPostExecuter2(result);
            _handler.post(postExecuter);
        }

        private String is2String(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while(0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            return sb.toString();
        }
    }


    //非同期でお天気情報を取得した後にUIスレッドでその情報を表示するためのクラス
    private class WeatherInfoPostExecuter implements Runnable{
        //取得したお天気情報JSON文字列
        private final String _result;

        //コンストラクタ
        public WeatherInfoPostExecuter(String result){
            _result=result;
        }

        @UiThread
        @Override
        public void run(){
            // 都市名。
            String cityName = "";
            // 天気。
            String weather = "";
            //気温
            String temp="";
            try {
                // ルートJSONオブジェクトを生成。
                JSONObject rootJSON = new JSONObject(_result);
                // 都市名文字列を取得。
                cityName = rootJSON.getString("name");
                // 緯度経度情報JSONオブジェクトを取得。
                JSONObject coordJSON = rootJSON.getJSONObject("coord");
                // 緯度情報文字列を取得。
                _latitude = coordJSON.getDouble("lat");
                // 経度情報文字列を取得。
                _longitude = coordJSON.getDouble("lon");
                // 天気情報JSON配列オブジェクトを取得。
                JSONArray weatherJSONArray = rootJSON.getJSONArray("weather");
                // 現在の天気情報JSONオブジェクトを取得。
                JSONObject weatherJSON = weatherJSONArray.getJSONObject(0);
                // 現在の天気情報文字列を取得。
                weather = weatherJSON.getString("description");

                JSONObject tempJSON=rootJSON.getJSONObject("main");
                temp=tempJSON.getString("temp");
            }
            catch(JSONException ex) {
                Log.e(DEBUG_TAG, "JSON解析失敗", ex);
            }

            // 画面に表示する「〇〇の天気」文字列を生成。
            String telop = getString(R.string.departure)+":"+cityName;
            // 天気の詳細情報を表示する文字列を生成。
            String desc = getString(R.string.bt_weather)+":"+weather+"\n"+getString(R.string.temp)+":"+temp+"℃";
            // 天気情報を表示するTextViewを取得。
            TextView tvWeatherTelop = findViewById(R.id.NameDep);
            TextView tvWeatherDesc = findViewById(R.id.weatherDep);
            // 天気情報を表示。
            tvWeatherTelop.setText(telop);
            tvWeatherDesc.setText(desc);
        }
    }

    private class WeatherInfoPostExecuter2 implements Runnable{
        //取得したお天気情報JSON文字列
        private final String _result;

        //コンストラクタ
        public WeatherInfoPostExecuter2(String result){
            _result=result;
        }

        @UiThread
        @Override
        public void run(){
            // 都市名。
            String cityName = "";
            // 天気。
            String weather = "";
            //気温
            String temp="";
            try {
                // ルートJSONオブジェクトを生成。
                JSONObject rootJSON = new JSONObject(_result);
                // 都市名文字列を取得。
                cityName = rootJSON.getString("name");
                // 緯度経度情報JSONオブジェクトを取得。
                JSONObject coordJSON = rootJSON.getJSONObject("coord");
                // 緯度情報文字列を取得。
                _latitude1 = coordJSON.getDouble("lat");
                // 経度情報文字列を取得。
                _longitude1 = coordJSON.getDouble("lon");
                // 天気情報JSON配列オブジェクトを取得。
                JSONArray weatherJSONArray = rootJSON.getJSONArray("weather");
                // 現在の天気情報JSONオブジェクトを取得。
                JSONObject weatherJSON = weatherJSONArray.getJSONObject(0);
                // 現在の天気情報文字列を取得。
                weather = weatherJSON.getString("description");

                JSONObject tempJSON=rootJSON.getJSONObject("main");
                temp=tempJSON.getString("temp");
            }
            catch(JSONException ex) {
                Log.e(DEBUG_TAG, "JSON解析失敗", ex);
            }

            // 画面に表示する「〇〇の天気」文字列を生成。
            String telop = getString(R.string.destination)+":"+cityName;
            // 天気の詳細情報を表示する文字列を生成。
            String desc = getString(R.string.bt_weather)+":"+weather+"\n"+getString(R.string.temp)+":"+temp+"℃";
            // 天気情報を表示するTextViewを取得。
            TextView tvWeatherTelop = findViewById(R.id.NameDes);
            TextView tvWeatherDesc = findViewById(R.id.weatherDes);
            tvWeatherTelop.setText(telop);
            tvWeatherDesc.setText(desc);

        }
    }

    public void on_put1(View view){
        EditText etSearchWord = findViewById(R.id.prefectureDes);
        String searchWord = etSearchWord.getText().toString();
        String q = searchWord;
        String urlFull=WEATHERINFO_URL+getString(R.string.language)+"&q="+q+"&appid="+APP_ID+"&units=metric";
        receiveWeatherInfo1(urlFull);
    }

    public void on_put2(View view){
        EditText etSearchWord = findViewById(R.id.prefectureDep);
        String searchWord = etSearchWord.getText().toString();
        String q = searchWord;
        String urlFull=WEATHERINFO_URL+getString(R.string.language)+"&q="+q+"&appid="+APP_ID+"&units=metric";
        receiveWeatherInfo2(urlFull);
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
    public void onMapShowCurrentButtonClick(View view) {
        // フィールドの緯度と経度の値をもとにマップアプリと連携するURI文字列を生成。
        String uriStr = "geo:" + _latitude + "," + _longitude;
        // URI文字列からURIオブジェクトを生成。
        Uri uri = Uri.parse(uriStr);
        // Intentオブジェクトを生成。
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // アクティビティを起動。
        startActivity(intent);
    }
    public void onMapShowCurrentButtonClick1(View view) {
        // フィールドの緯度と経度の値をもとにマップアプリと連携するURI文字列を生成。
        String uriStr = "geo:" + _latitude1 + "," + _longitude1;
        // URI文字列からURIオブジェクトを生成。
        Uri uri = Uri.parse(uriStr);
        // Intentオブジェクトを生成。
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // アクティビティを起動。
        startActivity(intent);
    }
}