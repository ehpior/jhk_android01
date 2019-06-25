package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    GestureDetector gestureDetector;

    String date_clicked = "";

    int view_flag = 0;

    SpannableStringBuilder sps;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public static Context mContext;

    DrawerLayout drawerLayout;

    private GestureDetector detector;

    SimpleDateFormat sdf_d = new SimpleDateFormat("d");
    SimpleDateFormat sdf_m = new SimpleDateFormat("M");
    SimpleDateFormat sdf_y = new SimpleDateFormat("y");
    SimpleDateFormat sdf_full = new SimpleDateFormat("yyyy-MM-dd");

    int today_position = -100;

    static final int[] weather_final = new int[10];

    private Date date_selected = new Date();

    String sToday_d = sdf_d.format(date_selected);
    String sToday_m = sdf_m.format(date_selected);
    String sToday_y = sdf_y.format(date_selected);
    String sToday_full = sdf_full.format(date_selected);

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
    //private TextView tvDate2;
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

    Thread th_weather;
    Thread th_weather2;

    public void grid_notifychange(){
        gridAdapter.notifyDataSetChanged();
    }

    public String cal_thisdate(int thisday,int month_chk){
        String thisdate = "";
        Calendar cal_tmp = (Calendar)mCal.clone();
        if(month_chk==0){
            //thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
            cal_tmp.set(Calendar.DATE,thisday);
            thisdate = sdf_full.format(cal_tmp.getTime());
        }
        else {
            if (month_chk == -1) {
            /*thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH))) + '-' + String.format("%02d", thisday);
            if(mCal.get(Calendar.MONTH) < 1){
                thisdate = String.valueOf(mCal.get(Calendar.YEAR)-1) + "-12-" + String.format("%02d", thisday);
            }*/
                cal_tmp.add(Calendar.MONTH, -1);
                cal_tmp.set(Calendar.DATE, thisday);
                thisdate = sdf_full.format(cal_tmp.getTime());
            } else if (month_chk == 1) {
            /*thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 2)) + '-' + String.format("%02d", thisday);
            if(mCal.get(Calendar.MONTH)+2 > 12){
                thisdate = String.valueOf(mCal.get(Calendar.YEAR)+1) + "-01-" + String.format("%02d", thisday);
            }*/
                cal_tmp.add(Calendar.MONTH, +1);
                cal_tmp.set(Calendar.DATE, thisday);
                thisdate = sdf_full.format(cal_tmp.getTime());
            }
        }
        return thisdate;
    }


    public void mOnPopup(View v, int thisday, int month_chk){
        String thisdate = cal_thisdate(thisday,month_chk);
        Intent intent = new Intent(this,MakeSchedule.class);
        intent.putExtra("date",thisdate);
        startActivityForResult(intent,1);
    }
    public void mOnPopup2(View v, int thisday){
        //String thisdate = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", thisday);
        mCal.set(Calendar.DATE,thisday);
        Intent intent = new Intent(this,MakeDiary.class);
        intent.putExtra("date",sdf_full.format(mCal.getTime()));
        startActivityForResult(intent,2);
    }

    private final String [] days = new String[]{"일","월","화","수","목","금","토"};
    //private final String [] days = new String[]{"SUN","MON","TUE","WED","THU","FRI","SAT"};
    //private final String [] days = new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        gestureDetector = new GestureDetector(this,new GestureListener());
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            th_weather = new Thread(new Runnable() {
                @Override
                public void run() {
                    Logic_Weather zxc = new Logic_Weather();
                    ArrayList<Integer> qqq = zxc.getweather();
                    for (int i = 0; i < 8; i++) {
                        weather_final[i + 2] = qqq.get(i);
                    }
                }
            });

            th_weather2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Logic_Weather2 zxc = new Logic_Weather2();
                    ArrayList<Integer> qqq = zxc.getsky();
                    ArrayList<Integer> qqq2 = zxc.getpty();
                    int temp = 0;
                    int temp2 = 0;
                    int j = 0;
                    int[] sky = new int[3];
                    int[] pty = new int[3];
                    sky = weather_cal(qqq);
                    pty = weather_cal(qqq2);
                    for (int i = 0; i < 3; i++) {//맑음:1, 구름:2, 비:3, 눈:4, 구름비:5, 구름눈:6
                        if (pty[i] == 0) {
                            if (sky[i] <= 2) {
                                weather_final[i] = 1;
                            } else {
                                weather_final[i] = 2;
                            }
                        } else if (pty[i] <= 2) {
                            if (sky[i] <= 2) {
                                weather_final[i] = 3;
                            } else {
                                weather_final[i] = 5;
                            }
                        } else {
                            if (sky[i] <= 2) {
                                weather_final[i] = 4;
                            } else {
                                weather_final[i] = 6;
                            }
                        }
                    }
                }

                private int[] weather_cal(ArrayList<Integer> qqq) {
                    int temp = 0;
                    int temp2 = 0;
                    int j = 0;
                    int[] sky = new int[3];

                    for (int i = 0; i < qqq.size(); i++) {
                        if (qqq.get(i) == -1) {
                            sky[j] = (int) (Math.round(temp / (double) temp2));
                            j++;
                            temp = 0;
                            temp2 = 0;
                        } else {
                            temp += qqq.get(i);
                            temp2++;
                        }
                    }
                    sky[2] = (int) (Math.round(temp / (double) temp2));

                    return sky;
                }
            });
            try {
                th_weather.start();
                th_weather2.start();
            } catch (Exception e) {
                Toast.makeText(this, "11111111111", Toast.LENGTH_SHORT).show();
            }
        }


        setContentView(R.layout.activity_main);

        tvDate = (TextView)findViewById(R.id.tv_date);
        //tvDate2 = (TextView)findViewById(R.id.tv_date2);
        gridView = (GridView)findViewById(R.id.gridview);

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        //연,월,일을 따로 저장
        //final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        //final SimpleDateFormat curMonthFormat = new SimpleDateFormat("M", Locale.KOREA);
        //final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 날짜 텍스트뷰에 뿌려줌
        //tvDate.setText(curYearFormat.format(date) + " / " + String.format("%02d",Integer.parseInt(curMonthFormat.format(date))));
        //tvDate.setText(curYearFormat.format(date)+String.format("%02d",Integer.parseInt(curMonthFormat.format(date))));
        sps = new SpannableStringBuilder(sdf_y.format(date)+" "+String.format("%02d",Integer.parseInt(sdf_m.format(date))));
        sps.setSpan(new AbsoluteSizeSpan(65),5,7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDate.setText(sps);
        //tvDate2.setText(String.format("%02d",Integer.parseInt(curMonthFormat.format(date))));

        //gridview 요일 표시
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)

        mCal.set(Integer.parseInt(sdf_y.format(date)), Integer.parseInt(sdf_m.format(date)) - 1, 1);
        //final int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        /*for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }*/
        //setCalendarDate(mCal.get(Calendar.MONTH) + 1);
        setCalendarDate();

        /**
         * 그리드뷰 생성
         */
        if (activeNetwork != null) {
            try {
                th_weather.join();
                th_weather2.join();
            } catch (InterruptedException e) {

            }
        }


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
         * 리사이클러뷰 생성
         */

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<RecyclerItem> diarylist_recycle = new ArrayList<>();
        diarylist_recycle.add(new RecyclerItem("1-1","1-2"));
        diarylist_recycle.add(new RecyclerItem("2-1","2-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));
        diarylist_recycle.add(new RecyclerItem("3-1","3-2"));

        RecyclerAdapter myRe = new RecyclerAdapter(diarylist_recycle);

        mRecyclerView.setAdapter(myRe);

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

        /*detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
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
        });*/

        /*GestureDetector.SimpleOnGestureListener detector2 = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent ev) {
                Log.w("TEST", "onDown = "+ev.toString());
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent ev) {
                Log.w("TEST", "onSingleTapUp = "+ev.toString());
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent ev) {
                Log.w("TEST", "onSingleTapConfirmed = "+ev.toString());
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent ev) {
                Log.w("TEST", "onDoubleTap = "+ev.toString());
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent ev) {
                Log.w("TEST", "onDoubleTapEvent = "+ev.toString());
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.w("TEST", "onScroll / e1 = "+e1.toString());
                Log.w("TEST", "onScroll / e2 = "+e2.toString());
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.w("TEST", "onFling / e1 = "+e1.toString());
                Log.w("TEST", "onFling / e2 = "+e2.toString());
                return true;
            }

        };

        detector = new GestureDetector(this, detector2);*/







        /*gridView.setOnTouchListener(new View.OnTouchListener(){
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
                BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                Bundle bundle = new Bundle();
                if(position<7){
                    return;
                }
                else if((position<=13)&&(Integer.parseInt(gridAdapter.getItem(position))>7)){
                    month_chk = -1;
                }
                else if((position>=35)&&(Integer.parseInt(gridAdapter.getItem(position))<20)){
                    month_chk = 1;
                }
                else{
                    month_chk = 0;
                }
                bundle.putString("data1",cal_thisdate(Integer.parseInt(gridAdapter.getItem(position)),month_chk));
                bottomSheetDialog.setArguments(bundle);
                bottomSheetDialog.show(getSupportFragmentManager(),"bott");
                //mOnPopup(view, Integer.parseInt(gridAdapter.getItem(position)),month_chk);
            }
        });*/


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnPopup2(view, position+1);
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
        /*TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout) ;

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
        }) ;*/

        /**
         * 좌측슬라이드메뉴
         */
        /*
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Drawer 화면(뷰) 객체 참조
        final View drawerView = (View) findViewById(R.id.drawer);


        // 드로어 화면을 열고 닫을 버튼 객체 참조
        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_OpenDrawer);
        Button btnCloseDrawer = (Button) findViewById(R.id.btn_CloseDrawer);


        // 드로어 여는 버튼 리스너
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        // 드로어 닫는 버튼 리스너
        btnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawerView);
            }
        });
        */

        /**
         * 우측슬라이드메뉴
         */

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        // Drawer 화면(뷰) 객체 참조
        final View drawerView2 = (View) findViewById(R.id.drawer2);


        // 드로어 화면을 열고 닫을 버튼 객체 참조
        ImageButton btnOpenDrawer2 = (ImageButton) findViewById(R.id.btn_OpenDrawer2);
        Button btnCloseDrawer2 = (Button) findViewById(R.id.btn_CloseDrawer2);


        // 드로어 여는 버튼 리스너
        btnOpenDrawer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView2);
            }
        });


        // 드로어 닫는 버튼 리스너
        btnCloseDrawer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawerView2);
            }
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);



        /*Button btnOpenCalendar = (Button)findViewById(R.id.open_calendar);
        Button btnOpenAccount = (Button)findViewById(R.id.open_account);
        Button btnOpenDiary = (Button)findViewById(R.id.open_diary);

        btnOpenCalendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(0);
                drawerLayout.closeDrawer(drawerView);
            }
        });
        btnOpenAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(1);
                drawerLayout.closeDrawer(drawerView);
            }
        });
        btnOpenDiary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(2);
                drawerLayout.closeDrawer(drawerView);
            }
        });*/

        /**
         * 좌측 슬라이드 메뉴(네비게이션)
         */

        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_OpenDrawer);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        btnOpenDrawer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
                Button btnOpenSign = (Button)findViewById(R.id.sign);
                btnOpenSign.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent k = new Intent(mContext,SignActivity.class);
                        startActivity(k);
                    }
                });
            }
        });
        navigationView.setNavigationItemSelectedListener(this);


        /**
         * BottomSheetDialog 하단 슬라이드 메뉴
         */

        Button qq = (Button)findViewById(R.id.omg);
        qq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("data1","2019-05-15");
                bottomSheetDialog.setArguments(bundle);
                bottomSheetDialog.show(getSupportFragmentManager(),"bott");
            }
        });


        /*FloatingActionButton fab_main = (FloatingActionButton)findViewById(R.id.fab_main);
        fab_main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_flag==0) {
                    mOnPopup(v, Integer.parseInt(sToday_d), 0);
                }
                else if(view_flag==1){

                }
                else if(view_flag==2){
                    mOnPopup2(v,Integer.parseInt(sToday_d));
                }
            }
        });*/
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            changeView(0);
            // Handle the camera action
        } else if (id == R.id.nav_account) {
            changeView(1);
        } else if (id == R.id.nav_diary) {
            changeView(2);
        } /*else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 이전 달
     */

    private void lastMonth(){
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal.add(mCal.MONTH,-1);
        /*int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i < dayNum2; i++) {
            dayList.add("");
        }*/

        //setCalendarDate(mCal.get(Calendar.MONTH)+1);
        setCalendarDate();


        int year_tmp = mCal.get(Calendar.YEAR);
        int month_tmp = mCal.get(Calendar.MONTH)+1;
        //tvDate.setText(String.valueOf(year_tmp)+String.format("%02d",month_tmp));
        sps = new SpannableStringBuilder(String.valueOf(year_tmp)+" "+String.format("%02d",month_tmp));
        sps.setSpan(new AbsoluteSizeSpan(65),5,7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDate.setText(sps);
        //tvDate2.setText(String.format("%02d",month_tmp));

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
        //gridAdapter.notifyDataSetChanged();

        dayList2 = new ArrayList<String>();
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList2.add("" + (i + 1));
        }
        listAdapter = new ListAdapter(getApplicationContext(), dayList2);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
    }


    /**
     * 다음달
     */
    private void nextMonth(){
        dayList = new ArrayList<String>();
        dayList.addAll(Arrays.asList(days));

        mCal.add(mCal.MONTH,+1);
        /*int dayNum2 = mCal.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i < dayNum2; i++) {
            dayList.add("");
        }*/

        //setCalendarDate(mCal.get(Calendar.MONTH)+1);
        setCalendarDate();


        int year_tmp = mCal.get(Calendar.YEAR);
        int month_tmp = mCal.get(Calendar.MONTH)+1;
        //tvDate.setText(String.valueOf(year_tmp)+String.format("%02d",month_tmp));
        sps = new SpannableStringBuilder(String.valueOf(year_tmp)+" "+String.format("%02d",month_tmp));
        sps.setSpan(new AbsoluteSizeSpan(65),5,7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDate.setText(sps);
        //tvDate2.setText(String.format("%02d",month_tmp));

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
        //gridAdapter.notifyDataSetChanged();

        dayList2 = new ArrayList<String>();
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList2.add("" + (i + 1));
        }
        listAdapter = new ListAdapter(getApplicationContext(), dayList2);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
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
                view_flag = 0;
                gridAdapter.notifyDataSetChanged();
                textView1.setVisibility(VISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :
                view_flag = 1;
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(VISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 2 :
                view_flag = 2;
                listAdapter.notifyDataSetChanged();
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(VISIBLE);
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
                //String result = data.getStringExtra("result");
                gridAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode==2){
            if(resultCode==RESULT_OK){
                //Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("bm"),0,data.getByteArrayExtra("bm").length);
                //ImageView zzz = (ImageView)findViewById(R.id.imageView6);
                //zzz.setImageBitmap(b);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 해당 월에 표시할 일 수 구함
     */
    //private void setCalendarDate(int month) {
    private void setCalendarDate() {

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
                    holder.grid_lay = (ConstraintLayout)convertView.findViewById(R.id.gridview_layout);
                    holder.more_chk = (ImageView)convertView.findViewById(R.id.more_chk);
                }

                holder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_item_gridview);

                convertView.setTag(holder);
            } else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvItemGridView.setTag(position);




            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            if(position<7){
                //layoutParams.height = gridviewH / 15;

                layoutParams.height = gridviewH / 22;
                /*holder.tvItemGridView2.setVisibility(View.GONE);
                holder.tvItemGridView3.setVisibility(View.GONE);
                holder.tvItemGridView4.setVisibility(View.GONE);*/
            }
            else{
                holder.more_chk.setVisibility(View.INVISIBLE);
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
                float zsr= (float)96f/255f;

                int month_chk = 0; //-1은 지난달, 0은 이번달, 1은 다음달
                if ((7 <= position) && (position <= 13) && (Integer.parseInt(getItem(position)) > 7)) {//이전 달 설정
                    month_chk = -1;
                    holder.tvItemWeather.setAlpha(zsr);
                    holder.tvItemGridView.setAlpha(zsr);
                    holder.tvItemGridView2.setAlpha(zsr);
                    holder.tvItemGridView3.setAlpha(zsr);
                    holder.tvItemGridView4.setAlpha(zsr);
                    holder.dot1.setAlpha(zsr);
                    holder.dot2.setAlpha(zsr);
                    /*holder.tvItemGridView.setTextColor(Color.parseColor("#60000000"));
                    if ((position % 7) == 0) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#60ff0000"));
                    } else if ((position % 7) == 6) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#600000ff"));
                    }*/
                } else if ((position >= 35) && (Integer.parseInt(getItem(position)) < 20)) {//다음 달 설정
                    month_chk = 1;
                    holder.tvItemWeather.setAlpha(zsr);
                    holder.tvItemGridView.setAlpha(zsr);
                    holder.tvItemGridView2.setAlpha(zsr);
                    holder.tvItemGridView3.setAlpha(zsr);
                    holder.tvItemGridView4.setAlpha(zsr);
                    holder.dot1.setAlpha(zsr);
                    holder.dot2.setAlpha(zsr);
                    /*holder.tvItemGridView.setTextColor(Color.parseColor("#60000000"));
                    if ((position % 7) == 0) {
                        holder.tvItemGridView.setTextColor(Color.parseColor("#60ff0000"));
                    } else if ((position % 7) == 6) {

                        holder.tvItemGridView.setTextColor(Color.parseColor("#600000ff"));
                    }*/
                }
                else{
                    zsr=(float)1.0;
                    holder.tvItemWeather.setAlpha(zsr);
                    holder.tvItemGridView.setAlpha(zsr);
                    holder.tvItemGridView2.setAlpha(zsr);
                    holder.tvItemGridView3.setAlpha(zsr);
                    holder.tvItemGridView4.setAlpha(zsr);
                    holder.dot1.setAlpha(zsr);
                    holder.dot2.setAlpha(zsr);
                }


                File file = new File(getFilesDir(), "schedule.db");
                SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file, null);


                final String thisday = cal_thisdate(Integer.parseInt(getItem(position)),month_chk);



                convertView.setOnTouchListener(new View.OnTouchListener() {//더블클릭
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        date_clicked = thisday;

                        return gestureDetector.onTouchEvent(event);
                    }
                });

                if (sToday_full.equals(thisday)) { //오늘 day 텍스트 컬러 변경
                    //holder.tvItemGridView.setTextColor(Color.parseColor("#009999"));
                    holder.tvItemGridView.setTextColor(Color.parseColor("#ffffff"));
                    holder.tvItemGridView.setBackgroundResource(R.drawable.bg_today);
                    today_position = position;
                }
                if((Integer.parseInt(thisday.substring(5,7)) == Integer.parseInt(sToday_m)) || (Integer.parseInt(thisday.substring(5,7)) == (Integer.parseInt(sToday_m)+1))) {
                    long calDate = -100;

                    try {
                        Date chk_date1 = sdf_full.parse(thisday);
                        Date chk_date2 = sdf_full.parse(sToday_full);

                        calDate = chk_date1.getTime() - chk_date2.getTime();
                        calDate /= 24 * 60 * 60 * 1000;
                    } catch (ParseException e) {

                    }
                    try {
                        switch ((int) (calDate)) {
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
                        }
                    }
                    catch(Exception e){
                        Toast.makeText(mContext, "2222222",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    holder.tvItemWeather.setVisibility(View.INVISIBLE);
                }

                if (sqliteDB != null) {
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM SCHEDULE WHERE DATE = Date('" + thisday + "')", null);

                    while (cursor.moveToNext()) { // 레코드가 존재한다면,
                        String content = cursor.getString(1);
                        int color = cursor.getInt(4);
                        if (holder.tvItemGridView2.getVisibility() == View.INVISIBLE) {
                            holder.tvItemGridView2.setVisibility(VISIBLE);
                            holder.tvItemGridView2.setText(content);
                            ((GradientDrawable)holder.tvItemGridView2.getBackground().getCurrent()).setStroke(5,color);
                        } else if (holder.tvItemGridView3.getVisibility() == View.INVISIBLE) {
                            holder.tvItemGridView3.setVisibility(VISIBLE);
                            holder.tvItemGridView3.setText(content);
                            ((GradientDrawable)holder.tvItemGridView3.getBackground().getCurrent()).setStroke(5,color);
                        } else if (holder.tvItemGridView4.getVisibility() == View.INVISIBLE) {
                            holder.tvItemGridView4.setVisibility(VISIBLE);
                            holder.tvItemGridView4.setText(content);
                            ((GradientDrawable)holder.tvItemGridView4.getBackground().getCurrent()).setStroke(5,color);
                        }
                        else if(holder.tvItemGridView4.getVisibility() == View.VISIBLE){
                            holder.more_chk.setVisibility(VISIBLE);
                        }
                    }
                    cursor.close();
                    db.close();
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
                    db.close();
                }
                sqliteDB.close();
            }

            return convertView;
        }
    }

    public int weather_chk(int k){
        switch (k){
            case 1:
                k=R.drawable.weather_1;
                break;
            case 2:
                k=R.drawable.weather_2;
                break;
            case 3:
                k=R.drawable.weather_3;
                break;
            case 4:
                k=R.drawable.weather_4;
                break;
            case 5:
                k=R.drawable.weather_5;
                break;
            case 6:
                k= R.drawable.weather_6;
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
        ImageView more_chk;
        ConstraintLayout grid_lay;
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
                //holder.tvitem_diary_summary = (TextView)convertView.findViewById(R.id.diary_summary);
                holder.imageView_diary = (ImageView)convertView.findViewById(R.id.diary_image);
                holder.diary_face = (ImageView)convertView.findViewById(R.id.list_face);
                holder.diary_weather = (ImageView)convertView.findViewById(R.id.list_weather);
                holder.diary_weather_face = (LinearLayout)convertView.findViewById(R.id.list_weather_face);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder2)convertView.getTag();
            }


            holder.tvitemListView.setText(String.format("%02d",Integer.parseInt(getItem(position))));
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

            File file = new File(getFilesDir(),"diary.db");
            SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(file,null);

            if(sqliteDB != null){
                String thisday = String.valueOf(mCal.get(Calendar.YEAR)) + '-' + String.format("%02d", (mCal.get(Calendar.MONTH) + 1)) + '-' + String.format("%02d", Integer.parseInt(holder.tvitemListView.getText().toString()));
                SQLiteDatabase db = dbHelper2.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM DIARY WHERE DATE = Date('"+thisday+"')",null);
                if(cursor.moveToNext()) {
                    holder.imageView_diary.setVisibility(View.VISIBLE);
                    holder.tvitem_diary_title.setText(cursor.getString(1));
                    //holder.tvitem_diary_summary.setText(cursor.getString(3));
                    holder.imageView_diary.setPadding(0,0,0,0);
                    holder.imageView_diary.setBackground(null);
                    try {
                        holder.imageView_diary.setImageBitmap(byteArrayToBitmap(cursor.getBlob(4)));
                    }
                    catch(NullPointerException e){
                        holder.imageView_diary.setImageAlpha(1);
                    }
                    holder.diary_weather_face.setVisibility(VISIBLE);
                    switch(cursor.getInt(5)){
                        case 1:
                            holder.diary_face.setImageResource(R.drawable.icon_face_11);
                            break;
                        case 2:
                            holder.diary_face.setImageResource(R.drawable.icon_face_22);
                            break;
                        case 3:
                            holder.diary_face.setImageResource(R.drawable.icon_face_33);
                            break;
                        case 4:
                            holder.diary_face.setImageResource(R.drawable.icon_face_44);
                            break;
                        case 5:
                            holder.diary_face.setImageResource(R.drawable.icon_face_55);
                            break;
                        case 6:
                            holder.diary_face.setImageResource(R.drawable.icon_face_66);
                            break;
                        /*case 7:
                            holder.diary_face.setImageResource(R.drawable.icon_face_7);
                            break;
                        case 8:
                            holder.diary_face.setImageResource(R.drawable.icon_face_8);
                            break;*/
                    }
                    switch(cursor.getInt(6)){
                        case 1:
                            holder.diary_weather.setImageResource(R.drawable.weather_1);
                            break;
                        case 2:
                            holder.diary_weather.setImageResource(R.drawable.weather_2);
                            break;
                        case 3:
                            holder.diary_weather.setImageResource(R.drawable.weather_3);
                            break;
                        case 4:
                            holder.diary_weather.setImageResource(R.drawable.weather_4);
                            break;
                        case 5:
                            holder.diary_weather.setImageResource(R.drawable.weather_5);
                            break;
                        case 6:
                            holder.diary_weather.setImageResource(R.drawable.weather_6);
                            break;
                    }
                }
                else{
                    holder.imageView_diary.setVisibility(View.INVISIBLE);
                    holder.tvitem_diary_title.setText("");
                    holder.tvitem_diary_title.setHint("New Diary");
                    //holder.tvitem_diary_summary.setText("");
                    //holder.tvitem_diary_summary.setHint("New Summary");
                    holder.imageView_diary.setPadding(33,50,33,50);
                    holder.imageView_diary.setBackgroundResource(R.drawable.black_jump);
                    holder.imageView_diary.setImageResource(R.drawable.plus);
                    holder.diary_weather_face.setVisibility(View.GONE);
                }
                cursor.close();
                db.close();
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
        ImageView diary_face;
        ImageView diary_weather;
        LinearLayout diary_weather_face;
        //TextView tvitem_diary_summary;
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

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
            Bundle bundle = new Bundle();

            bundle.putString("data1",date_clicked);
            bottomSheetDialog.setArguments(bundle);
            bottomSheetDialog.show(getSupportFragmentManager(),"bott");

            return super.onSingleTapConfirmed(e);
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            Intent intent = new Intent(mContext,MakeSchedule.class);
            intent.putExtra("date",date_clicked);
            startActivityForResult(intent,1);

            return true;
        }
    }



}

