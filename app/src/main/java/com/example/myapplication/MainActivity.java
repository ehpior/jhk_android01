package com.example.myapplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends Activity {



                ContactDBHelper dbHelper = new ContactDBHelper(this);
    /**
     * 연/월 텍스트뷰
     */
    private TextView tvDate;
    /**
     * 그리드뷰 어댑터
     */
    private GridAdapter gridAdapter;

    /**
     * 일 저장 할 리스트
     */
    private ArrayList<String> dayList;

    /**
     * 그리드뷰
     */
    private GridView gridView;

    /**
     * 캘린더 변수
     */
    private Calendar mCal;

    private Date date_selected = new Date();

    public void mOnPopup(View v){
        Intent intent = new Intent(this,PopupActivity.class);
        intent.putExtra("data",String.valueOf(mCal.get(Calendar.DATE)));
        startActivityForResult(intent,1);
    }

    private final String [] days = new String[]{"일","월","화","수","목","금","토"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvDate = (TextView)findViewById(R.id.tv_date);
        gridView = (GridView)findViewById(R.id.gridview);

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("M", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 날짜 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + " / " + String.format("%02d",Integer.parseInt(curMonthFormat.format(date))));

        //gridview 요일 표시
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        final int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);

        gridView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
            TextView kk = (TextView) findViewById(R.id.textView3);
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
                kk.setText("position : " + (position-dayNum2-5) +"    " +position);

                //Log.e("asd",String.valueOf(mCal.get(Calendar.YEAR))+"년"+String.valueOf(mCal.get(Calendar.MONTH)+1)+"월"+String.valueOf(position-dayNum2-5)+"일");
                //Log.e("asd2",date_selected.toString());
            }
        });

        Button bt1 = (Button)findViewById(R.id.button);
        bt1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dayList = new ArrayList<String>();
                dayList.addAll(Arrays.asList(days));

                mCal.add(mCal.MONTH,-1);
                int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
                 for (int i = 1; i < dayNum2; i++) {
                    dayList.add("");
                }

                setCalendarDate(mCal.get(Calendar.MONTH)+1);


                int year_tmp = mCal.get(Calendar.YEAR);
                int month_tmp = mCal.get(Calendar.MONTH)+1;
                tvDate.setText(String.valueOf(year_tmp) + "/" + String.valueOf(month_tmp));

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();
            }
        });
        Button bt2 = (Button)findViewById(R.id.button2);
        bt2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dayList = new ArrayList<String>();
                dayList.addAll(Arrays.asList(days));

                mCal.add(mCal.MONTH,+1);
                int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
                 for (int i = 1; i < dayNum2; i++) {
                    dayList.add("");
                }

                setCalendarDate(mCal.get(Calendar.MONTH)+1);


                int year_tmp = mCal.get(Calendar.YEAR);
                int month_tmp = mCal.get(Calendar.MONTH)+1;
                tvDate.setText(String.valueOf(year_tmp) + "/" + String.valueOf(month_tmp));

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                TextView kk = (TextView) findViewById(R.id.textView3);
                String result = data.getStringExtra("result");
                kk.setText(result);
                gridAdapter.notifyDataSetChanged();
            }
        }
    }
    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);

        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
        while(dayList.size()<49){
            dayList.add("");
        }

    }

    /**
     * 그리드뷰 어댑터
     *
     */
    private class GridAdapter extends BaseAdapter {

        private final List<String> list;

        private final LayoutInflater inflater;

        /**
         * 생성자
         *
         * @param context
         * @param list
         */
        public GridAdapter(Context context, List<String> list) {
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

            ViewHolder holder = null;
            gridView = (GridView) findViewById(R.id.gridview);
            int gridviewH = gridView.getHeight();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);

                holder = new ViewHolder();

                holder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_item_gridview);
                holder.tvItemGridView2 = (TextView) convertView.findViewById(R.id.tv_item2_gridview);
                holder.tvItemGridView3 = (TextView) convertView.findViewById(R.id.tv_item3_gridview);
                holder.tvItemGridView4 = (TextView) convertView.findViewById(R.id.tv_item4_gridview);


                convertView.setTag(holder);
            } else{
                holder = (ViewHolder)convertView.getTag();
            }


            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            if(position<7){
                layoutParams.height = gridviewH / 13;
                holder.tvItemGridView2.setVisibility(View.GONE);
                holder.tvItemGridView3.setVisibility(View.GONE);
                holder.tvItemGridView4.setVisibility(View.GONE);
            }
            else{
                layoutParams.height = gridviewH * 2 / 13;
                holder.tvItemGridView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                holder.tvItemGridView.setPadding(15,0,0,0);
                holder.tvItemGridView2.setVisibility(View.INVISIBLE);
                holder.tvItemGridView3.setVisibility(View.INVISIBLE);
                holder.tvItemGridView4.setVisibility(View.INVISIBLE);
            }

            int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
            //Log.e("asd",String.valueOf(convertView.getTag()));
            holder.tvItemGridView.setText("" + getItem(position));

            //if (position-dayNum2>=6 && position-dayNum2-5<=mCal.getActualMaximum(Calendar.DATE)){
            //holder.tvItemGridView2.setText("test1");
            //holder.tvItemGridView3.setText("test2");
            //}

            //해당 날짜 텍스트 컬러,배경 변경
            //mCal = Calendar.getInstance();
            //오늘 day 가져옴
            SimpleDateFormat sdf_d = new SimpleDateFormat("d");
            SimpleDateFormat sdf_m = new SimpleDateFormat("M");
            String sToday_d = sdf_d.format(date_selected);
            String sToday_m = sdf_m.format(date_selected);
            if (sToday_d.equals(getItem(position)) && String.valueOf(mCal.get(Calendar.MONTH)+1).equals(sToday_m)) { //오늘 day 텍스트 컬러 변경
                holder.tvItemGridView.setTextColor(Color.parseColor("#00AAAA"));
            }
            /*if((position % 7) == 0){
                convertView.setBackgroundColor(Color.rgb(255,0,0));
            }
            else if((position % 7) == 6){
                convertView.setBackgroundColor(Color.rgb(0,0,255));
            else{
                //convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }*/
            if((position % 7) == 0){
                holder.tvItemGridView.setTextColor(Color.rgb(255,0,0));
            }
            else if((position % 7) == 6){
                holder.tvItemGridView.setTextColor(Color.rgb(0,0,255));
            }



            File file = new File(getFilesDir(),"contact.db");
            SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file,null);


            if (sqliteDB != null) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT, null);
                cursor.moveToFirst();
                if (cursor.moveToFirst()) { // 레코드가 존재한다면,
                    // no (INTEGER) 값 가져오기.
                    int no = cursor.getInt(0) ;
                    if(no == (position-dayNum2-5)) {
                        // name (TEXT) 값 가져오기
                        String name = cursor.getString(1);
                        holder.tvItemGridView2.setVisibility(View.VISIBLE);
                        holder.tvItemGridView2.setText(name);
                        //holder.tvItemGridView2.setBackgroundColor(Color.parseColor("#00AAAA"));

                        // phone (TEXT) 값 가져오기
                        String phone = cursor.getString(2);
                        holder.tvItemGridView3.setVisibility(View.VISIBLE);
                        holder.tvItemGridView3.setText(phone);
                        //holder.tvItemGridView3.setBackgroundColor(Color.parseColor("#00BBBB"));

                        int over = cursor.getInt(3);
                        holder.tvItemGridView4.setVisibility(View.VISIBLE);
                        holder.tvItemGridView4.setText(String.valueOf(over));

                    }
                }
            }



            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
        TextView tvItemGridView2;
        TextView tvItemGridView3;
        TextView tvItemGridView4;
    }
}
