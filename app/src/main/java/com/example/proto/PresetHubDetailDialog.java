package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PresetHubDetailDialog extends Dialog {
    Context context;
    TextView PresetNameTV,PresetCommentTV;
    TextView Preset1WeekTempTV,Preset1WeekHumTV,Preset1WeekBrightnessTV;
    TextView Preset2WeekTempTV,Preset2WeekHumTV,Preset2WeekBrightnessTV;
    TextView Preset3WeekTempTV,Preset3WeekHumTV,Preset3WeekBrightnessTV;
    TextView Preset4WeekTempTV,Preset4WeekHumTV,Preset4WeekBrightnessTV;
    TextView PresetHitTV;
    TextView PresetSubmitBtn;

    public PresetHubDetailDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presethub_detail);

        PresetNameTV = findViewById(R.id.PresetNameTV);
        PresetCommentTV = findViewById(R.id.PresetCommentTV);

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

        PresetHitTV = findViewById(R.id.PresetHitTV);
        PresetSubmitBtn = findViewById(R.id.PresetSubmitBtn);

         loadPresetDetail(); //세부정보 가져오기
    }

    private void loadPresetDetail(){
        if (PresetHubDialog.selectedItem == null){
            Toast.makeText(context,"No selected Item",Toast.LENGTH_SHORT).show();
            return;
        }

        MainFragment.cropsHub_DB.document(PresetHubDialog.selectedItem).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("error","failed");
                    return;
                }
                if (value == null) {
                    Log.d("empty","empty");
                }
                PresetNameTV.setText(value.getId());
                PresetCommentTV.setText(value.get("comment").toString());

                String[] week1 = value.get("1week").toString().split(";");
                String[] week2 = value.get("2week").toString().split(";");
                String[] week3 = value.get("3week").toString().split(";");
                String[] week4 = value.get("4week").toString().split(";");

                Preset1WeekTempTV.setText(week1[0]+"˚C");
                Preset1WeekHumTV.setText(week1[1]+"%");
                Preset1WeekBrightnessTV.setText(week1[2]+"%");

                Preset2WeekTempTV.setText(week2[0]+"˚C");
                Preset2WeekHumTV.setText(week2[1]+"%");
                Preset2WeekBrightnessTV.setText(week2[2]+"%");

                Preset3WeekTempTV.setText(week3[0]+"˚C");
                Preset3WeekHumTV.setText(week3[1]+"%");
                Preset3WeekBrightnessTV.setText(week3[2]+"%");

                Preset4WeekTempTV.setText(week4[0]+"˚C");
                Preset4WeekHumTV.setText(week4[1]+"%");
                Preset4WeekBrightnessTV.setText(week4[2]+"%");

                PresetHitTV.setText(""+value.get("hit").toString());
            }
        });
    }
}
