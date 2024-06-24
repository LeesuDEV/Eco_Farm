package com.example.proto;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ChartFragment extends Fragment {

    DocumentReference fireStore_MyDB;

    ArrayList<Entry> temp_List;
    ArrayList<Entry> hum_List; // 차트에 그리기전 Entry 타입(float,float)로 데이터를 받아오는 어레이리스트

    String currentDate; //오늘 날짜 문자변수 - 날짜 컬럼구분에 사용할 것

    int year; //년도
    int month; //월
    int day; //일

    long[] tempList;
    long[] humList; //정렬한 날짜만큼의 평균 온습도 데이터
    long[] tempOfDay;
    long[] humOfDay; //온습도를 24시간 분량 받아올 평균내기용 그릇배열
    float[] dateList_s; //정렬한 날짜리스트

    int num = 0; //dateList(추출한 일수만큼 반복)

    LineChart tempLineChart;
    LineChart humLineChart; //온습도 라인차트(그래프를 그릴 외부 라이브러리)

    View view;
    List<LocalDate> dateList = new ArrayList<>(); //날짜 리스트를 받을 LocalDate형식의 어레이리스트

    public ChartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.chart, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);

        this.view = view;

        fireStore_MyDB = ((MainFragment) getActivity()).fireStore_MyDB;  // DocumentReference 형식의 참조데이터
        //loadDateData();

        Calendar calendar = Calendar.getInstance(); //현재 날짜+시간을 받아옴

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate date = LocalDate.of(year, month, day); // 현재 날짜를 담은 LocalDate 객체

        LocalDate date_test = LocalDate.of(2024, 05, 15); //테스트날짜 5월15일
        int range = 7;

        loadDateList_Data(date_test, range);

        //loadDateData(); 일일데이터 차트생성
    }

    private void loadDateList_Data(LocalDate date, int index) {

        num = 0; // 반복수 초기화

        tempList = new long[index];
        humList = new long[index]; //온습도 평균데이터를 담을 배열 (정렬한 날짜만큼 배열생성)
        dateList_s = new float[index];

        for (int i = 0; i < index; i++) {
            dateList.add(date.minusDays(i));
        }//오늘부터 -i일 까지 날짜리스트를 생성

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //날짜를 String에 담을 형식을 지정
        //AtomicInteger counter = new AtomicInteger(dateList.size());  // 남은 작업 수를 추적

        loadDataRecursively(dateList, formatter, 0);
    }


    public void loadDataRecursively(List<LocalDate> dateList, DateTimeFormatter formatter, int currentIndex) {
        if (currentIndex >= dateList.size()) { // 평균 온습도 추출이 다 끝났다면, 라인차트를 그림
            drawRangeChart();
            return;
        }

        LocalDate date = dateList.get(currentIndex);
        String extractDate = formatter.format(date);
        Log.d("extractDate", extractDate);

        fireStore_MyDB.collection(extractDate).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("extractDate_DB", String.valueOf(extractDate));
                tempOfDay = new long[24];
                humOfDay = new long[24]; //24시간 온습도를 받아서 평균을 낼 그릇배열

                int k = 0;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {  // 24시간 온습도 데이터를 배열에 저장
                    tempOfDay[k] = doc.getLong("degree").longValue();
                    humOfDay[k] = doc.getLong("hum").longValue();
                    k++;
                }

                long tempAvg = (long) Arrays.stream(tempOfDay).average().getAsDouble();
                long humAvg = (long) Arrays.stream(humOfDay).average().getAsDouble(); // 평균 온습도를 평균을 구해주는 OptionalDouble 함수

                synchronized (this) {
                    tempList[num] = tempAvg;
                    humList[num] = humAvg;

                    //날짜형태의 float 데이터 만들기
                    int day = date.getDayOfMonth();
                    float date_format = Float.parseFloat("" + day);
                    Log.d("format_date", String.valueOf(date_format));
                    dateList_s[num] = date_format; // 날짜리스트 배열에 포맷날짜 담기

                    num++;
                }

                //다음 날짜에 대해 재귀호출
                loadDataRecursively(dateList, formatter, currentIndex + 1);
            }
        });
    }

    private void drawRangeChart() {
        temp_List = new ArrayList<>();
        hum_List = new ArrayList<>(); // Entry타입 어레이리스트 초기화

        for (int i = 0; i < dateList_s.length; i++) {
            temp_List.add(new Entry(dateList_s[i], tempList[i]));
            hum_List.add(new Entry(dateList_s[i], humList[i])); // 온습도 평균데이터와 날짜를 어레이리스트에 담기
            Log.d("temp_List", String.valueOf(temp_List.get(i)));
            Log.d("hum_List", String.valueOf(hum_List.get(i)));
        }

        tempLineChart = view.findViewById(R.id.DegreeLineChart);
        humLineChart = view.findViewById(R.id.HumLineChart); // 라인차트 id값 참조

        createChart(temp_List, "주간온도", "#FF8300", tempLineChart);
        createChart(hum_List, "주간습도", "#00AEFF", humLineChart);
    } // 범위 차트를 그려주는 메소드

    public void createChart(ArrayList<Entry> list, String label, String color, LineChart chart) {
        Log.d("size_final", String.valueOf(list.size()));
        LineDataSet DataSet = new LineDataSet(list, label);
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

    public void loadDateData() {  // 1일치 데이터셋 가져오기 (일일데이터)
        temp_List = new ArrayList<>();
        hum_List = new ArrayList<>(); // Entry타입 어레이리스트 초기화

        fireStore_MyDB.collection("2024-04-07").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String timestamp = doc.getId();
                            int hour = Integer.parseInt(timestamp.split(":")[0]);
                            int degree = doc.getLong("degree").intValue();
                            int hum = doc.getLong("hum").intValue();

                            Log.d("hour", String.valueOf(hour));
                            Log.d("degree", String.valueOf(degree));
                            Log.d("hum", String.valueOf(hum));

                            temp_List.add(new Entry(hour, degree));
                            hum_List.add(new Entry(hour, hum));
                        }

                        createChart(temp_List, "degree", "#FF8300", tempLineChart);
                        createChart(hum_List, "hum", "#00AEFF", humLineChart);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
