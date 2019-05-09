package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PopupActivity2 extends Activity {

    TextView txtText;
    EditText title;
    EditText content;

    SQLiteDatabase sqliteDB;

    private ArrayList<HashMap<String,String>> contentList = new ArrayList<HashMap<String,String>>();

    DiaryDBHelper dbHelper = null;
    String thisdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup2);

        //UI 객체생성
        txtText = (EditText)findViewById(R.id.popup_diary);

        //데이터 가져오기
        Intent intent = getIntent();
        thisdate = intent.getStringExtra("date");
        txtText.setText(thisdate);

        title = (EditText)findViewById(R.id.et_diary_title);
        content = (EditText)findViewById(R.id.et_diary_content);


        sqliteDB = init_database();
        init_tables();
        load_values();


        Button buttonSave = (Button)findViewById(R.id.buttonSave_diary);
        buttonSave.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                save_values();
            }
        });

        Button buttonClear = (Button)findViewById(R.id.buttonClear_diary) ;
        buttonClear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values() ;
            }
        });

        Button buttonclose = (Button)findViewById(R.id.close_diary);
        buttonclose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    //확인 버튼 클릭
    public void mOnClose(){
        //데이터 전달하기
        Intent intent = new Intent();
        EditText et1 = (EditText)findViewById(R.id.popup_diary);
        intent.putExtra("result", et1.getText().toString());
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    public void mOnPopup_02_diary(View v){
        save_values();
    }
    public void mOnPopup_03_diary(View v){
        delete_values();
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

            }
            cursor.close();
        }
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL(ContactDBCtrct.SQL_DELETE);

        String sqlInsert = "INSERT INTO DIARY " +
                "(DATE, TITLE, CONTENT) VALUES (" +
                "'" + thisdate + "'," +
                "'" + title.getText()+ "',"+
                "'" + content.getText() + "')" ;

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
