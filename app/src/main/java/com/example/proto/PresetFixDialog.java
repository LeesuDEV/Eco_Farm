package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PresetFixDialog extends Dialog {

    Context context;
    TextView PresetNameTV, PresetCommentTV;

    EditText Preset1WeekScopeTV, Preset2WeekScopeTV, Preset3WeekScopeTV,Preset4WeekScopeTV;

    EditText Preset1WeekTempTV, Preset1WeekHumTV, Preset1WeekBrightnessTV;
    EditText Preset2WeekTempTV, Preset2WeekHumTV, Preset2WeekBrightnessTV;
    EditText Preset3WeekTempTV, Preset3WeekHumTV, Preset3WeekBrightnessTV;
    EditText Preset4WeekTempTV, Preset4WeekHumTV, Preset4WeekBrightnessTV;
    TextView week1TV, week2TV, week3TV, week4TV;
    TextView PresetFixSubmitBtn, PresetHubRegisterBtn;

    static String registerItem;
    static Boolean existUID = false;
    static Boolean isCancel = false;
    int num;

    public PresetFixDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_fix);

        PresetNameTV = findViewById(R.id.PresetNameTV);
        PresetCommentTV = findViewById(R.id.PresetCommentTV);

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

        PresetFixSubmitBtn = findViewById(R.id.PresetFixSubmitBtn);
        PresetHubRegisterBtn = findViewById(R.id.PresetHubRegisterBtn);

        PresetFixSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyEditPreset(); //프리셋 업데이트
            }
        });

        PresetHubRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresetRegisterDialog dialog = new PresetRegisterDialog(context, new PresetRegisterDialog.PresetDeleteDialogListener() {
                    @Override
                    public void onDialogDismissed() {
                        // 내 uid로 등록된 글이 없다면
                        if (isCancel) {
                            return;
                        }

                        if (!existUID) {
                            registerPreset();
                        } else {
                            Toast.makeText(context, "이미 내 프리셋이 허브에 등록돼있어요!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });


        loadMyPreset(); // 기존 프리셋을 로드하는 메소드
    }

    private void loadMyPreset() {
        MainFragment.fireStore_MyDB.collection("preset").document("preset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("error", "error");
                    return;
                }
                if (value == null) {
                    Log.d("empty", "empty");
                    return;
                }
                PresetNameTV.setText(value.get("name").toString());
                PresetCommentTV.setText(value.get("comment").toString());

                String[] week1 = value.get("1week").toString().split(";");
                String[] week2 = value.get("2week").toString().split(";");
                String[] week3 = value.get("3week").toString().split(";");
                String[] week4 = value.get("4week").toString().split(";");

                // 모든 주 프리셋 불러옴
                Preset1WeekScopeTV.setText(week1[0]);
                Preset1WeekTempTV.setText(week1[1]);
                Preset1WeekHumTV.setText(week1[2]);
                Preset1WeekBrightnessTV.setText(week1[3]);

                Preset2WeekScopeTV.setText(week2[0]);
                Preset2WeekTempTV.setText(week2[1]);
                Preset2WeekHumTV.setText(week2[2]);
                Preset2WeekBrightnessTV.setText(week2[3]);

                Preset3WeekScopeTV.setText(week3[0]);
                Preset3WeekTempTV.setText(week3[1]);
                Preset3WeekHumTV.setText(week3[2]);
                Preset3WeekBrightnessTV.setText(week3[3]);

                Preset4WeekScopeTV.setText(week4[0]);
                Preset4WeekTempTV.setText(week4[1]);
                Preset4WeekHumTV.setText(week4[2]);
                Preset4WeekBrightnessTV.setText(week4[3]);

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

    private void applyEditPreset() {
        String name = PresetNameTV.getText().toString();
        String comment = PresetCommentTV.getText().toString();

        String week1_scope = Preset1WeekScopeTV.getText().toString();
        String week1_temp = Preset1WeekTempTV.getText().toString();
        String week1_hum = Preset1WeekHumTV.getText().toString();
        String week1_brightness = Preset1WeekBrightnessTV.getText().toString();

        String week2_scope = Preset2WeekScopeTV.getText().toString();
        String week2_temp = Preset2WeekTempTV.getText().toString();
        String week2_hum = Preset2WeekHumTV.getText().toString();
        String week2_brightness = Preset2WeekBrightnessTV.getText().toString();

        String week3_scope = Preset3WeekScopeTV.getText().toString();
        String week3_temp = Preset3WeekTempTV.getText().toString();
        String week3_hum = Preset3WeekHumTV.getText().toString();
        String week3_brightness = Preset3WeekBrightnessTV.getText().toString();

        String week4_scope = Preset4WeekScopeTV.getText().toString();
        String week4_temp = Preset4WeekTempTV.getText().toString();
        String week4_hum = Preset4WeekHumTV.getText().toString();
        String week4_brightness = Preset4WeekBrightnessTV.getText().toString();

        String week1 = week1_scope + ";" + week1_temp + ";" + week1_hum + ";" + week1_brightness;
        String week2 = week2_scope + ";" + week2_temp + ";" + week2_hum + ";" + week2_brightness;
        String week3 = week3_scope + ";" + week3_temp + ";" + week3_hum + ";" + week3_brightness;
        String week4 = week4_scope + ";" + week4_temp + ";" + week4_hum + ";" + week4_brightness;

        //텍스트 중 하나라도 비어있다면.
        if (name.isEmpty() || comment.isEmpty() || week1_hum.isEmpty() || week1_brightness.isEmpty() || week1_temp.isEmpty() || week2_brightness.isEmpty() || week2_hum.isEmpty() || week2_temp.isEmpty() || week3_temp.isEmpty() || week3_hum.isEmpty() || week3_brightness.isEmpty() || week4_brightness.isEmpty() || week4_hum.isEmpty() || week4_temp.isEmpty()) {
            Toast.makeText(context, "빈칸이 존재해요!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Map에 데이터 준비
        Map<String, Object> obj = new HashMap<>();
        obj.put("name", name);
        obj.put("comment", comment);
        obj.put("1week", week1);
        obj.put("2week", week2);
        obj.put("3week", week3);
        obj.put("4week", week4);

        MainFragment.fireStore_MyDB.collection("preset").document("preset").update(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "프리셋 저장에 성공했어요!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerPreset() {
        String name = PresetNameTV.getText().toString();
        String comment = PresetCommentTV.getText().toString();
        String uid = MainFragment.uid;

        String week1_scope = Preset1WeekScopeTV.getText().toString();
        String week1_temp = Preset1WeekTempTV.getText().toString();
        String week1_hum = Preset1WeekHumTV.getText().toString();
        String week1_brightness = Preset1WeekBrightnessTV.getText().toString();

        String week2_scope = Preset2WeekScopeTV.getText().toString();
        String week2_temp = Preset2WeekTempTV.getText().toString();
        String week2_hum = Preset2WeekHumTV.getText().toString();
        String week2_brightness = Preset2WeekBrightnessTV.getText().toString();

        String week3_scope = Preset3WeekScopeTV.getText().toString();
        String week3_temp = Preset3WeekTempTV.getText().toString();
        String week3_hum = Preset3WeekHumTV.getText().toString();
        String week3_brightness = Preset3WeekBrightnessTV.getText().toString();

        String week4_scope = Preset4WeekScopeTV.getText().toString();
        String week4_temp = Preset4WeekTempTV.getText().toString();
        String week4_hum = Preset4WeekHumTV.getText().toString();
        String week4_brightness = Preset4WeekBrightnessTV.getText().toString();

        String week1 = week1_scope + ";" + week1_temp + ";" + week1_hum + ";" + week1_brightness;
        String week2 = week2_scope + ";" + week2_temp + ";" + week2_hum + ";" + week2_brightness;
        String week3 = week3_scope + ";" + week3_temp + ";" + week3_hum + ";" + week3_brightness;
        String week4 = week4_scope + ";" + week4_temp + ";" + week4_hum + ";" + week4_brightness;

        //텍스트 중 하나라도 비어있다면.
        if (name.isEmpty() || comment.isEmpty() || week1_hum.isEmpty() || week1_brightness.isEmpty() || week1_temp.isEmpty() || week2_brightness.isEmpty() || week2_hum.isEmpty() || week2_temp.isEmpty() || week3_temp.isEmpty() || week3_hum.isEmpty() || week3_brightness.isEmpty() || week4_brightness.isEmpty() || week4_hum.isEmpty() || week4_temp.isEmpty()) {
            Toast.makeText(context, "빈칸이 존재해요!", Toast.LENGTH_SHORT).show();
            return;
        }

        MainFragment.fireStoreDB.collection("hub").document("hubIndex").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                num = (int) documentSnapshot.getLong("index").longValue();
                num ++;

                //Map에 데이터 준비
                Map<String, Object> obj = new HashMap<>();
                obj.put("uid", uid);
                obj.put("comment", comment);
                obj.put("1week", week1);
                obj.put("2week", week2);
                obj.put("3week", week3);
                obj.put("4week", week4);
                obj.put("hit", 0);
                obj.put("number", num);

                Map<String, Object> numObj = new HashMap<>();
                numObj.put("index",num);

                MainFragment.fireStoreDB.collection("hub").document("hubIndex").update(numObj); // 최신번호 갱신

                MainFragment.cropsHub_DB.document(name).set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, name + "등록에 성공했어요!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
