package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PopupActivity extends Activity {

    TextView txtText;

    SQLiteDatabase sqliteDB;

    private ArrayList<HashMap<String,String>> contentList = new ArrayList<HashMap<String,String>>();
    private ListView listView;
    private ScrollView qwe;
    private int scroll_height = 0;


    ContactDBHelper dbHelper = null;
    String thisdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        qwe =  (ScrollView)findViewById(R.id.scrollView);

        //UI 객체생성
        txtText = (EditText)findViewById(R.id.popup_et);

        //데이터 가져오기
        Intent intent = getIntent();
        thisdate = intent.getStringExtra("date");
        txtText.setText(thisdate);


        sqliteDB = init_database();
        init_tables();
        load_values();


        Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_values() ;
            }
        });
        Button buttonClear = (Button)findViewById(R.id.buttonClear) ;
        buttonClear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values() ;
            }
        });

        Button buttonclose = (Button)findViewById(R.id.close);
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
        EditText et1 = (EditText)findViewById(R.id.popup_et);
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

    public void mOnPopup_02(View v){
        save_values();
    }
    public void mOnPopup_03(View v){
        delete_values();
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
    private void load_values() {
        contentList = new ArrayList<HashMap<String,String>>();
        if (sqliteDB != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM SCHEDULE WHERE DATE = Date('" + thisdate + "')", null);
            scroll_height = 0;
            while (cursor.moveToNext()) { // 레코드가 존재한다면,
                HashMap<String,String> hashMap = new HashMap<>();
                // no (INTEGER) 값 가져오기.
                hashMap.put("Content",cursor.getString(0));
                hashMap.put("Date",cursor.getString(1));

                contentList.add(hashMap);
                if(scroll_height<510) {
                    scroll_height += 170;
                }
            }
            cursor.close();
        }

        listView = (ListView)findViewById(R.id.schedule_list);
        qwe.setMinimumHeight(scroll_height);

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,contentList,android.R.layout.simple_list_item_1,new String[]{"Content","Date"},new int[]{android.R.id.text2,android.R.id.text1}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv1 = (TextView)view.findViewById(android.R.id.text1);
                tv1.setTextColor(Color.BLACK);

                return view;
            }
        };
        listView.setAdapter(simpleAdapter);
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL(ContactDBCtrct.SQL_DELETE);

        EditText editTextName = (EditText) findViewById(R.id.editText2) ;
        String content = editTextName.getText().toString() ;

        String sqlInsert = "INSERT INTO SCHEDULE " +
                "(DATE, CONTENT) VALUES (" +
                "'" + thisdate + "'," +
                "'" + content + "')" ;

        db.execSQL(sqlInsert);
        mOnClose();
    }

    private void delete_values() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM SCHEDULE WHERE DATE = Date('" + thisdate + "')");
        //db.execSQL(ContactDBCtrct.SQL_DROP_TBL);

        mOnClose();

    }
}