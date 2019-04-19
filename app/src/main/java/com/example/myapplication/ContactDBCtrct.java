package com.example.myapplication;

public class ContactDBCtrct {

    private ContactDBCtrct() {} ;

    public static final String TBL_CONTACT = "CONTACT_T" ;
    public static final String COL_NO = "NO";
    public static final String COL_NAME = "NAME";
    public static final String COL_PHONE = "PHONE";
    public static final String COL_OVER20 = "OVER20";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " +TBL_CONTACT+ " "+
            "(" +
                COL_NO+         " INTEGER NOT NULL" +", "+
                COL_NAME+       " TEXT"             +", "+
                COL_PHONE+      " TEXT"             +", "+
                COL_OVER20+     " INTEGER"          +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT;

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_CONTACT+ " " +
            "(" + COL_NO + ", " + COL_NAME + ", " + COL_PHONE + ", " + COL_OVER20 + ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT;
}
