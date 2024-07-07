package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PresetRegisterDialog extends Dialog {

    Context context;
    TextView PresetRegisterBtn,CancelBtn;
    private PresetDeleteDialogListener listener;
    public PresetRegisterDialog(Context context, PresetDeleteDialogListener listener){
        super(context);
        this.context = context;
        this.listener = listener;
    }

    public interface PresetDeleteDialogListener {
        void onDialogDismissed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_register);

        PresetRegisterBtn = findViewById(R.id.PresetRegisterBtn);
        CancelBtn = findViewById(R.id.CancelBtn);

        PresetRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registerItem = PresetFixDialog.registerItem;
                MainFragment.cropsHub_DB.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // 글이 없을때
                        if (queryDocumentSnapshots.isEmpty()){
                            PresetFixDialog.existUID = false;
                            dismiss();
                            return;
                        }
                        PresetFixDialog.existUID = false ;

                        for (QueryDocumentSnapshot query : queryDocumentSnapshots){
                            // 모든 글에 대해서 내 uid로 작성된 글이 있는지 탐색
                            if (query.get("uid").toString().equals(MainFragment.uid)){
                                //일치하는 글 있을시 글등록 X
                                PresetFixDialog.existUID = true;
                            }
                        }

                        dismiss();
                    }
                });
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresetFixDialog.isCancel = true;
                dismiss();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null){
                    listener.onDialogDismissed();
                }
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null){
                    listener.onDialogDismissed();
                }
            }
        });
    }
}
