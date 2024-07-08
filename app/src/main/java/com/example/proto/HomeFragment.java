package com.example.proto;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    LinearLayout newFarm, farmStatus;
    TextView selectFarmTV, carefulTV, registerFarmBtn;
    static TextView myNameTV;
    static TextView startDateTV, expireDateTV, growthDateTV, growthStateTV;
    static TextView nowTempTV, nowHumTV, nowBrightnessTV, nowWaterLevelTV;
    static TextView cropsTV;
    static ImageView cropsImageView;

    int expireDate;
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.main, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);

        farmStatus = view.findViewById(R.id.farmStatus);
        newFarm = view.findViewById(R.id.newFarm);

        selectFarmTV = view.findViewById(R.id.selectFarmTV);
        carefulTV = view.findViewById(R.id.carefulTV);
        registerFarmBtn = view.findViewById(R.id.registerFarmBtn);

        myNameTV = view.findViewById(R.id.myNameTV);

        cropsTV = view.findViewById(R.id.cropsTV);
        cropsImageView = view.findViewById(R.id.cropsImageView);

        startDateTV = view.findViewById(R.id.startDateTV);
        expireDateTV = view.findViewById(R.id.expireDateTV);
        growthDateTV = view.findViewById(R.id.growthDateTV);
        growthStateTV = view.findViewById(R.id.growthStateTV);

        nowTempTV = view.findViewById(R.id.nowTempTV);
        nowHumTV = view.findViewById(R.id.nowHumTV);
        nowBrightnessTV = view.findViewById(R.id.nowBrightnessTV);
        nowWaterLevelTV = view.findViewById(R.id.nowWaterLevelTV);

        updateFarmLayout(); // 농작물 유무에 따라 레이아웃 업데이트 해주는 메소드

        changeDataListener(); // 텍스트뷰에 Realtime DB 데이터 업데이트 하는 메소드

        loadDataOnTextView();  //로드데이터 기반으로 텍스트뷰 업데이트

        selectFarmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog(); // 농작물 선택 다이어로그
            }
        });

        registerFarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, expireDate);
                Date expireDate = calendar.getTime();
                String expireDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(expireDate);

                Map<String, Object> data = new HashMap<>();
                data.put("CROPS", selectFarmTV.getText().toString());
                data.put("DATE", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                data.put("EXPIRE_DATE", expireDateString);
                data.put("STATUS", true);


                //등록데이터 입력
                MainFragment.fireStoreDB.collection("users").document(MainFragment.uid).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "등록 성공!", Toast.LENGTH_SHORT).show();
                        MainFragment.farmStatus = true;
                        newFarm.setVisibility(View.GONE);
                        farmStatus.setVisibility(View.VISIBLE);

                        expireDateTV.setText(expireDateString); // 파기일 수동업데이트
                    }
                });
            }
        });
    }


    //로드데이터 기반으로 텍스트뷰 업데이트
    private void loadDataOnTextView() {
        myNameTV.setText(MainFragment.name);

        switch (MainFragment.crops) {
            case "상추":
                cropsTV.setText(MainFragment.crops);
                cropsImageView.setImageResource(R.drawable.sangchu);
                break;
            default:
                break;
        }

        HomeFragment.startDateTV.setText(MainFragment.date);
        HomeFragment.expireDateTV.setText(MainFragment.expireDate);
        HomeFragment.growthDateTV.setText(MainFragment.daysBetween + "일");

        switch (MainFragment.term){
            case "term5" :
                HomeFragment.growthStateTV.setText("수확");
                break;
            case "term4" :
                HomeFragment.growthStateTV.setText("성숙");
                break;
            case "term3" :
                HomeFragment.growthStateTV.setText("생장");
                break;
            case "term2" :
                HomeFragment.growthStateTV.setText("묘목");
                break;
            case "term1" :
                HomeFragment.growthStateTV.setText("발아");
                break;
        }
    }

    //농작물 선택 다이어로그
    public void chooseDialog() {
        final String[] words = new String[]{"상추"};

        new AlertDialog.Builder(getContext()).setTitle("농작물 선택").setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectFarmTV.setText(words[which]);
                Toast.makeText(getContext(), "words : " + words[which], Toast.LENGTH_SHORT).show();

                switch (words[which]) {
                    case "상추":
                        carefulTV.setText("평균소요일 : 42일\n\n벌레꼬임 : 적음\n\n방식 : 에어로팜\n\n난이도 : 쉬움");
                        expireDate = 42;
                        break;
                    default:
                        break;
                }
            }
        }).setNeutralButton("closed", null).setPositiveButton("OK", null).setNegativeButton("cancel", null).show();
    }


    // 텍스트뷰에 Realtime DB 데이터 업데이트 하는 메소드
    public void changeDataListener() {
        displayThread(MainFragment.now_temp, HomeFragment.nowTempTV, "˚");
        displayThread(MainFragment.now_hum, HomeFragment.nowHumTV, "%");
        displayThread(MainFragment.now_birghtness, HomeFragment.nowBrightnessTV, "%");
        displayThread(MainFragment.now_waterLevel, HomeFragment.nowWaterLevelTV, "%");
    }

    // realtimeDB 이벤트리스너를 달아주는 메소드 - changeDataListener랑 연계
    public void displayThread(DatabaseReference def, TextView textView, String s) {
        def.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    long Value = (long) snapshot.getValue();
                    textView.setText(Value + s);
                    Log.d("SUCCESS", snapshot.getKey() + " : " + snapshot.getValue() + ":" + Value);
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

    // 농작물 유무에 따라 레이아웃 업데이트 해주는 메소드
    private void updateFarmLayout() {
        //팜 활성화에 따라 레이아웃 업데이트
        if (MainFragment.farmStatus) {
            newFarm.setVisibility(View.GONE);
            farmStatus.setVisibility(View.VISIBLE);
        } else {
            newFarm.setVisibility(View.VISIBLE);
            farmStatus.setVisibility(View.GONE);
        }
    }

}
