package com.example.myapplication;

public class DiaryDBCtrct {

    private DiaryDBCtrct() {} ;

    public static final String TBL_SCHEDULE = "DIARY" ;
    public static final String COL_DATE = "DATE";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_CONTENT = "CONTENT";
    public static final String COL_SUMMARY = "SUMMARY";
    public static final String COL_IMAGE = "IMAGE";
    public static final String COL_FACE = "FACE";
    public static final String COL_WEATHER = "WEATHER";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " +TBL_SCHEDULE+ " "+
            "(" +
            COL_DATE+       " DATE"             +", "+
            COL_TITLE+      " TEXT"             +", "+
            COL_CONTENT+    " TEXT"             +", "+
            COL_SUMMARY+    " TEXT"             +", "+
            COL_IMAGE+      " BLOB"             +", "+
            COL_FACE+       " INT"              +", "+
            COL_WEATHER+    " INT"              +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_SCHEDULE;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_SCHEDULE;

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_SCHEDULE+ " " +
            "(" + COL_DATE + ", " + COL_CONTENT + ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_SCHEDULE;
}
