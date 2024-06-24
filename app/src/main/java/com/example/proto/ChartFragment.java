package com.example.proto;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChartFragment extends Fragment {

    DocumentReference fireStore_MyDB;

    public ChartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.chart, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        fireStore_MyDB = ((MainFragment) getActivity()).fireStore_MyDB;

        ArrayList<Entry> degree_List = new ArrayList<>();
        ArrayList<Entry> hum_List = new ArrayList<>();


        fireStore_MyDB.collection("2024-04-07").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String timestamp = doc.getId();
                            int hour = Integer.parseInt(timestamp.split(":")[0]);
                            int degree = doc.getLong("degree").intValue();
                            int hum = doc.getLong("hum").intValue();

                            degree_List.add(new Entry(hour, degree));
                            hum_List.add(new Entry(hour, hum));
                        }

                        LineChart degreeLineChart = view.findViewById(R.id.DegreeLineChart);
                        LineChart humLineChart =  view.findViewById(R.id.HumLineChart);

                        createChart(degree_List,"degree","#FF8300",degreeLineChart);
                        createChart(hum_List,"hum","#00AEFF",humLineChart);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void createChart(ArrayList<Entry> list, String label,String color,LineChart chart){
        LineDataSet DataSet = new LineDataSet(list,label);
        // 색상 설정

        DataSet.setColor(Color.parseColor(color)); // 검정색
        DataSet.setCircleRadius(5f); // 원의 반지름 설정
        DataSet.setLineWidth(3f); // 선의 너비 설정
        DataSet.setCircleColor(Color.parseColor("#673AB7")); // 보라색 (Material Design Primary Color)
        DataSet.setCircleHoleColor(Color.parseColor("#673AB7")); // 보라색 (Material Design Primary Color)
        DataSet.setDrawHighlightIndicators(false); // 하이라이트 표시 여부 설정
        DataSet.setDrawValues(true); // 값 표시 여부 설정
        DataSet.setValueTextColor(Color.parseColor("#000000")); // 검정색
        DataSet.setValueFormatter(new DefaultValueFormatter(0)); // 값의 표시 형식 설정
        DataSet.setValueTextSize(10f); // 값의 텍스트 크기 설정

        chart.getAxisRight().setEnabled(false); // 우측 Y축 사용 여부
        chart.getLegend().setEnabled(false); // 범례 사용 여부
        chart.getDescription().setEnabled(false); // 차트 아래의 설명문 사용 여부
        chart.setDragEnabled(true); // 드래그(이동) 가능 여부, X축만 가능하도록 설정
        chart.setScaleYEnabled(false); // Y축 방향으로의 스케일(확대/축소) 가능 여부
        chart.setScaleXEnabled(false); // X축 방향으로의 스케일(확대/축소) 가능 여부

        XAxis xAxis = chart.getXAxis();

        xAxis.setDrawGridLines(false); // 그리드 라인 그리기 설정
        xAxis.setDrawAxisLine(true); // 축 라인 그리기 설정
        xAxis.setDrawLabels(true); // 라벨 그리기 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X축의 위치를 아래로 설정
        xAxis.setTextColor(getContext().getResources().getColor(R.color.black, null)); // 라벨의 텍스트 색상 설정
        xAxis.setTextSize(10f); // 라벨의 텍스트 크기 설정
        xAxis.setLabelRotationAngle(0f); // 라벨의 회전 각도 설정
        xAxis.setLabelCount(list.size(), true);

        LineData lineData = new LineData();
        lineData.addDataSet(DataSet);

        chart.setData(lineData);
        chart.invalidate();
    }
}
