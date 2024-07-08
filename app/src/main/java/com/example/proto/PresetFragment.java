package com.example.proto;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class PresetFragment extends Fragment {
    TextView currentPresetTV, currentValueTV, currentWeekTV;
    String week;
    String week_str;
    TextView presetHubBtn, presetNowBtn, presetFixBtn;
    Switch PresetSwitch;
    LinearLayout PresetLayout;

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

        presetHubBtn = view.findViewById(R.id.presetHubBtn);
        presetNowBtn = view.findViewById(R.id.presetNowBtn);
        presetFixBtn = view.findViewById(R.id.presetFixBtn);

        PresetSwitch = view.findViewById(R.id.PresetSwitch);
        PresetLayout = view.findViewById(R.id.PresetLayout);

        updateValue();

        MainFragment.fireStore_MyDB.collection("preset").document("preset").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });

        presetHubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresetHubDialog dialog = new PresetHubDialog(getContext());
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //다이어로그 배경 투명처리
            }
        });

        presetNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresetNowDialog dialog = new PresetNowDialog(getContext());
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //다이어로그 배경 투명처리
            }
        });


        presetFixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresetFixDialog dialog = new PresetFixDialog(getContext());
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //다이어로그 배경 투명처리
            }
        });

        setPresetStatus();

        PresetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PresetLayout.setVisibility(View.VISIBLE);
                    Map<String,Object>obj = new HashMap<>();
                    obj.put("usePreset",true);
                    MainFragment.fireStore_MyDB.collection("preset").document("preset").update(obj);
                } else {
                    PresetLayout.setVisibility(View.GONE);
                    Map<String,Object>obj = new HashMap<>();
                    obj.put("usePreset",false);
                    MainFragment.fireStore_MyDB.collection("preset").document("preset").update(obj);
                }
            }
        });
    }

    //DB데이터를 기반으로 프리셋 사용여부를 설정.
    public void setPresetStatus(){
        MainFragment.fireStore_MyDB.collection("preset").document("preset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                boolean check = value.getBoolean("usePreset");

                if (check) {
                    PresetSwitch.setChecked(true);
                } else {
                    PresetSwitch.setChecked(false);
                }
            }
        });
    }

    // 텍스트뷰에 값을 업데이트 해주는 메소드
    public void updateValue() {
        // 몇주차인지 업데이트
        currentWeekTV.setText(MainFragment.week + "주차");

        //사용하고 있는 프리셋이름과 현재주차 값 로딩
        MainFragment.fireStore_MyDB.collection("preset").document("preset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("Failed Preset Listener", "Failed Preset Listener");
                    return;
                }

                //프리셋 이름
                String name = value.getString("name");
                currentPresetTV.setText(name);

                String[] tmp_ls;
                //텍스트뷰에 현재주차 프리셋 온습도 업데이트
                tmp_ls = value.getString(MainFragment.term).split(";");
                currentValueTV.setText("온도 : " + tmp_ls[1] + "˚습도 : " + tmp_ls[2] + "%  조도 : " + tmp_ls[3] + "%");

            }
        });
    }
}
