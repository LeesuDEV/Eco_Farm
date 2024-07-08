package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PresetHubDetailDialog extends Dialog {
    Context context;
    TextView PresetNameTV, PresetCommentTV;

    TextView Preset1TermScopeTV, Preset2TermScopeTV, Preset3TermScopeTV,Preset4TermScopeTV;
    TextView Preset1TermTempTV, Preset1TermHumTV, Preset1TermBrightnessTV;
    TextView Preset2TermTempTV, Preset2TermHumTV, Preset2TermBrightnessTV;
    TextView Preset3TermTempTV, Preset3TermHumTV, Preset3TermBrightnessTV;
    TextView Preset4TermTempTV, Preset4TermHumTV, Preset4TermBrightnessTV;
    TextView PresetHitTV;
    TextView PresetSubmitBtn;
    TextView hitTV;
    int selectNum;

    ListenerRegistration registration;

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

        PresetHitTV = findViewById(R.id.PresetHitTV);
        PresetSubmitBtn = findViewById(R.id.PresetSubmitBtn);

        hitTV = findViewById(R.id.hitTV);

        hitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = PresetNameTV.getText().toString();
                int value = Integer.parseInt(PresetHitTV.getText().toString());

                if (hitTV.getText().toString().equals("♡")) {
                    value++;

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("hit", value);

                    MainFragment.cropsHub_DB.document(name).update(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "이 글을 좋아합니다", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Map<String, Object> hitObj = new HashMap<>();
                    hitObj.put(String.valueOf(selectNum), true);

                    MainFragment.fireStore_MyDB.collection("preset").document("hitPreset").update(hitObj);
                } else {
                    value--;

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("hit", value);

                    MainFragment.cropsHub_DB.document(name).update(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "좋아요를 취소합니다", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Map<String, Object> hitObj = new HashMap<>();
                    hitObj.put(String.valueOf(selectNum), false);

                    MainFragment.fireStore_MyDB.collection("preset").document("hitPreset").update(hitObj);
                }
            }
        });

        PresetSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresetSubmitDialog dialog = new PresetSubmitDialog(context);
                dialog.show();
            }
        });

        loadPresetDetail(); //세부정보 가져오기
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (registration != null){
            registration.remove();
        }
    }

    private void loadPresetDetail() {
        if (PresetHubDialog.selectedItem == null) {
            Toast.makeText(context, "No selected Item", Toast.LENGTH_SHORT).show();
            return;
        }


        registration = MainFragment.cropsHub_DB.document(PresetHubDialog.selectedItem).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("error", "failed");
                    return;
                }
                if (value == null) {
                    Log.d("empty", "empty");
                }
                PresetNameTV.setText(value.getId());
                PresetCommentTV.setText(value.get("comment").toString());

                String[] term1 = value.get("term1").toString().split(";");
                String[] term2 = value.get("term2").toString().split(";");
                String[] term3 = value.get("term3").toString().split(";");
                String[] term4 = value.get("term4").toString().split(";");

                Preset1TermScopeTV.setText("~"+term1[0]+"주");
                Preset1TermTempTV.setText(term1[1]+"˚C");
                Preset1TermHumTV.setText(term1[2]+"%");
                Preset1TermBrightnessTV.setText(term1[3]+"%");

                Preset2TermScopeTV.setText("~"+term2[0]+"주");
                Preset2TermTempTV.setText(term2[1]+"˚C");
                Preset2TermHumTV.setText(term2[2]+"%");
                Preset2TermBrightnessTV.setText(term2[3]+"%");

                Preset3TermScopeTV.setText("~"+term3[0]+"주");
                Preset3TermTempTV.setText(term3[1]+"˚C");
                Preset3TermHumTV.setText(term3[2]+"%");
                Preset3TermBrightnessTV.setText(term3[3]+"%");

                Preset4TermScopeTV.setText("~"+term4[0]+"주");
                Preset4TermTempTV.setText(term4[1]+"˚C");
                Preset4TermHumTV.setText(term4[2]+"%");
                Preset4TermBrightnessTV.setText(term4[3]+"%");

                PresetHitTV.setText("" + value.get("hit").toString());

                //글번호로 좋아요를 판별하기 위한 변수
                selectNum = (int) value.getLong("number").longValue();

                MainFragment.fireStore_MyDB.collection("preset").document("hitPreset").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot mValue, @Nullable FirebaseFirestoreException error) {
                        String num = String.valueOf(selectNum);

                        // 글을 본적이 있나요?
                        if (mValue.contains(num)) {
                            // 이미 내가 좋아요를 누른 글이라면? (true)
                            if (mValue.getBoolean(num)) {
                                hitTV.setText("♥");
                            } else {
                                hitTV.setText("♡");
                            }
                        } else {
                            // 이 글을 좋아요를 누른적이 없는경우
                            hitTV.setText("♡");
                        }
                    }
                });
            }
        });
    }
}
