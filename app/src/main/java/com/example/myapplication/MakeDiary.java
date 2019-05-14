package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class MakeDiary extends Activity {

    TextView txtText;
    EditText title;
    EditText content;
    EditText summary;

    SQLiteDatabase sqliteDB;

    DiaryDBHelper dbHelper = null;
    String thisdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_diary);

        txtText = (EditText)findViewById(R.id.diary_et_date);

        Intent intent = getIntent();
        thisdate = intent.getStringExtra("date");
        txtText.setText(thisdate);

        title = (EditText)findViewById(R.id.diary_et_title);
        content = (EditText)findViewById(R.id.diary_et_content);
        summary = (EditText)findViewById(R.id.diary_et_summary);

        sqliteDB = init_database();
        init_tables();
        load_values();

        Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_values();
            }
        });

        Button buttonClear = (Button)findViewById(R.id.clearclear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values();
            }
        });

        Button buttonClose = (Button)findViewById(R.id.buttonclose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void mOnClose(){
        //데이터 전달하기
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    private SQLiteDatabase init_database(){
        SQLiteDatabase db = null;
        File file = new File(getFilesDir(),"diary.db");
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
        dbHelper = new DiaryDBHelper(this);
    }
    private void load_values() {
        if (sqliteDB != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM DIARY WHERE DATE = Date('" + thisdate + "')", null);
            while (cursor.moveToNext()) { // 레코드가 존재한다면,
                // no (INTEGER) 값 가져오기.
                title.setText(cursor.getString(1));

                content.setText(cursor.getString(2));

                summary.setText(cursor.getString(3));

            }
            cursor.close();
        }
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL(ContactDBCtrct.SQL_DELETE);

        String sqlInsert = "INSERT INTO DIARY " +
                "(DATE, TITLE, CONTENT, SUMMARY) VALUES (" +
                "'" + thisdate + "'," +
                "'" + title.getText()+ "',"+
                "'" + content.getText()+ "',"+
                "'" + summary.getText() + "')" ;

        db.execSQL(sqlInsert);
        mOnClose();
    }

    private void delete_values() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM DIARY WHERE DATE = Date('" + thisdate + "')");
        //db.execSQL(ContactDBCtrct.SQL_DROP_TBL);

        mOnClose();

    }



}
