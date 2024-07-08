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

    TextView Preset1TermScopeTV, Preset2TermScopeTV, Preset3TermScopeTV, Preset4TermScopeTV;
    TextView Preset1TermTempTV, Preset1TermHumTV, Preset1TermBrightnessTV;
    TextView Preset2TermTempTV, Preset2TermHumTV, Preset2TermBrightnessTV;
    TextView Preset3TermTempTV, Preset3TermHumTV, Preset3TermBrightnessTV;
    TextView Preset4TermTempTV, Preset4TermHumTV, Preset4TermBrightnessTV;
    TextView term1TV, term2TV, term3TV, term4TV;

    public PresetNowDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_now);

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

        loadMyPreset();
    }

    public void loadMyPreset() {
        DocumentSnapshot value = MainFragment.presetSnapshot;

        if (value == null) {
            Log.d("empty", "empty");
            return;
        }

        String[] term1 = value.get("term1").toString().split(";");
        String[] term2 = value.get("term2").toString().split(";");
        String[] term3 = value.get("term3").toString().split(";");
        String[] term4 = value.get("term4").toString().split(";");

        // 모든 주 프리셋 불러옴
        Preset1TermScopeTV.setText("~" + term1[0] + "주");
        Preset1TermTempTV.setText(term1[1] + "˚C");
        Preset1TermHumTV.setText(term1[2] + "%");
        Preset1TermBrightnessTV.setText(term1[3] + "%");

        Preset2TermScopeTV.setText("~" + term2[0] + "주");
        Preset2TermTempTV.setText(term2[1] + "˚C");
        Preset2TermHumTV.setText(term2[2] + "%");
        Preset2TermBrightnessTV.setText(term2[3] + "%");

        Preset3TermScopeTV.setText("~" + term3[0] + "주");
        Preset3TermTempTV.setText(term3[1] + "˚C");
        Preset3TermHumTV.setText(term3[2] + "%");
        Preset3TermBrightnessTV.setText(term3[3] + "%");

        Preset4TermScopeTV.setText("~" + term4[0] + "주");
        Preset4TermTempTV.setText(term4[1] + "˚C");
        Preset4TermHumTV.setText(term4[2] + "%");
        Preset4TermBrightnessTV.setText(term4[3] + "%");

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
}
