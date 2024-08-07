package com.example.proto;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.checker.units.qual.C;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
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

    static DocumentReference fireStore_MyDB; //DB값 -UID처리 돼있음
    static CollectionReference cropsHub_DB; // 작물허브 DB

    static FirebaseDatabase fireRealtimeDB = FirebaseDatabase.getInstance(); // 리얼타임 데이터베이스 변수선언
    static FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance(); // 파이어스토어 데이터베이스 변수선언

    static String uid, userName;

    static boolean farmStatus = false; // 작물 키우고있나? 아닌가? 상태
    static String startDate; // 식물 성장 시작일
    static int week; // 식물 성장 주
    static int daysBetween; //몇일차

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

        loadDB();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();                            //액션바 숨기기

        // BottomNavigationBar을 통한 플래그먼트 이동부분(메소드는 밑에있음)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Log.d("item?", String.valueOf(item));

                switch (item.getItemId()) {
                    case (R.id.mainmenu):
                        fragment = new HomeFragment();
                        break;
                    case (R.id.controll_menu):
                        fragment = new PresetFragment();
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

        //기본 프래그먼트를 StartFragment로 설정
        fragment = new StartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();
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
    public void loadDB() {
        realtimeDB_Hope = fireRealtimeDB.getReference("USER/" + uid + "/hope"); // Firebase Realtime Database 인스턴스의 참조를 생성
        realtimeDB_Now = fireRealtimeDB.getReference("USER/" + uid + "/now");

        cropsHub_DB = FirebaseFirestore.getInstance().collection("crops");

        fireStore_MyDB = FirebaseFirestore.getInstance().collection("users").document(uid); // FireStore값 참조

        now_temp = realtimeDB_Now.child("temp");
        now_hum = realtimeDB_Now.child("hum");
        now_birghtness = realtimeDB_Now.child("brightness");
        now_waterLevel = realtimeDB_Now.child("water_level");

        hope_temp = realtimeDB_Hope.child("temp");
        hope_hum = realtimeDB_Hope.child("hum");
        hope_brightness = realtimeDB_Hope.child("brightness");
        hope_water_level = realtimeDB_Hope.child("water_level");

        // 유저의 FireStore 데이터 갖고오기.
        fireStore_MyDB.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();

                if (data.containsKey("USER_NAME")) {
                    userName = (String) data.get("USER_NAME");
                    userName_TextView.setText(userName + "님");
                }
                if (data.containsKey("DATE")) {
                    startDate = (String) data.get("DATE");
                }

                if (!startDate.isEmpty()) {
                    int year, month, day;

                    Calendar calendar = Calendar.getInstance(); //오늘날짜 받아오기
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH) + 1;
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    LocalDate localDate = LocalDate.of(year, month, day);

                    String[] splitStartDate = startDate.split("-"); //시작날짜 받아오기
                    year = Integer.parseInt(splitStartDate[0]);
                    month = Integer.parseInt(splitStartDate[1]);
                    day = Integer.parseInt(splitStartDate[2]);
                    LocalDate startLocalDate = LocalDate.of(year, month, day);

                    // 시작날짜 - 오늘날짜 차이 계산
                    daysBetween = (int) ChronoUnit.DAYS.between(startLocalDate, localDate);
                    Log.d("daysBetween", String.valueOf(daysBetween));

                    week = daysBetween / 7;

                    showMyfarmStatus(); //팜정보 로드
                }


            }
        });
    }

    static String crops;
    static String date;
    static String expireDate;
    static String name;
    static String term;
    static DocumentSnapshot presetSnapshot;

    public void showMyfarmStatus() {
        MainFragment.fireStore_MyDB.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Map<String, Object> data = value.getData();

                MainFragment.farmStatus = Boolean.parseBoolean(data.get("farmStatus").toString());

                crops = (String) data.get("CROPS");
                date = (String) data.get("DATE");
                expireDate = (String) data.get("EXPIRE_DATE");
                name = (String) data.get("USER_NAME");

                // 현재 작물 상태 - 프리셋기준 비교
                MainFragment.fireStore_MyDB.collection("preset").document("preset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        presetSnapshot = value;

                        String[] term1_ls = value.get("term1").toString().split(";");
                        String[] term2_ls = value.get("term2").toString().split(";");
                        String[] term3_ls = value.get("term3").toString().split(";");
                        String[] term4_ls = value.get("term4").toString().split(";");

                        if (MainFragment.daysBetween >= Integer.parseInt(term4_ls[0]) * 7) { //수확시기
                            term = "term5";
                        }
                        if (MainFragment.daysBetween < Integer.parseInt(term4_ls[0]) * 7) { //텀4보다 숫자작을시 ~
                            term = "term4";
                        }
                        if (MainFragment.daysBetween < Integer.parseInt(term3_ls[0]) * 7) {
                            term = "term3";
                        }
                        if (MainFragment.daysBetween < Integer.parseInt(term2_ls[0]) * 7) {
                            term = "term2";
                        }
                        if (MainFragment.daysBetween < Integer.parseInt(term1_ls[0]) * 7) { //텀1보다 숫자 작을시
                            term = "term1";
                        }
                    }
                });
            }
        });
    }
}

