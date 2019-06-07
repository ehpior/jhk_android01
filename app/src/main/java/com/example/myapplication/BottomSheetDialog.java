package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.swipe.SwipeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by charlie on 2017. 11. 22
 */

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener{


    public static BottomSheetDialog getInstance() { return new BottomSheetDialog(); }

    private String thisdate="";
    private ContactDBHelper dbHelper;
    private ArrayList<String> sch_list = new ArrayList<String>();
    private ListAdapter listAdapter;
    private ListView listView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bottom_sheet_dialog, container,false);
        dbHelper = new ContactDBHelper(getActivity());

        //final TextView bottom_sch1 = (TextView)view.findViewById(R.id.bottom_sch1);
        final EditText bottom_sch_make = (EditText)view.findViewById(R.id.bottom_make_sch);

        if(getArguments() != null){
            thisdate = getArguments().getString("data1");
        }

        File file = new File(getActivity().getFilesDir(), "schedule.db");
        SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file, null);

        if (sqliteDB != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM SCHEDULE WHERE DATE = Date('" + thisdate + "')", null);

            while (cursor.moveToNext()) { // 레코드가 존재한다면,
                sch_list.add(cursor.getString(1));
            }
            cursor.close();
            db.close();
        }
        sqliteDB.close();

        listAdapter = new ListAdapter(getActivity(), sch_list);
        listView = (ListView)view.findViewById(R.id.listview_bottom_sch);
        listView.setAdapter(listAdapter);

        bottom_sch_make.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){
                    case EditorInfo.IME_ACTION_DONE:
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        String content = bottom_sch_make.getText().toString() ;

                        String sqlInsert = "INSERT INTO SCHEDULE " +
                                "(DATE, CONTENT) VALUES (" +
                                "'" + thisdate + "'," +
                                "'" + content + "')" ;

                        db.execSQL(sqlInsert);
                        db.close();
                        dismiss();
                }
                return false;
            }
        });

        /*swl1.findViewWithTag("delete").setOnClickListener(new View.OnClickListener() {
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
        });*/


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

    private class ListAdapter extends BaseAdapter {

        private final List<String> list;

        private final LayoutInflater inflater;

        public ListAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder2 holder = null;

            if (convertView == null){
                convertView = inflater.inflate(R.layout.item_bottom_schedule_listview, parent, false);

                holder = new ViewHolder2();
                holder.tvitem_bottom_sch = (TextView) convertView.findViewById(R.id.bottom_schs);
                holder.tvitem_bottom_modify = (ImageView)convertView.findViewById(R.id.bottom_sch_modify);
                holder.tvitem_bottom_delete = (ImageView)convertView.findViewById(R.id.bottom_sch_delete);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder2)convertView.getTag();
            }

            holder.tvitem_bottom_sch.setText(getItem(position));

            final String this_title = getItem(position);

            holder.tvitem_bottom_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.mContext,MakeSchedule.class);
                    intent.putExtra("date",thisdate);
                    intent.putExtra("content",this_title);
                    dismiss();
                    startActivity(intent);
                    ((MainActivity)MainActivity.mContext).grid_notifychange();
                }
            });

            holder.tvitem_bottom_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    db.execSQL("DELETE FROM SCHEDULE WHERE DATE = Date('" + thisdate + "') AND CONTENT = '"+ this_title +"'");
                    db.close();

                    dismiss();
                    ((MainActivity)MainActivity.mContext).grid_notifychange();
                }
            });


            return convertView;
        }
    }
    /**
     * 리스트뷰 용 뷰홀더
     */
    private class ViewHolder2 {
        TextView tvitem_bottom_sch;
        ImageView tvitem_bottom_modify;
        ImageView tvitem_bottom_delete;
    }
}