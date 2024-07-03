package com.example.proto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class PresetFragment extends Fragment {
    TextView currentPresetTV,currentValueTV,currentWeekTV;

    public PresetFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.preset, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentPresetTV = view.findViewById(R.id.currentPresetTV);
        currentValueTV = view.findViewById(R.id.currentValueTV);
        currentWeekTV = view.findViewById(R.id.currentWeekTV);

        updateValue();

        MainFragment.fireStore_MyDB.collection("preset").document("preset").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });

    }

    // 텍스트뷰에 값을 업데이트 해주는 메소드
    public void updateValue(){
        // 몇주차인지 업데이트
        MainFragment.fireStore_MyDB.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentWeekTV.setText(documentSnapshot.get("WEEK").toString());
            }
        });

        //사용하고 있는 프리셋이름과 현재주차 값 로딩
        MainFragment.fireStore_MyDB.collection("preset").document("preset").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                currentPresetTV.setText(name);

                String[] tmp_ls;
                switch (MainFragment.week) {
                    case 1:
                        tmp_ls = documentSnapshot.getString("1week").split(";");
                        currentValueTV.setText("온도 : "+tmp_ls[0]+"˚습도 : "+tmp_ls[1]+"%  조도 : "+tmp_ls[2]+"%");
                        break;
                    case 2:
                        tmp_ls = documentSnapshot.getString("2week").split(";");
                        currentValueTV.setText("온도 : "+tmp_ls[0]+"˚습도 : "+tmp_ls[1]+"%  조도 : "+tmp_ls[2]+"%");
                        break;
                    case 3:
                        tmp_ls = documentSnapshot.getString("3week").split(";");
                        currentValueTV.setText("온도 : "+tmp_ls[0]+"˚습도 : "+tmp_ls[1]+"%  조도 : "+tmp_ls[2]+"%");
                        break;
                    case 4:
                        tmp_ls = documentSnapshot.getString("4week").split(";");
                        currentValueTV.setText("온도 : "+tmp_ls[0]+"˚습도 : "+tmp_ls[1]+"%  조도 : "+tmp_ls[2]+"%");
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
