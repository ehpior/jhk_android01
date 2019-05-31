package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import java.io.File;

import android.content.Intent;
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
import com.daimajia.swipe.SwipeLayout;

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

        final TextView bottom_sch1 = (TextView)view.findViewById(R.id.bottom_sch1);
        TextView bottom_sch2 = (TextView)view.findViewById(R.id.bottom_sch2);
        TextView bottom_sch3 = (TextView)view.findViewById(R.id.bottom_sch3);
        SwipeLayout swl1 = (SwipeLayout)view.findViewById(R.id.swipelayout);
        SwipeLayout swl2 = (SwipeLayout)view.findViewById(R.id.swipelayout2);
        SwipeLayout swl3 = (SwipeLayout)view.findViewById(R.id.swipelayout3);

        final EditText bottom_sch_make = (EditText)view.findViewById(R.id.bottom_make_sch);

        swl1.setVisibility(GONE);
        swl2.setVisibility(GONE);
        swl3.setVisibility(GONE);

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
                    swl1.setVisibility(VISIBLE);
                    bottom_sch1.setText(content);
                }
                else if(flag==1){
                    flag=2;
                    swl2.setVisibility(VISIBLE);
                    bottom_sch2.setText(content);
                }
                else if(flag==2){
                    flag=3;
                    swl3.setVisibility(VISIBLE);
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

        swl1.findViewWithTag("delete").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String title = String.valueOf(bottom_sch1.getText());

                db.execSQL("DELETE FROM SCHEDULE WHERE DATE = Date('" + kkk + "') AND CONTENT = '"+ title +"'");

                dismiss();
                ((MainActivity)MainActivity.mContext).grid_notifychange();

            }
        });
        swl1.findViewWithTag("modify").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.mContext,MakeSchedule.class);
                intent.putExtra("date",kkk);
                intent.putExtra("content",String.valueOf(bottom_sch1.getText()));
                dismiss();
                startActivity(intent);

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