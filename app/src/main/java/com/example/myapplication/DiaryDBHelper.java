package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DBFILE_CONTACT = "diary.db";

    public DiaryDBHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DiaryDBCtrct.SQL_CREATE_TBL);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
