package com.example.houston.weatherforcast;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView forecast;
    EditText cityName;
    String city;


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current =(char)data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                String message ="";
                ArrayList<String> dates = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray list = jsonObject.getJSONArray("list");
                for(int a=0; a<list.length();  a++){
                    JSONObject listObject = list.getJSONObject(a);
                    String description ="";
                    String date = "";
                    JSONArray weather = listObject.getJSONArray("weather");
                    JSONObject weather2 = weather.getJSONObject(0);
                    description = weather2.getString("description");
                    date = listObject.getString("dt_txt");
                   // dates.add(date);
                    if(description!=""){
                        message += "Time:  "+date+" Description: "+description+"\r\n \r\n";
                       Log.i("message",message);
                    }
                }
                if(message!=""){
                    forecast.setText(message);
                    Log.i("Dates / Times",dates.toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Could not query weather",Toast.LENGTH_LONG);
                }
            }catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not query weather",Toast.LENGTH_LONG);
            }
        }
    }

    public void findWeather(View view){
        city=cityName.getText().toString();
        //textView.setText(city);
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        try{
            String encodedCityName = URLEncoder.encode(city,"UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/forecast?q="+encodedCityName+"&appid=ca06f8bc9edb0481414e003b4a4894ec");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not query weather",Toast.LENGTH_LONG);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText)findViewById(R.id.cityName);
        textView = (TextView)findViewById(R.id.textView);
        forecast = (TextView)findViewById(R.id.forecast);
        forecast.setMovementMethod(new ScrollingMovementMethod());
    }
}
