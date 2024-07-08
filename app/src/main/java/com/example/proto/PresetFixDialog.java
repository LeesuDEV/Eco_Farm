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

    EditText Preset1TermScopeTV, Preset2TermScopeTV, Preset3TermScopeTV, Preset4TermScopeTV;

    EditText Preset1TermTempTV, Preset1TermHumTV, Preset1TermBrightnessTV;
    EditText Preset2TermTempTV, Preset2TermHumTV, Preset2TermBrightnessTV;
    EditText Preset3TermTempTV, Preset3TermHumTV, Preset3TermBrightnessTV;
    EditText Preset4TermTempTV, Preset4TermHumTV, Preset4TermBrightnessTV;
    TextView term1TV, term2TV, term3TV, term4TV;
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

        Preset1TermScopeTV = findViewById(R.id.Preset1TermScopeTV);
        Preset2TermScopeTV = findViewById(R.id.Preset2TermScopeTV);
        Preset3TermScopeTV = findViewById(R.id.Preset3TermScopeTV);
        Preset4TermScopeTV = findViewById(R.id.Preset4TermScopeTV);

        Preset1TermTempTV = findViewById(R.id.Preset1TermTempTV);
        Preset1TermHumTV = findViewById(R.id.Preset1TermHumTV);
        Preset1TermBrightnessTV = findViewById(R.id.Preset1TermBrightnessTV);
        Preset2TermTempTV = findViewById(R.id.Preset2TermTempTV);
        Preset2TermHumTV = findViewById(R.id.Preset2TermHumTV);
        Preset2TermBrightnessTV = findViewById(R.id.Preset2TermBrightnessTV);
        Preset3TermTempTV = findViewById(R.id.Preset3TermTempTV);
        Preset3TermHumTV = findViewById(R.id.Preset3TermHumTV);
        Preset3TermBrightnessTV = findViewById(R.id.Preset3TermBrightnessTV);
        Preset4TermTempTV = findViewById(R.id.Preset4TermTempTV);
        Preset4TermHumTV = findViewById(R.id.Preset4TermHumTV);
        Preset4TermBrightnessTV = findViewById(R.id.Preset4TermBrightnessTV);

        term1TV = findViewById(R.id.term1TV);
        term2TV = findViewById(R.id.term2TV);
        term3TV = findViewById(R.id.term3TV);
        term4TV = findViewById(R.id.term4TV);

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

                String[] term1 = value.get("term1").toString().split(";");
                String[] term2 = value.get("term2").toString().split(";");
                String[] term3 = value.get("term3").toString().split(";");
                String[] term4 = value.get("term4").toString().split(";");

                // 모든 주 프리셋 불러옴
                Preset1TermScopeTV.setText(term1[0]);
                Preset1TermTempTV.setText(term1[1]);
                Preset1TermHumTV.setText(term1[2]);
                Preset1TermBrightnessTV.setText(term1[3]);

                Preset2TermScopeTV.setText(term2[0]);
                Preset2TermTempTV.setText(term2[1]);
                Preset2TermHumTV.setText(term2[2]);
                Preset2TermBrightnessTV.setText(term2[3]);

                Preset3TermScopeTV.setText(term3[0]);
                Preset3TermTempTV.setText(term3[1]);
                Preset3TermHumTV.setText(term3[2]);
                Preset3TermBrightnessTV.setText(term3[3]);

                Preset4TermScopeTV.setText(term4[0]);
                Preset4TermTempTV.setText(term4[1]);
                Preset4TermHumTV.setText(term4[2]);
                Preset4TermBrightnessTV.setText(term4[3]);

                //해당 주 초록색강조함
                switch (MainFragment.term) {
                    case "term1":
                        term1TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1TermScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1TermTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1TermHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset1TermBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                    case "term2":
                        term2TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2TermScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2TermTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2TermHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset2TermBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                    case "term3":
                        term3TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3TermScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3TermTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3TermHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset3TermBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                    case "term4":
                        term4TV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4TermScopeTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4TermTempTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4TermHumTV.setTextColor(Color.parseColor("#78FF6E"));
                        Preset4TermBrightnessTV.setTextColor(Color.parseColor("#78FF6E"));
                        break;
                }
            }
        });
    }

    private void applyEditPreset() {
        String name = PresetNameTV.getText().toString();
        String comment = PresetCommentTV.getText().toString();

        String term1_scope = Preset1TermScopeTV.getText().toString();
        String term1_temp = Preset1TermTempTV.getText().toString();
        String term1_hum = Preset1TermHumTV.getText().toString();
        String term1_brightness = Preset1TermBrightnessTV.getText().toString();

        String term2_scope = Preset2TermScopeTV.getText().toString();
        String term2_temp = Preset2TermTempTV.getText().toString();
        String term2_hum = Preset2TermHumTV.getText().toString();
        String term2_brightness = Preset2TermBrightnessTV.getText().toString();

        String term3_scope = Preset3TermScopeTV.getText().toString();
        String term3_temp = Preset3TermTempTV.getText().toString();
        String term3_hum = Preset3TermHumTV.getText().toString();
        String term3_brightness = Preset3TermBrightnessTV.getText().toString();

        String term4_scope = Preset4TermScopeTV.getText().toString();
        String term4_temp = Preset4TermTempTV.getText().toString();
        String term4_hum = Preset4TermHumTV.getText().toString();
        String term4_brightness = Preset4TermBrightnessTV.getText().toString();

        String term1 = term1_scope + ";" + term1_temp + ";" + term1_hum + ";" + term1_brightness;
        String term2 = term2_scope + ";" + term2_temp + ";" + term2_hum + ";" + term2_brightness;
        String term3 = term3_scope + ";" + term3_temp + ";" + term3_hum + ";" + term3_brightness;
        String term4 = term4_scope + ";" + term4_temp + ";" + term4_hum + ";" + term4_brightness;

        //텍스트 중 하나라도 비어있다면.
        if (name.isEmpty() || comment.isEmpty() || term1_hum.isEmpty() || term1_brightness.isEmpty() || term1_temp.isEmpty() || term2_brightness.isEmpty() || term2_hum.isEmpty() || term2_temp.isEmpty() || term3_temp.isEmpty() || term3_hum.isEmpty() || term3_brightness.isEmpty() || term4_brightness.isEmpty() || term4_hum.isEmpty() || term4_temp.isEmpty()) {
            Toast.makeText(context, "빈칸이 존재해요!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Map에 데이터 준비
        Map<String, Object> obj = new HashMap<>();
        obj.put("name", name);
        obj.put("comment", comment);
        obj.put("term1", term1);
        obj.put("term2", term2);
        obj.put("term3", term3);
        obj.put("term4", term4);

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

        String term1_scope = Preset1TermScopeTV.getText().toString();
        String term1_temp = Preset1TermTempTV.getText().toString();
        String term1_hum = Preset1TermHumTV.getText().toString();
        String term1_brightness = Preset1TermBrightnessTV.getText().toString();

        String term2_scope = Preset2TermScopeTV.getText().toString();
        String term2_temp = Preset2TermTempTV.getText().toString();
        String term2_hum = Preset2TermHumTV.getText().toString();
        String term2_brightness = Preset2TermBrightnessTV.getText().toString();

        String term3_scope = Preset3TermScopeTV.getText().toString();
        String term3_temp = Preset3TermTempTV.getText().toString();
        String term3_hum = Preset3TermHumTV.getText().toString();
        String term3_brightness = Preset3TermBrightnessTV.getText().toString();

        String term4_scope = Preset4TermScopeTV.getText().toString();
        String term4_temp = Preset4TermTempTV.getText().toString();
        String term4_hum = Preset4TermHumTV.getText().toString();
        String term4_brightness = Preset4TermBrightnessTV.getText().toString();

        String term1 = term1_scope + ";" + term1_temp + ";" + term1_hum + ";" + term1_brightness;
        String term2 = term2_scope + ";" + term2_temp + ";" + term2_hum + ";" + term2_brightness;
        String term3 = term3_scope + ";" + term3_temp + ";" + term3_hum + ";" + term3_brightness;
        String term4 = term4_scope + ";" + term4_temp + ";" + term4_hum + ";" + term4_brightness;

        //텍스트 중 하나라도 비어있다면.
        if (name.isEmpty() || comment.isEmpty() || term1_hum.isEmpty() || term1_brightness.isEmpty() || term1_temp.isEmpty() || term2_brightness.isEmpty() || term2_hum.isEmpty() || term2_temp.isEmpty() || term3_temp.isEmpty() || term3_hum.isEmpty() || term3_brightness.isEmpty() || term4_brightness.isEmpty() || term4_hum.isEmpty() || term4_temp.isEmpty()) {
            Toast.makeText(context, "빈칸이 존재해요!", Toast.LENGTH_SHORT).show();
            return;
        }

        MainFragment.fireStoreDB.collection("hub").document("hubIndex").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                num = (int) documentSnapshot.getLong("index").longValue();
                num++;

                //Map에 데이터 준비
                Map<String, Object> obj = new HashMap<>();
                obj.put("uid", uid);
                obj.put("comment", comment);
                obj.put("term1", term1);
                obj.put("term2", term2);
                obj.put("term3", term3);
                obj.put("term4", term4);
                obj.put("hit", 0);
                obj.put("number", num);

                Map<String, Object> numObj = new HashMap<>();
                numObj.put("index", num);

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
