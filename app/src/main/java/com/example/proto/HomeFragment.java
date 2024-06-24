package com.example.proto;

import android.content.DialogInterface;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    LinearLayout newFarm,farmStatus;
    TextView selectFarmTV,carefulTV,registerFarmBtn;
    static TextView myNameTV;
    static TextView startDateTV,expireDateTV,growthDateTV,growthStateTV;
    static TextView nowTempTV,nowHumTV,nowBrightnessTV,nowWaterLevelTV;
    static TextView cropsTV;
    static ImageView cropsImageView;

    int expireDate;
    static long growthDate;

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

        //팜 활성화에 따라 레이아웃 업데이트
        if (MainFragment.status) {
            newFarm.setVisibility(View.GONE);
            farmStatus.setVisibility(View.VISIBLE);
        } else {
            newFarm.setVisibility(View.VISIBLE);
            farmStatus.setVisibility(View.GONE);
        }

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

                Map<String,Object>data = new HashMap<>();
                data.put("CROPS",selectFarmTV.getText().toString());
                data.put("DATE",new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                data.put("EXPIRE_DATE",expireDateString);
                data.put("STATUS",true);


                //등록데이터 입력
                MainFragment.fireStoreDB.collection("users").document(MainFragment.uid).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"등록 성공!",Toast.LENGTH_SHORT).show();
                        MainFragment.status = true;
                        newFarm.setVisibility(View.GONE);
                        farmStatus.setVisibility(View.VISIBLE);

                        expireDateTV.setText(expireDateString); // 파기일 수동업데이트
                    }
                });
            }
        });
    }

    public void chooseDialog(){
        final String[] words = new String[] {"상추"};

        new AlertDialog.Builder(getContext()).setTitle("농작물 선택").setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectFarmTV.setText(words[which]);
                Toast.makeText(getContext(), "words : " + words[which], Toast.LENGTH_SHORT).show();

                switch (words[which]){
                    case "상추" :
                        carefulTV.setText("평균소요일 : 21일\n\n벌레꼬임 : 적음\n\n방식 : 에어로팜\n\n난이도 : 쉬움");
                        expireDate = 21;
                        break;
                    default:
                        break;
                }
            }
        }).setNeutralButton("closed",null).setPositiveButton("OK",null).setNegativeButton("cancel", null).show();
    } //농작물 선택 다이어로그



}
