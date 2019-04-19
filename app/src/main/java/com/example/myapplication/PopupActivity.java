package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;

import java.io.File;

public class PopupActivity extends Activity {

    TextView txtText;

    SQLiteDatabase sqliteDB;

    ContactDBHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        txtText.setText(data);


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
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
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
        File file = new File(getFilesDir(),"contact.db");
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

        if (sqliteDB != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT, null);

            if (cursor.moveToNext()) { // 레코드가 존재한다면,
                // no (INTEGER) 값 가져오기.
                int no = cursor.getInt(0) ;
                EditText editTextNo = (EditText) findViewById(R.id.editTextNo) ;
                editTextNo.setText(Integer.toString(no)) ;

                // name (TEXT) 값 가져오기
                String name = cursor.getString(1) ;
                EditText editTextName = (EditText) findViewById(R.id.editTextName) ;
                editTextName.setText(name) ;

                // phone (TEXT) 값 가져오기
                String phone = cursor.getString(2) ;
                EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone) ;
                editTextPhone.setText(phone) ;

                // over20 (INTEGER) 값 가져오기.
                int over20 = cursor.getInt(3) ;
                CheckBox checkBoxOver20 = (CheckBox) findViewById(R.id.checkBoxOver20) ;
                if (over20 == 0) {
                    checkBoxOver20.setChecked(false) ;
                } else {
                    checkBoxOver20.setChecked(true) ;
                }
            }
        }
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(ContactDBCtrct.SQL_DELETE);

        EditText editTextNo = (EditText) findViewById(R.id.editTextNo) ;
        int no = Integer.parseInt(editTextNo.getText().toString());

        EditText editTextName = (EditText) findViewById(R.id.editTextName) ;
        String name = editTextName.getText().toString() ;

        EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone) ;
        String phone = editTextPhone.getText().toString() ;

        CheckBox checkBoxOver20 = (CheckBox) findViewById(R.id.checkBoxOver20) ;
        boolean isOver20 = checkBoxOver20.isChecked() ;

        String sqlInsert = "INSERT INTO CONTACT_T " +
                "(NO, NAME, PHONE, OVER20) VALUES (" +
                Integer.toString(no) + "," +
                "'" + name + "'," +
                "'" + phone + "'," +
                ((isOver20 == true) ? "1" : "0") + ")" ;

        db.execSQL(sqlInsert);
    }

    private void delete_values() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(ContactDBCtrct.SQL_DELETE);

        EditText editTextNo = (EditText) findViewById(R.id.editTextNo) ;
        editTextNo.setText("") ;

        EditText editTextName = (EditText) findViewById(R.id.editTextName) ;
        editTextName.setText("") ;

        EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone) ;
        editTextPhone.setText("") ;

        CheckBox checkBoxOver20 = (CheckBox) findViewById(R.id.checkBoxOver20) ;
        checkBoxOver20.setChecked(false) ;

    }
}