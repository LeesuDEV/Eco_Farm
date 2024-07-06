package com.example.proto;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Map;

public class PresetDeleteDialog extends Dialog {

    Context context;
    TextView PresetDeleteBtn,CancelBtn;
    private PresetDeleteDialogListener listener;
    public PresetDeleteDialog(Context context,PresetDeleteDialogListener listener){
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
        setContentView(R.layout.preset_delete);

        PresetDeleteBtn = findViewById(R.id.PresetDeleteBtn);
        CancelBtn = findViewById(R.id.CancelBtn);

        PresetDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deleteItem = PresetHubDialog.deleteItem;
                Log.d ("deleteItem",deleteItem);
                MainFragment.cropsHub_DB.document(deleteItem).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dismiss();
                        Toast.makeText(context,"글삭제에 성공했어요!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
