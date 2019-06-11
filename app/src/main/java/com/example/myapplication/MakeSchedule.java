package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.util.Random;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;

public class MakeSchedule extends AppCompatActivity implements  View.OnClickListener, ColorPickerDialogListener {

    SQLiteDatabase sqliteDB;
    TextView txtText;
    EditText editTextName;
    EditText editdate;
    View qwer;
    LinearLayout qwer2;

    ContactDBHelper dbHelper = null;
    String thisdate = new String();
    String sch_content = "";
    int color_final=0;

    InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_schedule);

        txtText = (EditText)findViewById(R.id.schedule_et_date);

        Intent intent = getIntent();
        thisdate = intent.getStringExtra("date");
        txtText.setText(thisdate);

        sch_content = intent.getStringExtra("content");

        editTextName = (EditText) findViewById(R.id.schedule_et_title) ;
        editdate = (EditText) findViewById(R.id.schedule_et_date) ;
        qwer = (View)findViewById(R.id.prac_bt);
        qwer2 = (LinearLayout) findViewById(R.id.prac_lay);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sqliteDB = init_database();
        init_tables();
        load_values();

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

        ImageButton buttondelete = (ImageButton)findViewById(R.id.buttondelete);
        buttondelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values();
            }
        });


        qwer2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        final int id = v.getId();
        hideKeyboard();
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(DIALOG_PRESET_ID)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this);
    }
    private static final int DIALOG_DEFAULT_ID = 0;
    private static final int DIALOG_PRESET_ID = 1;

    public void mOnClose(){
        //데이터 전달하기
        Intent intent = new Intent();
        EditText et1 = (EditText)findViewById(R.id.schedule_et_title);
        intent.putExtra("result", et1.getText().toString());
        setResult(RESULT_OK, intent);
        ((MainActivity)MainActivity.mContext).grid_notifychange();

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
        Random random = new Random();
        qwer.setBackgroundColor(Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
        color_final = ((ColorDrawable)qwer.getBackground()).getColor();
        return db;
    }
    private void init_tables(){
        dbHelper = new ContactDBHelper(this);
    }

    private void load_values() {

        editTextName.setText(sch_content);
        editdate.setText(thisdate);

        /*if (sqliteDB != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM DIARY WHERE DATE = Date('" + thisdate + "') AND CONTENT = '"+ sch_content +"'", null);

            while (cursor.moveToNext()) { // 레코드가 존재한다면,
                // no (INTEGER) 값 가져오기.
                title.setText(cursor.getString(1));

                content.setText(cursor.getString(2));

                summary.setText(cursor.getString(3));

            }
            cursor.close();
        }*/
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL(ContactDBCtrct.SQL_DROP_TBL);

        String content = editTextName.getText().toString() ;
        String date = editdate.getText().toString() ;

        /*String sqlInsert = "INSERT INTO SCHEDULE " +
                "(DATE, CONTENT) VALUES (" +
                "'" + date + "'," +
                "'" + content + "')" ;


        db.execSQL(sqlInsert);*/
        ContentValues value = new ContentValues();
        value.put("DATE",date);
        value.put("CONTENT",content);
        value.put("COLOR",color_final);

        db.insert("SCHEDULE",null,value);

        db.close();
        mOnClose();
    }
    private void delete_values() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL("DELETE FROM SCHEDULE WHERE DATE = Date('" + date + "')");
        db.execSQL("DELETE FROM SCHEDULE WHERE DATE = Date('" +  thisdate + "') AND CONTENT = '"+ sch_content +"'");

        //db.execSQL(ContactDBCtrct.SQL_DROP_TBL);

        db.close();
        mOnClose();

    }

    @Override
    public void onColorSelected(int dialogId, final int color) {

        final int invertColor = ~color;
        final String hexColor = String.format("%X", color);
        String hexInvertColor = String.format("%X", invertColor);
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "id " + dialogId + " c: " + hexColor + " i:" + hexInvertColor, Toast.LENGTH_SHORT).show();
            Log.e("asd", "id " + dialogId + " c: " + hexColor + " i:" + hexInvertColor);
        }

        color_final = color;

        qwer.setBackgroundColor(color);


    }

    /**
     * @brief : Color Picker dismiss 호출되는 리스너
     * @param dialogId : 종료된 대화상자 고유 아이디
     */
    @Override
    public void onDialogDismissed(int dialogId) {

    }



    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(editdate.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
    }
}


