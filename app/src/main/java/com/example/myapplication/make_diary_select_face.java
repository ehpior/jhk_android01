package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class make_diary_select_face extends Activity implements View.OnClickListener{

    ImageView face1;
    ImageView face2;
    ImageView face3;
    ImageView face4;
    ImageView face5;
    ImageView face6;
    ImageView face7;
    ImageView face8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_diary_select_face);
        face1 = (ImageView)findViewById(R.id.face_1);
        face2 = (ImageView)findViewById(R.id.face_2);
        face3 = (ImageView)findViewById(R.id.face_3);
        face4 = (ImageView)findViewById(R.id.face_4);
        face5 = (ImageView)findViewById(R.id.face_5);
        face6 = (ImageView)findViewById(R.id.face_6);
        //face7 = (ImageView)findViewById(R.id.face_7);
        //face8 = (ImageView)findViewById(R.id.face_8);
        face1.setOnClickListener(this);
        face2.setOnClickListener(this);
        face3.setOnClickListener(this);
        face4.setOnClickListener(this);
        face5.setOnClickListener(this);
        face6.setOnClickListener(this);
        //face7.setOnClickListener(this);
        //face8.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()) {
            case R.id.face_1:
                intent.putExtra("no", 1);
                break;
            case R.id.face_2:
                intent.putExtra("no", 2);
                break;
            case R.id.face_3:
                intent.putExtra("no", 3);
                break;
            case R.id.face_4:
                intent.putExtra("no", 4);
                break;
            case R.id.face_5:
                intent.putExtra("no", 5);
                break;
            case R.id.face_6:
                intent.putExtra("no", 6);
                break;
            /*case R.id.face_7:
                intent.putExtra("no", 7);
                break;
            case R.id.face_8:
                intent.putExtra("no", 8);
                break;*/
        }
        setResult(RESULT_OK,intent);
        finish();
    }
}
