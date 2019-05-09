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
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity{


    PieChart pieChart;
    /**
     * db선언
     */
    ContactDBHelper dbHelper = new ContactDBHelper(this);
    DiaryDBHelper dbHelper2 = new DiaryDBHelper(this);
    /**
     * 연/월 텍스트뷰
     */
    private TextView tvDate;
    /**
     * 그리드뷰 어댑터
     */
    private GridAdapter gridAdapter;
    private ListAdapter listAdapter;

    /**
     * 일 저장 할 리스트
     */
    private ArrayList<String> dayList;
    private ArrayList<String> dayList2;

    /**
     * 그리드뷰
     */
    private GridView gridView;
    private ListView listView;

    /**
     * 캘린더 변수
     */
    private Calendar mCal;

    private Date date_selected = new Date();

    public void mOnPopup(View v, int thisday){
        String thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
        Intent intent = new Intent(this,PopupActivity.class);
        //intent.putExtra("data",String.valueOf(mCal.get(Calendar.DATE)));
        intent.putExtra("date",thisdate);
        startActivityForResult(intent,1);
    }
    public void mOnPopup2(View v, int thisday){
        String thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
        Intent intent = new Intent(this,PopupActivity2.class);
        //intent.putExtra("data",String.valueOf(mCal.get(Calendar.DATE)));
        intent.putExtra("date",thisdate);
        startActivityForResult(intent,2);
    }

    private final String [] days = new String[]{"일","월","화","수","목","금","토"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = new File(getFilesDir(),"schedule.db");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file,null);
        db.execSQL(DiaryDBCtrct.SQL_DROP_TBL);
        db.execSQL(DiaryDBCtrct.SQL_CREATE_TBL);


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

        /**
         * 그리드뷰 생성
         */
        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);

        /**
         * 리스트뷰 생성
         */
        dayList2 = new ArrayList<String>();
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList2.add("" + (i + 1));
        }
        listAdapter = new ListAdapter(getApplicationContext(), dayList2);
        listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(listAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        /**
         * 차트생성
         */

        pieChart = (PieChart)findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(50f,"Meal"));
        yValues.add(new PieEntry(10f,"Transportation"));
        yValues.add(new PieEntry(8f,"Mobile Phone"));
        yValues.add(new PieEntry(20f,"Dessert"));
        yValues.add(new PieEntry(30f,"Clothes"));
        yValues.add(new PieEntry(40f,"Activities"));

        Description description = new Description();
        description.setText("Accout Status"); //라벨
        description.setTextSize(30);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"Countries");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);









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
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
                mOnPopup(view, position-dayNum2-5);

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mOnPopup2(view, position+1);

            }
        });


        Button bttt = (Button)findViewById(R.id.button3);
        bttt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPopup2(v, 1);
            }
        });

        /**
         * 달 바꾸기 bt1 , bt2
         */
        ImageButton bt1 = (ImageButton)findViewById(R.id.button);
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
                tvDate.setText(String.valueOf(year_tmp) + " / " + String.format("%02d",month_tmp));

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();

                dayList2 = new ArrayList<String>();
                for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    dayList2.add("" + (i + 1));
                }
                listAdapter = new ListAdapter(getApplicationContext(), dayList2);
                listView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();;
            }
        });
        ImageButton bt2 = (ImageButton)findViewById(R.id.button2);
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
                tvDate.setText(String.valueOf(year_tmp) + " / " + String.format("%02d",month_tmp));

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();

                dayList2 = new ArrayList<String>();
                for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    dayList2.add("" + (i + 1));
                }
                listAdapter = new ListAdapter(getApplicationContext(), dayList2);
                listView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();;
            }
        });


        /**
         * 탭 변환
         */
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout) ;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                changeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        }) ;

    }

    /**
     * 탭에 따른 변화 함수
     */
    private void changeView(int index) {
        GridView textView1 = (GridView) findViewById(R.id.gridview) ;
        PieChart textView2 = (PieChart) findViewById(R.id.piechart) ;
        ListView textView3 = (ListView) findViewById(R.id.listview) ;

        switch (index) {
            case 0 :
                textView1.setVisibility(View.VISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(View.VISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 2 :
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(View.VISIBLE) ;
                break ;

        }
    }


    /**
     * 달력 최신화
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
                gridAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode==2){
            if(resultCode==RESULT_OK){
                listAdapter.notifyDataSetChanged();;
            }
        }
    }

    /**
     * 해당 월에 표시할 일 수 구함
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
            holder.tvItemGridView.setText("" + getItem(position));

            if((position % 7) == 0){
                holder.tvItemGridView.setTextColor(Color.rgb(255,0,0));
            }
            else if((position % 7) == 6){
                holder.tvItemGridView.setTextColor(Color.rgb(0,0,255));
            }

            SimpleDateFormat sdf_d = new SimpleDateFormat("d");
            SimpleDateFormat sdf_m = new SimpleDateFormat("M");
            String sToday_d = sdf_d.format(date_selected);
            String sToday_m = sdf_m.format(date_selected);
            if (sToday_d.equals(getItem(position)) && String.valueOf(mCal.get(Calendar.MONTH)+1).equals(sToday_m)) { //오늘 day 텍스트 컬러 변경
                holder.tvItemGridView.setTextColor(Color.parseColor("#00AAAA"));
            }



            File file = new File(getFilesDir(),"schedule.db");
            SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file,null);

            if(position>7 && position<41) {
                String thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", (position - dayNum2 - 5));

                if (sqliteDB != null) {
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM SCHEDULE WHERE DATE = Date('" + thisday + "')", null);

                    while (cursor.moveToNext()) { // 레코드가 존재한다면,
                        String content = cursor.getString(1);
                        if(holder.tvItemGridView2.getVisibility() == View.INVISIBLE) {
                            holder.tvItemGridView2.setVisibility(View.VISIBLE);
                            holder.tvItemGridView2.setText(content);
                        }
                        else if(holder.tvItemGridView3.getVisibility() == View.INVISIBLE){
                            holder.tvItemGridView3.setVisibility(View.VISIBLE);
                            holder.tvItemGridView3.setText(content);
                        }
                        else if(holder.tvItemGridView4.getVisibility() == View.INVISIBLE){
                            holder.tvItemGridView4.setVisibility(View.VISIBLE);
                            holder.tvItemGridView4.setText(content);
                        }
                    }
                    cursor.close();
                }
            }



            return convertView;
        }
    }

    /**
     * 그리드뷰 용 뷰홀더
     */
    private class ViewHolder {
        TextView tvItemGridView;
        TextView tvItemGridView2;
        TextView tvItemGridView3;
        TextView tvItemGridView4;
    }

    /**
     * 리스트뷰 어댑터
     */
    private class ListAdapter extends BaseAdapter{

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
                convertView = inflater.inflate(R.layout.item_diary_listview, parent, false);

                holder = new ViewHolder2();
                holder.tvitemListView = (TextView)convertView.findViewById(R.id.diary_date);
                holder.tvitemListView2 = (TextView)convertView.findViewById(R.id.diary_title);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder2)convertView.getTag();
            }

            holder.tvitemListView.setText(""+getItem(position));

            File file = new File(getFilesDir(),"schedule.db");
            SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file,null);

            if(sqliteDB != null){
                String thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", Integer.parseInt(holder.tvitemListView.getText().toString()));
                Log.e("asd",holder.tvitemListView.getText().toString());
                SQLiteDatabase db = dbHelper2.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM DIARY WHERE DATE = Date('"+thisday+"')",null);
                if(cursor.moveToNext()) {
                    holder.tvitemListView2.setText(cursor.getString(1));
                }
                else{
                    holder.tvitemListView2.setText("");
                    holder.tvitemListView2.setHint("New Diary");
                }
                cursor.close();
            }

            return convertView;
        }
    }
    /**
     * 리스트뷰 용 뷰홀더
     */
    private class ViewHolder2 {
        TextView tvitemListView;
        TextView tvitemListView2;
    }

}
