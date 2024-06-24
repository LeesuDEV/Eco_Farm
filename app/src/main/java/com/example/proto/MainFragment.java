package com.example.proto;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainFragment extends AppCompatActivity {
    TextView userName_TextView;
    Fragment fragment; //홈프래그먼트를 초기값으로 설정 - 네비게이션바 클릭할떄마다 객체 생성하지않도록 멤버변수로 선언
    BottomNavigationView bottomNavigationView; // 하단네비게이션 바

    static DatabaseReference realtimeDB_Hope;
    static DatabaseReference realtimeDB_Now;

    DocumentReference fireStore_MyDB; //DB값 -UID처리 돼있음

    static FirebaseDatabase fireRealtimeDB = FirebaseDatabase.getInstance(); // 리얼타임 데이터베이스 변수선언
    static FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance(); // 파이어스토어 데이터베이스 변수선언

    static String uid, userName;

    static boolean status = false; // 작물 키우고있나? 아닌가? 상태
    static String startDate; // 식물 성장 시작일

    static DatabaseReference now_temp;
    static DatabaseReference now_hum;
    static DatabaseReference now_birghtness;
    static DatabaseReference now_waterLevel;

    static DatabaseReference hope_temp;
    static DatabaseReference hope_hum;
    static DatabaseReference hope_brightness;
    static DatabaseReference hope_water_level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName_TextView = findViewById(R.id.UserName);

        uid = getIntent().getStringExtra("uid"); //UID정보 호출

        dbThread();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();                            //액션바 숨기기

        /*--------------------------BottomNavigationBar을 통한 플래그먼트 이동부분(메소드는 밑에있음)------------*/
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case (R.id.mainmenu):
                        fragment = new HomeFragment();
                        break;
                    case (R.id.controll_menu):
                        fragment = new ControlFragment();
                        break;
                    case (R.id.set_menu):
                        fragment = new SettingFragment();
                        break;
                    case (R.id.chart_menu):
                        fragment = new ChartFragment();
                        break;
                }
                return loadFragmeent(fragment);
            }
        });
        /*--------------------------BottomNavigationBar을 통한 플래그먼트 이동부분(메소드는 밑에있음)------------*/
    }

    public void getRealtimeDB() {
        now_temp = realtimeDB_Now.child("temp");
        now_hum = realtimeDB_Now.child("hum");
        now_birghtness = realtimeDB_Now.child("brightness");
        now_waterLevel = realtimeDB_Now.child("water_level");

        hope_temp = realtimeDB_Hope.child("temp");
        hope_hum = realtimeDB_Hope.child("hum");
        hope_brightness = realtimeDB_Hope.child("brightness");
        hope_water_level = realtimeDB_Hope.child("water_level");
    }

    /*-------------플래그먼트 로드 메소드------------*/
    public boolean loadFragmeent(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();
            return true;
        }
        return false;
    }
    /*-------------플래그먼트 로드 메소드------------*/

    /*------------------------데이터베이스 쓰레드 ----------------------*/
    public void dbThread() {
        Executor executor = Executors.newSingleThreadExecutor(); // 싱글쓰레드익스큐터로 DB작업 백그라운드 처리.

        executor.execute(new Runnable() {
            @Override
            public void run() {

                realtimeDB_Hope = fireRealtimeDB.getReference("USER/" + uid + "/hope"); // Firebase Realtime Database 인스턴스의 참조를 생성
                realtimeDB_Now = fireRealtimeDB.getReference("USER/" + uid + "/now");

                getRealtimeDB(); //리얼타임 DB 참조

                // 유저의 FireStore 데이터 갖고오기.
                fireStoreDB.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();

                        if (data.containsKey("USER_NAME")) {
                            userName = (String) data.get("USER_NAME");
                        }
                        if (data.containsKey("STATUS")) {
                            status = (boolean) data.get("STATUS");
                        }
                        if (data.containsKey("DATE")) {
                            startDate = (String) data.get("DATE");
                        }

                        //작업후 기본 프래그먼트를 HomeFragment로 설정
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragment = new HomeFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();

                                userName_TextView.setText(userName + "님");
                                showMyfarmStatus(); // 내 농작물 현황을 업데이트하는 메소드
                            }
                        });
                    }
                });
            }
        });
    }
    /*------------------------데이터베이스 쓰레드 ----------------------*/

    public void changeDataListener() {
        displayThread(MainFragment.now_temp, HomeFragment.nowTempTV, "˚");
        displayThread(MainFragment.now_hum, HomeFragment.nowHumTV, "%");
        displayThread(MainFragment.now_birghtness, HomeFragment.nowBrightnessTV, "%");
        displayThread(MainFragment.now_waterLevel, HomeFragment.nowWaterLevelTV, "%");
    }


    /*------------------값 표출부분 ------------------------------------------*/
    public void displayThread(DatabaseReference def, TextView textView, String s) {
        def.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    long Value = (long) snapshot.getValue();
                    textView.setText(Value + s);
                    Log.d("SUCCESS",snapshot.getKey() + " : " + snapshot.getValue() +":" + Value);
                } else {
                    Log.w("MainFragment", "DataSnapshot does not exist or has no value!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MainFragment", "Failed to read value.", error.toException());
            }
        });
    }
    /*--------------------------값 표출부분 끝 -----------------------------*/

    public void showMyfarmStatus() {
        fireStoreDB.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();

                changeDataListener(); // 텍스트뷰에 데이터 업데이트 하는 메소드

                String crops = (String) data.get("CROPS");
                String date = (String) data.get("DATE");
                String expireDate = (String) data.get("EXPIRE_DATE");
                String name = (String) data.get("USER_NAME");

                HomeFragment.myNameTV.setText(name);

                switch (crops) {
                    case "상추":
                        HomeFragment.cropsTV.setText(crops);
                        HomeFragment.cropsImageView.setImageResource(R.drawable.sangchu);
                        break;
                    default:
                        break;
                }
                HomeFragment.startDateTV.setText(date);
                HomeFragment.expireDateTV.setText(expireDate);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    // 몇일차이인지 계산
                    Date mDate = dateFormat.parse(date);
                    Date mTodayDate = dateFormat.parse(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())); //오늘날짜

                    long growthMillis = mTodayDate.getTime() - mDate.getTime();
                    HomeFragment.growthDate = TimeUnit.DAYS.convert(growthMillis, TimeUnit.MILLISECONDS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                HomeFragment.growthDateTV.setText("" + HomeFragment.growthDate + "일");

                if (HomeFragment.growthDate > 45) {
                    //재배
                    HomeFragment.growthStateTV.setText("재배");
                }
                if (HomeFragment.growthDate < 45) {
                    //성장
                    HomeFragment.growthStateTV.setText("성장");
                }
                if (HomeFragment.growthDate < 21) {
                    //묘목
                    HomeFragment.growthStateTV.setText("묘목");
                }
                if (HomeFragment.growthDate < 7) {
                    //발아
                    HomeFragment.growthStateTV.setText("발아");
                }

            }
        });
    }
}

