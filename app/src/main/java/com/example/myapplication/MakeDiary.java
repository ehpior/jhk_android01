package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class MakeDiary extends Activity {

    TextView txtText;
    EditText title;
    EditText content;
    EditText summary;
    ImageView tmp_image;
    ImageView tmp_image2;
    ImageView tmp_image3;
    ImageView state_face;
    ImageView state_weather;

    boolean new_chk;

    int face=1;
    int weather=1;

    Bitmap tmp_bitmap = null;

    SQLiteDatabase sqliteDB;

    DiaryDBHelper dbHelper = null;
    String thisdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_diary);

        txtText = (EditText)findViewById(R.id.diary_et_date);

        new_chk = false;

        Intent intent = getIntent();
        thisdate = intent.getStringExtra("date");
        txtText.setText(thisdate);

        title = (EditText)findViewById(R.id.diary_et_title);
        content = (EditText)findViewById(R.id.diary_et_content);
        summary = (EditText)findViewById(R.id.diary_et_summary);
        tmp_image = (ImageView)findViewById(R.id.diary_tmp_image);
        tmp_image2 = (ImageView)findViewById(R.id.diary_tmp_image2);
        tmp_image3 = (ImageView)findViewById(R.id.diary_tmp_image3);
        state_face = (ImageView)findViewById(R.id.make_diary_face);
        state_weather = (ImageView)findViewById(R.id.make_diary_weather);



        sqliteDB = init_database();
        init_tables();
        load_values();


        tmp_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,9);
            }
        });
        tmp_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values();
            }
        });

        Button asd = (Button)findViewById(R.id.loadload);
        asd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,9);
            }
        });

        state_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),make_diary_select_face.class);
                startActivityForResult(intent,5);
            }
        });
        state_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),make_diary_select_weather.class);
                startActivityForResult(intent,6);
            }
        });








        ImageButton buttonSave = (ImageButton)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_values();
            }
        });

        ImageButton buttonClose = (ImageButton)findViewById(R.id.buttonclose);
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
        //ByteArrayOutputStream bs = new ByteArrayOutputStream();
        //tmp_bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
        //intent.putExtra("bm",bs.toByteArray());
        setResult(RESULT_OK, intent);

        sqliteDB.close();

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
                new_chk = true;
                // no (INTEGER) 값 가져오기.
                title.setText(cursor.getString(1));

                content.setText(cursor.getString(2));

                summary.setText(cursor.getString(3));

                switch(cursor.getInt(5)){
                    case 1:
                        face=1;
                        state_face.setImageResource(R.drawable.icon_face_1);
                        break;
                    case 2:
                        face=2;
                        state_face.setImageResource(R.drawable.icon_face_2);
                        break;
                    case 3:
                        face=3;
                        state_face.setImageResource(R.drawable.icon_face_3);
                        break;
                    case 4:
                        face=4;
                        state_face.setImageResource(R.drawable.icon_face_4);
                        break;
                    case 5:
                        face=5;
                        state_face.setImageResource(R.drawable.icon_face_5);
                        break;
                    case 6:
                        face=6;
                        state_face.setImageResource(R.drawable.icon_face_6);
                        break;
                    case 7:
                        face=7;
                        state_face.setImageResource(R.drawable.icon_face_7);
                        break;
                    case 8:
                        face=8;
                        state_face.setImageResource(R.drawable.icon_face_8);
                        break;
                }
                switch(cursor.getInt(6)){
                    case 1:
                        weather=1;
                        state_weather.setImageResource(R.drawable.weather_1);
                        break;
                    case 2:
                        weather=2;
                        state_weather.setImageResource(R.drawable.weather_2);
                        break;
                    case 3:
                        weather=3;
                        state_weather.setImageResource(R.drawable.weather_3);
                        break;
                    case 4:
                        weather=4;
                        state_weather.setImageResource(R.drawable.weather_4);
                        break;
                    case 5:
                        weather=5;
                        state_weather.setImageResource(R.drawable.weather_5);
                        break;
                    case 6:
                        weather=6;
                        state_weather.setImageResource(R.drawable.weather_6);
                        break;
                }

                try {
                    tmp_image.setImageBitmap(byteArrayToBitmap(cursor.getBlob(4)));
                    tmp_bitmap = byteArrayToBitmap(cursor.getBlob(4));
                }
                catch(NullPointerException e){
                    tmp_bitmap = null;
                }

            }
            cursor.close();
        }
    }

    private void save_values() {
        // delete

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.execSQL(ContactDBCtrct.SQL_DELETE);

        /*String sqlInsert = "INSERT INTO DIARY " +
                "(DATE, TITLE, CONTENT, SUMMARY) VALUES (" +
                "'" + thisdate + "'," +
                "'" + title.getText()+ "',"+
                "'" + content.getText()+ "',"+
                "'" + summary.getText() + "')" ;

        db.execSQL(sqlInsert);*/
        ContentValues value = new ContentValues();
        value.put("DATE", thisdate);
        value.put("TITLE", String.valueOf(title.getText()));
        value.put("CONTENT", String.valueOf(content.getText()));
        value.put("SUMMARY", String.valueOf(summary.getText()));
        value.put("IMAGE", bitmapToByteArray(tmp_bitmap));
        value.put("FACE",face);
        value.put("WEATHER",weather);

        if(new_chk == true){
            db.update("DIARY",value,"DATE = '" + thisdate + "'",null);
        }
        else{
            db.insert("DIARY",null,value);
        }

        mOnClose();
    }

    private void delete_values() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM DIARY WHERE DATE = Date('" + thisdate + "')");
        //db.execSQL(DiaryDBCtrct.SQL_DROP_TBL);

        mOnClose();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==5){
            if(resultCode==RESULT_OK){
                int a = data.getIntExtra("no",1);
                switch(a){
                    case 1:
                        face=1;
                        state_face.setImageResource(R.drawable.icon_face_1);
                        break;
                    case 2:
                        face=2;
                        state_face.setImageResource(R.drawable.icon_face_2);
                        break;
                    case 3:
                        face=3;
                        state_face.setImageResource(R.drawable.icon_face_3);
                        break;
                    case 4:
                        face=4;
                        state_face.setImageResource(R.drawable.icon_face_4);
                        break;
                    case 5:
                        face=5;
                        state_face.setImageResource(R.drawable.icon_face_5);
                        break;
                    case 6:
                        face=6;
                        state_face.setImageResource(R.drawable.icon_face_6);
                        break;
                    case 7:
                        face=7;
                        state_face.setImageResource(R.drawable.icon_face_7);
                        break;
                    case 8:
                        face=8;
                        state_face.setImageResource(R.drawable.icon_face_8);
                        break;
                }
            }
        }
        else if(requestCode==6){
            if(resultCode==RESULT_OK){
                int a = data.getIntExtra("no",1);
                switch(a){
                    case 1:
                        weather=1;
                        state_weather.setImageResource(R.drawable.weather_1);
                        break;
                    case 2:
                        weather=2;
                        state_weather.setImageResource(R.drawable.weather_2);
                        break;
                    case 3:
                        weather=3;
                        state_weather.setImageResource(R.drawable.weather_3);
                        break;
                    case 4:
                        weather=4;
                        state_weather.setImageResource(R.drawable.weather_4);
                        break;
                    case 5:
                        weather=5;
                        state_weather.setImageResource(R.drawable.weather_5);
                        break;
                    case 6:
                        weather=6;
                        state_weather.setImageResource(R.drawable.weather_6);
                        break;
                }
            }
        }
        else if(requestCode==9){
            if(resultCode==RESULT_OK){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    Log.e("asd",String.valueOf(data.getData()));
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    tmp_bitmap = img;
                    in.close();
                    // 이미지 표시
                    tmp_image.setPadding(0,0,0,0);
                    tmp_image.setBackgroundResource(R.drawable.black);
                    tmp_image.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;//
        }
        catch(NullPointerException e){
            return null;
        }
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        byteArray = null;
        return bitmap;
    }


}
