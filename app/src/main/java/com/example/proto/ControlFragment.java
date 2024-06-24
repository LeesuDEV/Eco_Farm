package com.example.proto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ControlFragment extends Fragment {

    Switch ledSw;

    DatabaseReference ledStatus;

    public ControlFragment (){

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        return layoutInflater.inflate(R.layout.control,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ledStatus != null) {
            displayStatusThread(ledStatus,ledSw);  //LED상태 실시간동기화 쓰레드
        }

        /*----------------------앱 LED스위치 변경시 데이터 업로드--------------------------*/
        ledSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ledStatus.setValue(true);
                } else {
                    ledStatus.setValue(false);
                }
            }
        });
        /*----------------------앱 LED스위치 변경시 데이터 업로드--------------------------*/

    }

    /*------------------led 실시간 on/off 표출메소드 ------------------------------------------*/
    public void displayStatusThread(DatabaseReference def, Switch sw) {
        def.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean statusValue = snapshot.getValue(Boolean.class); //

                sw.setChecked(statusValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /*-----------------------led 실시간 on/off 표출메소드 -----------------------------*/
}
