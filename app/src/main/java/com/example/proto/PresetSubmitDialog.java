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

    String term1,term2,term3,term4,term5;
    String comment;
    String name;

    private void setPreset(){
        String selectName = PresetHubDialog.selectedItem;
        MainFragment.cropsHub_DB.document(selectName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                term1 = documentSnapshot.get("term1").toString();
                term2 = documentSnapshot.get("term2").toString();
                term3 = documentSnapshot.get("term3").toString();
                term4 = documentSnapshot.get("term4").toString();
                term5 = documentSnapshot.get("term5").toString();

                comment = documentSnapshot.get("comment").toString();
                name = documentSnapshot.getId();

                Map<String, Object>obj = new HashMap<>();
                obj.put("term1",term1);
                obj.put("term2",term2);
                obj.put("term3",term3);
                obj.put("term4",term4);
                obj.put("term5",term5);
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
