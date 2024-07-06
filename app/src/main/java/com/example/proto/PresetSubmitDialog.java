package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PresetSubmitDialog extends Dialog {

    Context context;
    TextView PresetSubmitBtn,CancelBtn;
    public PresetSubmitDialog(Context context){
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_submit);

        PresetSubmitBtn = findViewById(R.id.PresetSubmitBtn);
        CancelBtn = findViewById(R.id.CancelBtn);

        PresetSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreset(); // 프리셋 적용 메소드
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    String week1,week2,week3,week4;
    String comment;
    String name;

    private void setPreset(){
        String selectName = PresetHubDialog.selectedItem;
        MainFragment.cropsHub_DB.document(selectName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                week1 = documentSnapshot.get("1week").toString();
                week2 = documentSnapshot.get("2week").toString();
                week3 = documentSnapshot.get("3week").toString();
                week4 = documentSnapshot.get("4week").toString();

                comment = documentSnapshot.get("comment").toString();
                name = documentSnapshot.getId();

                Map<String, Object>obj = new HashMap<>();
                obj.put("1week",week1);
                obj.put("2week",week2);
                obj.put("3week",week3);
                obj.put("4week",week4);
                obj.put("comment",comment);
                obj.put("name",name);

                MainFragment.fireStore_MyDB.collection("preset").document("preset").update(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"프리셋 적용에 성공했어요!",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });
    }
}
