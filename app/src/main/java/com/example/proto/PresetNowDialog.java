package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PresetNowDialog extends Dialog {

    Context context;

    TextView Preset1WeekScopeTV, Preset2WeekScopeTV, Preset3WeekScopeTV,Preset4WeekScopeTV;
    TextView Preset1WeekTempTV,Preset1WeekHumTV,Preset1WeekBrightnessTV;
    TextView Preset2WeekTempTV,Preset2WeekHumTV,Preset2WeekBrightnessTV;
    TextView Preset3WeekTempTV,Preset3WeekHumTV,Preset3WeekBrightnessTV;
    TextView Preset4WeekTempTV,Preset4WeekHumTV,Preset4WeekBrightnessTV;
    TextView week1TV,week2TV,week3TV,week4TV;
    public PresetNowDialog(Context context){
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_now);

        Preset1WeekScopeTV = findViewById(R.id.Preset1WeekScopeTV);
        Preset2WeekScopeTV = findViewById(R.id.Preset2WeekScopeTV);
        Preset3WeekScopeTV = findViewById(R.id.Preset3WeekScopeTV);
        Preset4WeekScopeTV = findViewById(R.id.Preset4WeekScopeTV);

        Preset1WeekTempTV = findViewById(R.id.Preset1WeekTempTV);
        Preset1WeekHumTV = findViewById(R.id.Preset1WeekHumTV);
        Preset1WeekBrightnessTV = findViewById(R.id.Preset1WeekBrightnessTV);
        Preset2WeekTempTV = findViewById(R.id.Preset2WeekTempTV);
        Preset2WeekHumTV = findViewById(R.id.Preset2WeekHumTV);
        Preset2WeekBrightnessTV = findViewById(R.id.Preset2WeekBrightnessTV);
        Preset3WeekTempTV = findViewById(R.id.Preset3WeekTempTV);
        Preset3WeekHumTV = findViewById(R.id.Preset3WeekHumTV);
        Preset3WeekBrightnessTV = findViewById(R.id.Preset3WeekBrightnessTV);
        Preset4WeekTempTV = findViewById(R.id.Preset4WeekTempTV);
        Preset4WeekHumTV = findViewById(R.id.Preset4WeekHumTV);
        Preset4WeekBrightnessTV = findViewById(R.id.Preset4WeekBrightnessTV);

        week1TV = findViewById(R.id.week1TV);
        week2TV = findViewById(R.id.week2TV);
        week3TV = findViewById(R.id.week3TV);
        week4TV = findViewById(R.id.week4TV);

        loadMyPreset();


    }

    public void loadMyPreset(){
        MainFragment.fireStore_MyDB.collection("preset").document("preset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d("error","error");
                    return;
                }
                if (value == null){
                    Log.d("empty","empty");
                    return;
                }

                String[] week1 = value.get("1week").toString().split(";");
                String[] week2 = value.get("2week").toString().split(";");
                String[] week3 = value.get("3week").toString().split(";");
                String[] week4 = value.get("4week").toString().split(";");

                // 모든 주 프리셋 불러옴
                Preset1WeekScopeTV.setText("~"+week1[0]+"주");
                Preset1WeekTempTV.setText(week1[1]+"˚C");
                Preset1WeekHumTV.setText(week1[2]+"%");
                Preset1WeekBrightnessTV.setText(week1[3]+"%");

                Preset2WeekScopeTV.setText("~"+week2[0]+"주");
                Preset2WeekTempTV.setText(week2[1]+"˚C");
                Preset2WeekHumTV.setText(week2[2]+"%");
                Preset2WeekBrightnessTV.setText(week2[3]+"%");

                Preset3WeekScopeTV.setText("~"+week3[0]+"주");
                Preset3WeekTempTV.setText(week3[1]+"˚C");
                Preset3WeekHumTV.setText(week3[2]+"%");
                Preset3WeekBrightnessTV.setText(week3[3]+"%");

                Preset4WeekScopeTV.setText("~"+week4[0]+"주");
                Preset4WeekTempTV.setText(week4[1]+"˚C");
                Preset4WeekHumTV.setText(week4[2]+"%");
                Preset4WeekBrightnessTV.setText(week4[3]+"%");

                //해당 주 초록색강조함
                switch (MainFragment.week) {
                    case 1:
                        week1TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1WeekScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1WeekTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1WeekHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1WeekBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                    case 2:
                        week2TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2WeekScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2WeekTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2WeekHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2WeekBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                    case 3:
                        week3TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3WeekScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3WeekTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3WeekHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3WeekBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                    case 4:
                        week4TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4WeekScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4WeekTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4WeekHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4WeekBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                }


            }
        });
    }
}
