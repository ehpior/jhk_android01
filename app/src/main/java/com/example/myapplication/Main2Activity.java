package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent_get = getIntent();
        TextView tv1 = findViewById(R.id.textView);
        TextView tv2 = findViewById(R.id.textView2);

        tv1.setText(intent_get.getStringExtra("String1"));
        tv2.setText(intent_get.getStringExtra("String2"));
    }

    public void onClick_back(View v){
        Intent resultintent = new Intent();
        TextView tv1 = findViewById(R.id.textView);
        TextView tv2 = findViewById(R.id.textView2);
        String tmp = tv1.getText().toString() + tv2.getText().toString();
        resultintent.putExtra("result",tmp);
        setResult(RESULT_OK,resultintent);
        finish();
    }
}
