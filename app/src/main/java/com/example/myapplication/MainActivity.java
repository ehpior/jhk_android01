package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick01(View v){
        Intent intent1 = new Intent(this, Main2Activity.class);
        EditText tv1 = findViewById(R.id.editText);
        EditText tv2 = findViewById(R.id.editText2);

        intent1.putExtra("String1",tv1.getText().toString());
        intent1.putExtra("String2",tv2.getText().toString());

        startActivityForResult(intent1,3000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case 3000:
                    TextView kk = findViewById(R.id.textView3);
                    kk.setText(data.getStringExtra("result"));
            }
        }
    }

}
