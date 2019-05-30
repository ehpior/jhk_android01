package com.example.myapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

public class MakeSchedule extends AppCompatActivity {

    SQLiteDatabase sqliteDB;
    TextView txtText;

    ContactDBHelper dbHelper = null;
    String thisdate = new String();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_schedule);

        txtText = (EditText)findViewById(R.id.schedule_et_date);

        Intent intent = getIntent();
        thisdate = intent.getStringExtra("date");
        txtText.setText(thisdate);

        sqliteDB = init_database();
        init_tables();

        ImageButton buttonSave = (ImageButton)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_values() ;
            }
        });
        ImageButton buttonclose = (ImageButton)findViewById(R.id.buttonclose);
        buttonclose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });

        Button clear = (Button)findViewById(R.id.clearclear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values();
            }
        });
    }

    public void mOnClose(){
        //데이터 전달하기
        Intent intent = new Intent();
        EditText et1 = (EditText)findViewById(R.id.schedule_et_title);
        intent.putExtra("result", et1.getText().toString());
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    private SQLiteDatabase init_database(){
        SQLiteDatabase db = null;
        File file = new File(getFilesDir(),"schedule.db");
        System.out.println("PATH : " + file.toString());
        try{
            db = SQLiteDatabase.openOrCreateDatabase(file,null);
        } catch(SQLiteException e){
            e.printStackTrace();
        }
        if(db == null){
            System.out.println("DB creation failed. " + file.getAbsolutePath());
        }
        return db;
    }
    private void init_tables(){
        dbHelper = new ContactDBHelper(this);
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL(ContactDBCtrct.SQL_DROP_TBL);

        EditText editTextName = (EditText) findViewById(R.id.schedule_et_title) ;
        EditText editdate = (EditText) findViewById(R.id.schedule_et_date) ;
        String content = editTextName.getText().toString() ;
        String date = editdate.getText().toString() ;

        String sqlInsert = "INSERT INTO SCHEDULE " +
                "(DATE, CONTENT) VALUES (" +
                "'" + date + "'," +
                "'" + content + "')" ;

        db.execSQL(sqlInsert);
        mOnClose();
    }
    private void delete_values() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        EditText editdate = (EditText) findViewById(R.id.schedule_et_date) ;
        String date = editdate.getText().toString() ;

        db.execSQL("DELETE FROM SCHEDULE WHERE DATE = Date('" + date + "')");
        //db.execSQL(ContactDBCtrct.SQL_DROP_TBL);

        mOnClose();

    }
}


