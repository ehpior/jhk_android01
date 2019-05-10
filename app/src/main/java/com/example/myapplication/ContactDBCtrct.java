package com.example.myapplication;

public class ContactDBCtrct {

    private ContactDBCtrct() {} ;

    public static final String TBL_SCHEDULE = "SCHEDULE" ;
    public static final String COL_DATE = "DATE";
    public static final String COL_CONTENT = "CONTENT";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " +TBL_SCHEDULE+ " "+
            "(" +
                COL_DATE+       " DATE NOT NULL"             +", "+
                COL_CONTENT+      " TEXT NOT NULL"             +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_SCHEDULE;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_SCHEDULE;

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_SCHEDULE+ " " +
            "(" + COL_DATE + ", " + COL_CONTENT + ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_SCHEDULE;
}
