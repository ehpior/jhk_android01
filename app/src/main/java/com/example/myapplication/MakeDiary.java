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

    Bitmap tmp_bitmap = null;

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
        tmp_image = (ImageView)findViewById(R.id.diary_tmp_image);
        tmp_image2 = (ImageView)findViewById(R.id.diary_tmp_image2);
        tmp_image3 = (ImageView)findViewById(R.id.diary_tmp_image3);



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

        ImageButton buttonSave = (ImageButton)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_values();
            }
        });

        /*Button buttonClear = (Button)findViewById(R.id.clearclear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_values();
            }
        });*/

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

                tmp_image.setImageBitmap(byteArrayToBitmap(cursor.getBlob(4)));
                tmp_bitmap = byteArrayToBitmap(cursor.getBlob(4));

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

        db.insert("DIARY",null,value);

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
        if(requestCode==9){
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        byteArray = null;
        return bitmap;
    }


}
