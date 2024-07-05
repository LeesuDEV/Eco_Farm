package com.example.proto;

import android.app.Dialog;
import android.content.Context;
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

import java.util.Map;

public class PresetHubDialog extends Dialog {

    Context context;
    LinearLayout presetHubLayout;
    Typeface netmarble_B;
    static String selectedItem;
    public PresetHubDialog(Context context){
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_hub);

        presetHubLayout = findViewById(R.id.presetHubLayout);

        netmarble_B = ResourcesCompat.getFont(context,R.font.netmarble_bold);

        loadPresetHub();
    }

    private void loadPresetHub(){
        MainFragment.cropsHub_DB.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots == null) {
                    Toast.makeText(context,"추천값이 없습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                int i = 0;

                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) { //hub 게시긇 수 만큼 로딩
                    i++;
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setPadding(2,2,2,2);
                    layout.setWeightSum(10); // weight 10 설정

                    //레이아웃 파라미터 설정
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            100
                    );
                    layout.setLayoutParams(layoutParams);

                    //텍스트뷰 설정
                    CreateTextView(""+i,"#000000",netmarble_B,1.5f,layout); //넘버
                    CreateTextView(""+snapshots.getId().toString(),"#000000",netmarble_B,5f,layout); //넘버
                    CreateTextView(""+snapshots.get("hit").toString(),"#000000",netmarble_B,1.5f,layout); //넘버
                    if (snapshots.get("uid").toString().equals(MainFragment.uid)) {
                        CreateTextView("삭제","#F85802",netmarble_B,2f,layout); //넘버
                    }

                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedItem = snapshots.getId(); //상세정보를 위한 게시글이름 전달
                            PresetHubDetailDialog presetHubDetailDialog = new PresetHubDetailDialog(getContext());
                            presetHubDetailDialog.show();
                            presetHubDetailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //투명배경
                        }
                    });
                    presetHubLayout.addView(layout);
                }
            }
        });
    }

    public void CreateTextView(String Text,String color,Typeface typeface,Float weight,LinearLayout layout){
        TextView textView = new TextView(context);
        textView.setText(Text);
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor(color));
        textView.setTypeface(netmarble_B);
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
        );
        textView.setLayoutParams(tvLayoutParams);
        layout.addView(textView);
    }
}
