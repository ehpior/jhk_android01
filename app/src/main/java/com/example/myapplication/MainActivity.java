package com.example.myapplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.view.View.VISIBLE;
import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity{


    private GestureDetectorCompat detector;

    SimpleDateFormat sdf_d = new SimpleDateFormat("d");
    SimpleDateFormat sdf_m = new SimpleDateFormat("M");
    SimpleDateFormat sdf_y = new SimpleDateFormat("y");

    int today_position = -100;

    final int[] weather_final = new int[10];

    private Date date_selected = new Date();

    String sToday_d = sdf_d.format(date_selected);
    String sToday_m = sdf_m.format(date_selected);
    String sToday_y = sdf_y.format(date_selected);

    //PieChart pieChart;
    /**
     * db선언
     */
    ContactDBHelper dbHelper = new ContactDBHelper(this);
    DiaryDBHelper dbHelper2 = new DiaryDBHelper(this);
    /**
     * 연/월 텍스트뷰
     */
    private TextView tvDate;
    private TextView tvDate2;
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
    private ArrayList<String> dayList_last;
    private ArrayList<String> dayList_next;

    /**
     * 그리드뷰
     */
    private GridView gridView;
    private ListView listView;

    /**
     * 캘린더 변수
     */
    private Calendar mCal;


    public void mOnPopup(View v, int thisday, int month_chk){
        String thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
        if(month_chk==0){
            thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
        }
        else if(month_chk==-1) {
            thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH))) + '-' + String.format("%02d", thisday);
            if(mCal.get(Calendar.MONTH) < 1){
                thisdate = String.valueOf(mCal.get(Calendar.YEAR)-1) + "-12-" + String.format("%02d", thisday);
            }
        }
        else if(month_chk== 1) {
            thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 2)) + '-' + String.format("%02d", thisday);
            if(mCal.get(Calendar.MONTH)+2 > 12){
                thisdate = String.valueOf(mCal.get(Calendar.YEAR)+1) + "-01-" + String.format("%02d", thisday);
            }
        }
        //Intent intent = new Intent(this,PopupActivity.class);
        Intent intent = new Intent(this,MakeSchedule.class);
        intent.putExtra("date",thisdate);
        startActivityForResult(intent,1);
    }
    public void mOnPopup2(View v, int thisday){
        String thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
        //Intent intent = new Intent(this,PopupActivity2.class);
        Intent intent = new Intent(this,MakeDiary.class);
        //intent.putExtra("data",String.valueOf(mCal.get(Calendar.DATE)));
        intent.putExtra("date",thisdate);
        startActivityForResult(intent,2);
    }

    private final String [] days = new String[]{"일","월","화","수","목","금","토"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread th_weather = new Thread(new Runnable() {
            @Override public void run() {
                Logic_Weather zxc = new Logic_Weather();
                ArrayList<Integer> qqq = zxc.getweather();
                for(int i=0 ; i<8 ;i++){
                    weather_final[i+2] = qqq.get(i);
                }
            }
        });

        Thread th_weather2 = new Thread(new Runnable() {
            @Override public void run() {
                Logic_Weather2 zxc = new Logic_Weather2();
                ArrayList<Integer> qqq = zxc.getsky();
                ArrayList<Integer> qqq2 = zxc.getpty();
                int temp=0;
                int temp2=0;
                int j=0;
                int[] sky = new int[3];
                int[] pty = new int[3];
                sky = weather_cal(qqq);
                pty = weather_cal(qqq2);
                for(int i=0 ; i<3 ; i++){//맑음:1, 구름:2, 비:3, 눈:4, 구름비:5, 구름눈:6
                    if(pty[i]==0){
                        if(sky[i]<=2){
                            weather_final[i]=1;
                        }
                        else{
                            weather_final[i]=2;
                        }
                    }
                    else if(pty[i]<=2){
                        if(sky[i]<=2){
                            weather_final[i]=3;
                        }
                        else{
                            weather_final[i]=5;
                        }
                    }
                    else{
                        if(sky[i]<=2){
                            weather_final[i]=4;
                        }
                        else{
                            weather_final[i]=6;
                        }
                    }
                }
            }
            private int[] weather_cal(ArrayList<Integer> qqq){
                int temp=0;
                int temp2=0;
                int j=0;
                int[] sky = new int[3];

                for(int i=0 ; i<qqq.size() ; i++){
                    if(qqq.get(i)==-1){
                        sky[j] = (int)(Math.round(temp/(double)temp2));
                        j++;
                        temp=0;
                        temp2=0;
                    }
                    else{
                        temp += qqq.get(i);
                        temp2++;
                    }
                }
                sky[2] = (int)(Math.round(temp/(double)temp2));

                return sky;
            }
        });

        th_weather.start();
        th_weather2.start();

        try{
            th_weather.join();
            th_weather2.join();
        }
        catch(InterruptedException e){

        }

        /*File file = new File(getFilesDir(),"schedule.db");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file,null);
        db.execSQL(ContactDBCtrct.SQL_DROP_TBL);
        db.execSQL(ContactDBCtrct.SQL_CREATE_TBL);

        file = new File(getFilesDir(),"diary.db");
        db = SQLiteDatabase.openOrCreateDatabase(file,null);
        db.execSQL(DiaryDBCtrct.SQL_DROP_TBL);
        db.execSQL(DiaryDBCtrct.SQL_CREATE_TBL);*/

        setContentView(R.layout.activity_main);


        tvDate = (TextView)findViewById(R.id.tv_date);
        tvDate2 = (TextView)findViewById(R.id.tv_date2);
        gridView = (GridView)findViewById(R.id.gridview);

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("M", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 날짜 텍스트뷰에 뿌려줌
        //tvDate.setText(curYearFormat.format(date) + " / " + String.format("%02d",Integer.parseInt(curMonthFormat.format(date))));
        tvDate.setText(curYearFormat.format(date));
        tvDate2.setText(String.format("%02d",Integer.parseInt(curMonthFormat.format(date))));

        //gridview 요일 표시
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        final int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        /*for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }*/
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

        /*pieChart = (PieChart)findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,5,5,5);

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

        pieChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Object item = (Object)h.;
                Log.e("qqq",h.toString());
                Log.e("qqq",e.toString());
            }

            @Override
            public void onNothingSelected() {
                Log.e("qqq","nothing");

            }
        });*/


        /**
         * 제스처
         */

        detector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > 100 && abs(velocityX) > 100) {
                        if (diffX > 0) {
                            lastMonth();
                        } else {
                            nextMonth();
                        }
                    }
                } else {
                    if (abs(diffY) > 100 && abs(velocityY) > 100) {
                        if (diffY > 0) {
                            //onSwipeBottom();
                        } else {
                            //onSwipeTop();
                        }
                    }
                }
                return true;
            }
        });









        gridView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                detector.onTouchEvent(event);
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            int month_chk = 0;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<7){
                    return;
                }
                else if((7<=position)&&(position<=13)&&(Integer.parseInt(gridAdapter.getItem(position))>7)){
                    month_chk = -1;
                }
                else if((position>=35)&&(Integer.parseInt(gridAdapter.getItem(position))<20)){
                    month_chk = 1;
                }
                else{
                    month_chk = 0;
                }
                mOnPopup(view, Integer.parseInt(gridAdapter.getItem(position)),month_chk);

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
                lastMonth();
            }
        });
        ImageButton bt2 = (ImageButton)findViewById(R.id.button2);
        bt2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
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


    private void lastMonth(){
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal.add(mCal.MONTH,-1);
        /*int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i < dayNum2; i++) {
            dayList.add("");
        }*/

        setCalendarDate(mCal.get(Calendar.MONTH)+1);


        int year_tmp = mCal.get(Calendar.YEAR);
        int month_tmp = mCal.get(Calendar.MONTH)+1;
        tvDate.setText(String.valueOf(year_tmp));
        tvDate2.setText(String.format("%02d",month_tmp));

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        dayList2 = new ArrayList<String>();
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList2.add("" + (i + 1));
        }
        listAdapter = new ListAdapter(getApplicationContext(), dayList2);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    private void nextMonth(){
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal.add(mCal.MONTH,+1);
        /*int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i < dayNum2; i++) {
            dayList.add("");
        }*/

        setCalendarDate(mCal.get(Calendar.MONTH)+1);


        int year_tmp = mCal.get(Calendar.YEAR);
        int month_tmp = mCal.get(Calendar.MONTH)+1;
        tvDate.setText(String.valueOf(year_tmp));
        tvDate2.setText(String.format("%02d",month_tmp));

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        dayList2 = new ArrayList<String>();
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList2.add("" + (i + 1));
        }
        listAdapter = new ListAdapter(getApplicationContext(), dayList2);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
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
                textView1.setVisibility(VISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(VISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 2 :
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(VISIBLE) ;
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
                //Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("bm"),0,data.getByteArrayExtra("bm").length);
                //ImageView zzz = (ImageView)findViewById(R.id.imageView6);
                //zzz.setImageBitmap(b);
                listAdapter.notifyDataSetChanged();;
            }
        }
    }

    /**
     * 해당 월에 표시할 일 수 구함
     */
    private void setCalendarDate(int month) {

        dayList_last = new ArrayList<String>();
        dayList_next = new ArrayList<String>();
        mCal.add(mCal.MONTH,-1);
        int k = mCal.get(Calendar.DAY_OF_WEEK)-1;
        if((28-(k-1)+7)<=mCal.getActualMaximum(Calendar.DAY_OF_MONTH)){
            for(int i=36-k; i<=mCal.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
                //dayList_last.add(""+i);
                dayList.add(""+i);
            }
        }
        else if((29-k)>mCal.getActualMaximum(Calendar.DAY_OF_MONTH)){
            for(int i=22-k; i<=mCal.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
                //dayList_last.add(""+i);
                dayList.add(""+i);
            }
        }
        else{
            for(int i=29-k; i<=mCal.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
                //dayList_last.add(""+i);
                dayList.add(""+i);
            }
        }
        mCal.add(mCal.MONTH,+1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
        /*while(dayList.size()<49){
            dayList.add("");
        }*/
        int tmp = dayList.size();
        for(int i=1; i<=(49-tmp);i++){
            dayList.add(""+i);
        }


    }

    /**
     * 그리드뷰 어댑터
     */
    private class GridAdapter extends BaseAdapter {

        private final List<String> list;

        private final LayoutInflater inflater;
        private String thisday;

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

                holder = new ViewHolder();

                if(position<7){
                    convertView = inflater.inflate(R.layout.item_calendar_gridview_first, parent, false);
                }
                else{
                    convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                    holder.tvItemGridView2 = (TextView) convertView.findViewById(R.id.tv_item2_gridview);
                    holder.tvItemGridView3 = (TextView) convertView.findViewById(R.id.tv_item3_gridview);
                    holder.tvItemGridView4 = (TextView) convertView.findViewById(R.id.tv_item4_gridview);
                    holder.tvItemWeather = (ImageView) convertView.findViewById(R.id.weather);
                    holder.dot1 = (ImageView)convertView.findViewById(R.id.dot_1);
                    holder.dot2 = (ImageView)convertView.findViewById(R.id.dot_2);
                }

                holder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_item_gridview);

                convertView.setTag(holder);
            } else{
                holder = (ViewHolder)convertView.getTag();
            }


            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            if(position<7){
                //layoutParams.height = gridviewH / 15;
                layoutParams.height = gridviewH / 20;
                /*holder.tvItemGridView2.setVisibility(View.GONE);
                holder.tvItemGridView3.setVisibility(View.GONE);
                holder.tvItemGridView4.setVisibility(View.GONE);*/
            }
            else{
                //layoutParams.height = gridviewH * 2 / 13;
                layoutParams.height = gridviewH * 3 / 19;
                //holder.tvItemGridView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                //holder.tvItemGridView.setPadding(15,0,0,0);
                holder.tvItemGridView2.setVisibility(View.INVISIBLE);
                holder.tvItemGridView3.setVisibility(View.INVISIBLE);
                holder.tvItemGridView4.setVisibility(View.INVISIBLE);
            }

            holder.tvItemGridView.setText("" + getItem(position));

            if((position % 7) == 0){
                holder.tvItemGridView.setTextColor(Color.rgb(255,0,0));
            }
            else if((position % 7) == 6){
                holder.tvItemGridView.setTextColor(Color.rgb(0,0,255));
            }

            if(position>6) {

                int month_chk = 0; //-1은 지난달, 0은 이번달, 1은 다음달
                if ((7 <= position) && (position <= 13) && (Integer.parseInt(getItem(position)) > 7)) {//이전 달 설정
                    holder.tvItemGridView.setTextColor(Color.parseColor("#60000000"));
                    month_chk = -1;
                    if ((position % 7) == 0) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#60ff0000"));
                    } else if ((position % 7) == 6) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#600000ff"));
                    }
                } else if ((position >= 35) && (Integer.parseInt(getItem(position)) < 20)) {//다음 달 설정
                    holder.tvItemGridView.setTextColor(Color.parseColor("#60000000"));
                    month_chk = 1;
                    if ((position % 7) == 0) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#60ff0000"));
                    } else if ((position % 7) == 6) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#600000ff"));
                    }
                }


                if (sToday_d.equals(getItem(position)) && String.valueOf(mCal.get(Calendar.MONTH) + 1).equals(sToday_m) && String.valueOf(mCal.get(Calendar.YEAR)).equals(sToday_y)) { //오늘 day 텍스트 컬러 변경
                    //holder.tvItemGridView.setTextColor(Color.parseColor("#009999"));
                    holder.tvItemGridView.setTextColor(Color.parseColor("#ffffff"));
                    holder.tvItemGridView.setBackgroundResource(R.drawable.bg_today);
                    today_position = position;
                }
                Log.e("asd",position+" "+getItem(position) );

                if(String.valueOf(mCal.get(Calendar.MONTH) + 1).equals(sToday_m) && String.valueOf(mCal.get(Calendar.YEAR)).equals(sToday_y)){
                    switch (position - today_position){
                        case 0:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[0]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 1:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[1]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 2:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[2]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 3:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[3]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 4:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[4]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 5:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[5]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 6:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[6]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 7:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[7]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 8:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[8]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        case 9:
                            holder.tvItemWeather.setImageResource(weather_chk(weather_final[9]));
                            holder.tvItemWeather.setVisibility(VISIBLE);
                            break;
                        default:
                            holder.tvItemWeather.setVisibility(View.INVISIBLE);
                            break;
                    }
                }



                /*if (position > 6 && String.valueOf(mCal.get(Calendar.MONTH) + 1).equals(sToday_m)) {//날씨 설정
                    if (abs(Integer.parseInt(sToday_d) - Integer.parseInt(getItem(position))) < 5) {
                        holder.tvItemWeather.setVisibility(VISIBLE);
                    } else {
                        holder.tvItemWeather.setVisibility(View.INVISIBLE);
                    }
                }*/


                File file = new File(getFilesDir(), "schedule.db");
                SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file, null);


                if (position > 6) {
                    if (month_chk == 0) {
                        thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", Integer.parseInt(getItem(position)));
                        if(String.valueOf(mCal.get(Calendar.MONTH) + 1).equals(sToday_m)) {
                            /*if (abs(Integer.parseInt(sToday_d) - Integer.parseInt(getItem(position))) < 5) {
                                holder.tvItemWeather.setColorFilter(Color.parseColor("#FFBB00"), PorterDuff.Mode.SRC_IN);
                                holder.tvItemWeather.setVisibility(VISIBLE);
                            } else {
                                holder.tvItemWeather.setVisibility(View.INVISIBLE);
                            }*/


                        }
                    } else if (month_chk == -1) {
                        thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH))) + '-' + String.format("%02d", Integer.parseInt(getItem(position)));
                        if (mCal.get(Calendar.MONTH) < 1) {
                            thisday = String.valueOf(mCal.get(Calendar.YEAR) - 1) + "-12-" + String.format("%02d", Integer.parseInt(getItem(position)));
                        }
                    } else if (month_chk == 1) {
                        thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 2)) + '-' + String.format("%02d", Integer.parseInt(getItem(position)));
                        if (mCal.get(Calendar.MONTH) + 2 > 12) {
                            thisday = String.valueOf(mCal.get(Calendar.YEAR) + 1) + "-01-" + String.format("%02d", Integer.parseInt(getItem(position)));
                        }
                    }

                    if (sqliteDB != null) {
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        Cursor cursor = db.rawQuery("SELECT * FROM SCHEDULE WHERE DATE = Date('" + thisday + "')", null);

                        while (cursor.moveToNext()) { // 레코드가 존재한다면,
                            String content = cursor.getString(1);
                            if (holder.tvItemGridView2.getVisibility() == View.INVISIBLE) {
                                holder.tvItemGridView2.setVisibility(VISIBLE);
                                holder.tvItemGridView2.setText(content);
                            } else if (holder.tvItemGridView3.getVisibility() == View.INVISIBLE) {
                                holder.tvItemGridView3.setVisibility(VISIBLE);
                                holder.tvItemGridView3.setText(content);
                            } else if (holder.tvItemGridView4.getVisibility() == View.INVISIBLE) {
                                holder.tvItemGridView4.setVisibility(VISIBLE);
                                holder.tvItemGridView4.setText(content);
                            }
                        }
                        cursor.close();
                    }
                }
                sqliteDB.close();
                file = new File(getFilesDir(),"diary.db");
                sqliteDB = SQLiteDatabase.openOrCreateDatabase(file,null);


                if(sqliteDB != null){
                    SQLiteDatabase db = dbHelper2.getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM DIARY WHERE DATE = Date('"+thisday+"')",null);
                    if(cursor.moveToNext()) {
                        holder.dot1.setVisibility(View.VISIBLE);
                        holder.dot2.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.dot1.setVisibility(View.INVISIBLE);
                        holder.dot2.setVisibility(View.INVISIBLE);
                    }
                    cursor.close();
                }
                sqliteDB.close();
            }



            return convertView;
        }
    }

    public int weather_chk(int k){
        switch (k){
            case 1:
                k=R.drawable.weather_sunny;
                break;
            case 2:
                k=R.drawable.weather_cloud;
                break;
            case 3:
                k=R.drawable.weather_rain;
                break;
            case 4:
                k=R.drawable.weather_snow;
                break;
            case 5:
                k=R.drawable.weather_cloud_rain;
                break;
            case 6:
                k= R.drawable.weather_cloud_snow;
                break;
        }
        return k;
    }

    /**
     * 그리드뷰 용 뷰홀더
     */
    private class ViewHolder {
        TextView tvItemGridView;
        TextView tvItemGridView2;
        TextView tvItemGridView3;
        TextView tvItemGridView4;
        ImageView tvItemWeather;
        ImageView dot1;
        ImageView dot2;
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
                holder.tvitemListView2 = (TextView)convertView.findViewById(R.id.diary_datechk);
                holder.tvitem_diary_title = (TextView)convertView.findViewById(R.id.diary_title);
                holder.tvitem_diary_summary = (TextView)convertView.findViewById(R.id.diary_summary);
                holder.imageView_diary = (ImageView)convertView.findViewById(R.id.diary_image);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder2)convertView.getTag();
            }


            holder.tvitemListView.setText(""+getItem(position));
            switch((Integer.parseInt(getItem(position))+mCal.get(Calendar.DAY_OF_WEEK))%7){
                case 0:
                    holder.tvitemListView2.setText("Fri");
                    break;
                case 1:
                    holder.tvitemListView2.setText("Sat");
                    break;
                case 2:
                    holder.tvitemListView2.setText("Sun");
                    break;
                case 3:
                    holder.tvitemListView2.setText("Mon");
                    break;
                case 4:
                    holder.tvitemListView2.setText("Tue");
                    break;
                case 5:
                    holder.tvitemListView2.setText("Wed");
                    break;
                case 6:
                    holder.tvitemListView2.setText("Thu");
                    break;
            }

            /*if(position==1){
                ImageView kkk = (ImageView)findViewById(R.id.diary_image);
                kkk.setImageResource(R.drawable.beach);
            }*/

            File file = new File(getFilesDir(),"diary.db");
            SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file,null);

            if(sqliteDB != null){
                String thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", Integer.parseInt(holder.tvitemListView.getText().toString()));
                SQLiteDatabase db = dbHelper2.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM DIARY WHERE DATE = Date('"+thisday+"')",null);
                if(cursor.moveToNext()) {
                    holder.tvitem_diary_title.setText(cursor.getString(1));
                    holder.tvitem_diary_summary.setText(cursor.getString(3));
                    holder.imageView_diary.setPadding(0,0,0,0);
                    holder.imageView_diary.setBackground(null);
                    holder.imageView_diary.setImageBitmap(byteArrayToBitmap(cursor.getBlob(4)));
                }
                else{
                    holder.tvitem_diary_title.setText("");
                    holder.tvitem_diary_title.setHint("New Diary");
                    holder.tvitem_diary_summary.setText("");
                    holder.tvitem_diary_summary.setHint("New Summary");
                    holder.imageView_diary.setPadding(33,50,33,50);
                    holder.imageView_diary.setBackgroundResource(R.drawable.black_jump);
                    holder.imageView_diary.setImageResource(R.drawable.plus);
                }
                cursor.close();
            }
            sqliteDB.close();

            return convertView;
        }
    }
    /**
     * 리스트뷰 용 뷰홀더
     */
    private class ViewHolder2 {
        TextView tvitemListView;
        TextView tvitemListView2;
        ImageView imageView_diary;
        TextView tvitem_diary_title;
        TextView tvitem_diary_summary;
    }
    public Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        byteArray = null;
        return bitmap;
    }

    public class Logic_Weather {

        private ArrayList<Integer> weather = new ArrayList<Integer>();

        public Logic_Weather() {

            try {
                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                DocumentBuilder parser = f.newDocumentBuilder();

                Document xmlDoc = null;
                String url = "http://www.weather.go.kr/weather/forecast/mid-term-rss3.jsp?stnId=109";
                xmlDoc = parser.parse(url);

                Element root = xmlDoc.getDocumentElement();
                Log.e("asd",root.getTagName());

                String k = "";

                for(int i=0 ; ; i++){
                    Node xmlNode1 = root.getElementsByTagName("data").item(i);
                    if(i<10){
                        if(i%2==0){
                            continue;
                        }
                    }
                    if(xmlNode1 == null){
                        break;
                    }
                    Node xmlNode21 = ((Element) xmlNode1).getElementsByTagName("wf").item(0);

                    k = xmlNode21.getTextContent();
                    if(k.contains("맑") || k.contains("조")){
                        weather.add(1);
                    }
                    else if(k.contains("음") || k.contains("림")){
                        weather.add(2);
                    }
                    else if(k.contains("많")){
                        if(k.contains(" 비")){
                            weather.add(3);
                        }
                        else if(k.contains(" 눈")){
                            weather.add(4);
                        }
                    }
                    else{
                        if(k.contains(" 비")){
                            weather.add(5);
                        }
                        else if(k.contains(" 눈")){
                            weather.add(6);
                        }
                    }
                    k="";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.toString());
            }
        }

        public ArrayList<Integer> getweather() {
            return weather;
        }

    }
    public class Logic_Weather2 {

        private ArrayList<Integer> sky = new ArrayList<Integer>();
        private ArrayList<Integer> pty = new ArrayList<Integer>();

        /*private String[] wfEn = new String[5];
        private String[] hour1 = new String[5];*/

        public Logic_Weather2() {

            try {
                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                DocumentBuilder parser = f.newDocumentBuilder();

                Document xmlDoc = null;
                String url = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1135063000";
                xmlDoc = parser.parse(url);

                Element root = xmlDoc.getDocumentElement();

                int flag=0;

                for(int i=0 ; ; i++){
                    Node xmlNode1 = root.getElementsByTagName("data").item(i);
                    if(xmlNode1 == null){
                        break;
                    }
                    Node xmlNode21 = ((Element) xmlNode1).getElementsByTagName("sky").item(0);
                    Node xmlNode22 = ((Element) xmlNode1).getElementsByTagName("pty").item(0);
                    Node xmlNode23 = ((Element) xmlNode1).getElementsByTagName("day").item(0);

                    sky.add(Integer.parseInt(xmlNode21.getTextContent()));
                    pty.add(Integer.parseInt(xmlNode22.getTextContent()));
                    if(Integer.parseInt(xmlNode23.getTextContent())==1 && flag==0){
                        flag=1;
                        sky.add(-1);
                        pty.add(-1);
                    }
                    else if(Integer.parseInt(xmlNode23.getTextContent())==2 && flag==1){
                        flag=2;
                        sky.add(-1);
                        pty.add(-1);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.toString());
            }
        }

        public ArrayList<Integer> getsky() {
            return sky;
        }

        public ArrayList<Integer> getpty() {
            return pty;
        }

    }

}

