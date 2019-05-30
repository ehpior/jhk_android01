package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import java.io.File;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by charlie on 2017. 11. 22
 */

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener{


    public static BottomSheetDialog getInstance() { return new BottomSheetDialog(); }

    String kkk="";
    ContactDBHelper dbHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bottom_sheet_dialog, container,false);
        dbHelper = new ContactDBHelper(getActivity());

        TextView bottom_sch1 = (TextView)view.findViewById(R.id.bottom_sch1);
        TextView bottom_sch2 = (TextView)view.findViewById(R.id.bottom_sch2);
        TextView bottom_sch3 = (TextView)view.findViewById(R.id.bottom_sch3);
        final EditText bottom_sch_make = (EditText)view.findViewById(R.id.bottom_make_sch);

        bottom_sch1.setVisibility(GONE);
        bottom_sch2.setVisibility(GONE);
        bottom_sch3.setVisibility(GONE);

        if(getArguments() != null){
            kkk = getArguments().getString("data1");
        }
        Log.e("전달값",kkk);

        File file = new File(getActivity().getFilesDir(), "schedule.db");
        SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file, null);

        if (sqliteDB != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM SCHEDULE WHERE DATE = Date('" + kkk + "')", null);
            int flag=0;

            while (cursor.moveToNext()) { // 레코드가 존재한다면,
                String content = cursor.getString(1);
                if(flag==0){
                    flag=1;
                    bottom_sch1.setVisibility(VISIBLE);
                    bottom_sch1.setText(content);
                }
                else if(flag==1){
                    flag=2;
                    bottom_sch2.setVisibility(VISIBLE);
                    bottom_sch2.setText(content);
                }
                else if(flag==2){
                    flag=3;
                    bottom_sch3.setVisibility(VISIBLE);
                    bottom_sch3.setText(content);
                }
            }
            cursor.close();
        }

        sqliteDB.close();

        bottom_sch_make.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){
                    case EditorInfo.IME_ACTION_DONE:
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        String content = bottom_sch_make.getText().toString() ;

                        String sqlInsert = "INSERT INTO SCHEDULE " +
                                "(DATE, CONTENT) VALUES (" +
                                "'" + kkk + "'," +
                                "'" + content + "')" ;

                        db.execSQL(sqlInsert);
                        dismiss();
                }
                return false;
            }
        });


        return view;
    }

    @Override
    public void onClick(View view) {
        /*switch (view.getId()){
            case R.id.msgLo:
                Toast.makeText(getContext(),"Message",Toast.LENGTH_SHORT).show();
                break;
            case R.id.emailLo:
                Toast.makeText(getContext(),"Email",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cloudLo:
                Toast.makeText(getContext(),"Cloud",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bluetoothLo:
                Toast.makeText(getContext(),"Bluetooth",Toast.LENGTH_SHORT).show();
                break;
        }*/
        dismiss();
    }
}