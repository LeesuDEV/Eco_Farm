package com.example.proto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {

    TextView hopeTempTV,hopeHumTV,hopeBrightnessTV,hopeWaterLevelTV; //현재 온도를 표시할 텍스트뷰
    EditText hopeTempET,hopeHumET,hopeBrightnessET,hopeWaterLevelET;
    Button hopeTempSetBtn,hopeHumSetBtn,hopeBrightnessSetBtn,hopeWaterLevelBtn;
    TextView PresetModeTV,PresetModeNoticeTV;

    String name;
    boolean statusOfPreset = false;
    int value;

    public SettingFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,Bundle bundle){
        return inflater.inflate(R.layout.setting,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle){
        super.onViewCreated(view,bundle);

        hopeTempTV = view.findViewById(R.id.hopeTempTV);
        hopeHumTV = view.findViewById(R.id.hopeHumTV);
        hopeBrightnessTV = view.findViewById(R.id.hopeBrightnessTV);
        hopeWaterLevelTV = view.findViewById(R.id.hopeWaterLevelTV);

        hopeTempET = view.findViewById(R.id.hopeTempET);
        hopeHumET = view.findViewById(R.id.hopeHumET);
        hopeBrightnessET = view.findViewById(R.id.hopeBrightnessET);
        hopeWaterLevelET = view.findViewById(R.id.hopeWaterLevelET);

        hopeTempSetBtn = view.findViewById(R.id.hopeTempSetBtn);
        hopeHumSetBtn = view.findViewById(R.id.hopeHumSetBtn);
        hopeBrightnessSetBtn = view.findViewById(R.id.hopeBrightnessSetBtn);
        hopeWaterLevelBtn = view.findViewById(R.id.hopeWaterLevelBtn);

        PresetModeTV = view.findViewById(R.id.PresetModeTV);
        PresetModeNoticeTV = view.findViewById(R.id.PresetModeNoticeTV);

        setPresetMode(); // 프리셋모드 설정을 해주는 메소드

        setChangeDataListener(); // 희망온도 체인지 리스너 메소드

        hopeTempSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "temp";
                setHopeValue(hopeTempET,MainFragment.hope_temp);
            }
        });

        hopeHumSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "hum";
                setHopeValue(hopeHumET,MainFragment.hope_hum);
            }
        });

        hopeBrightnessSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "brightness";
                setHopeValue(hopeBrightnessET,MainFragment.hope_brightness);
            }
        });

        hopeWaterLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "water_value";
                setHopeValue(hopeWaterLevelET,MainFragment.hope_water_level);
            }
        });
    }

    private void setPresetMode(){
        MainFragment.fireStore_MyDB.collection("preset").document("preset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null && error != null){
                    Log.d("ERROR","ERROR");
                    return;
                }

                if(value.getBoolean("usePreset")){
                    PresetModeTV.setText("프리셋모드 입니다");
                    PresetModeNoticeTV.setVisibility(View.VISIBLE);
                    statusOfPreset = true;
                } else {
                    PresetModeTV.setText("수동세팅모드 입니다");
                    PresetModeNoticeTV.setVisibility(View.GONE);
                    statusOfPreset = false;
                }
            }
        });
    }

    public void setHopeValue(EditText et,DatabaseReference def) {
        if (!statusOfPreset) {
            if (et.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "값이 비어있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(et.getText().toString()) > 100) {
                Toast.makeText(getContext(), "100을 초과하지 마세요!", Toast.LENGTH_SHORT).show();
                return;
            }
            def.setValue(Integer.parseInt(et.getText().toString()));

            value = Integer.parseInt(et.getText().toString());

            Map<String, Object> data = new HashMap<>();
            data.put(name, value);

            MainFragment.fireStore_MyDB.collection("hope").document("hope").update(data);
            Toast.makeText(getContext(), "업데이트 됐습니다!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "프리셋모드 적용중입니다.(제어불가능)", Toast.LENGTH_SHORT).show();
        }
    }

    public void setChangeDataListener(){
        displayThread(MainFragment.hope_temp, hopeTempTV, "˚");
        displayThread(MainFragment.hope_hum, hopeHumTV, "%");
        displayThread(MainFragment.hope_brightness, hopeBrightnessTV, "%");
        displayThread(MainFragment.hope_water_level, hopeWaterLevelTV, "%");
    }

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
}
