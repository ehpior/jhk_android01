package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class make_diary_select_weather extends Activity implements View.OnClickListener {
    ImageView weather1;
    ImageView weather2;
    ImageView weather3;
    ImageView weather4;
    ImageView weather5;
    ImageView weather6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_diary_select_weather);
        weather1 = (ImageView)findViewById(R.id.weather_1);
        weather2 = (ImageView)findViewById(R.id.weather_2);
        weather3 = (ImageView)findViewById(R.id.weather_3);
        weather4 = (ImageView)findViewById(R.id.weather_4);
        weather5 = (ImageView)findViewById(R.id.weather_5);
        weather6 = (ImageView)findViewById(R.id.weather_6);
        weather1.setOnClickListener(this);
        weather2.setOnClickListener(this);
        weather3.setOnClickListener(this);
        weather4.setOnClickListener(this);
        weather5.setOnClickListener(this);
        weather6.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()) {
            case R.id.weather_1:
                intent.putExtra("no", 1);
                break;
            case R.id.weather_2:
                intent.putExtra("no", 2);
                break;
            case R.id.weather_3:
                intent.putExtra("no", 3);
                break;
            case R.id.weather_4:
                intent.putExtra("no", 4);
                break;
            case R.id.weather_5:
                intent.putExtra("no", 5);
                break;
            case R.id.weather_6:
                intent.putExtra("no", 6);
                break;
        }
        setResult(RESULT_OK,intent);
        finish();
    }
}
