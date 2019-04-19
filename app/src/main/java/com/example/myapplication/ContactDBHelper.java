package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DBFILE_CONTACT = "contact.db";

    public ContactDBHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(ContactDBCtrct.SQL_CREATE_TBL);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
