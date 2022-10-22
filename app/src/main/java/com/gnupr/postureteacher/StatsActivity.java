package com.gnupr.postureteacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.gnupr.postureteacher.Databases.DaoClass.MeasureDatasDAO;
import com.gnupr.postureteacher.Databases.DaoClass.MeasureRoundsDAO;
import com.gnupr.postureteacher.Databases.EntityClass.MeasureDatasEntity;
import com.gnupr.postureteacher.Databases.EntityClass.MeasureRoundsEntity;
import com.gnupr.postureteacher.Databases.MeasureRoomDatabase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    private ArrayList<StatsModel> arrayList;
    private StatsAdapter statsAdapter;
    private RecyclerView recyclerView;
    private MeasureRoomDatabase db = Room.databaseBuilder(getApplicationContext(),MeasureRoomDatabase.class,"measure_database").build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        recyclerView = findViewById(R.id.rv_stats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();

        //리사이클러뷰 데이터 입력
        MeasureDatasDAO measureDatasDAO = db.getMeasureDatasDao();
        MeasureRoundsDAO measureRoundsDAO = db.getMeasureRoundsDao();
        MeasureRoundsEntity mrentity;
        MeasureDatasEntity mdentity;
        List<MeasureRoundsEntity> mrarray= measureRoundsDAO.getAllData();   //측정데이터 모든 값 가져오기
        int mralen = mrarray.size(); //mrarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음
        for(int i=0; i < mralen; i++){
            StatsModel statsModel = new StatsModel();   //입력 개체
            mrentity = mrarray.get(i);
            //측정ID
            statsModel.setId(mrentity.getMeasureRoundID()); // ex)3
            //총측정시간
            Duration diff = Duration.between( mrentity.getMeasureRoundStartTime(), mrentity.getMeasureRoundEndTime());//총 측정시간 계산
            statsModel.setTime( diff.toMinutes() + ":" + diff.getSeconds() % 60 ); //분은 그대로 초는 60으로 나눈 나머지 "분:초" 꼴로 ex) 50:55
            //올바른 자세 비율
            LocalDateTime mrstrart = mrentity.getMeasureRoundStartTime();    //측정 회차 시작시간
            List<MeasureDatasEntity> mdarray = measureDatasDAO.getTimeData(mrstrart);   //시작시간 기준으로 상세데이터 가져오기
            int totalsec = 0;
            int mdalen = mdarray.size(); //mdarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음
            for(int k = 0; k<mdalen; k++){
                mdentity = mdarray.get(k);
                Duration diffmde = Duration.between(mdentity.getMeasureDataStartTime(), mdentity.getMeasureDataEndTime() ); //불안정한 자세 시작과 끝 시간
                totalsec += diffmde.getSeconds();
            }
            float secpercent = ( (float)totalsec/(float)diff.getSeconds() )*100;// (계산 시간 / 총 측정시간)
            statsModel.setPercent( secpercent +"%"); //ex 50%
            //처음자세가 불안정해진 시간
            Duration diffunstart = Duration.between(mrstrart,mdarray.get(0).getMeasureDataStartTime()); // 전체 측정 시작 시간에서부터 처음 자세가 불안정해진 시간까지
            statsModel.setUnstable(diffunstart.toMinutes() + ":" + diffunstart.getSeconds() / 60 ); //분은 그대로 초는 60으로 나눈 나머지 "분:초" 꼴로 ex) 12:55
            arrayList.add(statsModel);
        }
        StatsModel stats = new StatsModel(1,"12:00","89%","6:00");
        arrayList.add(stats);
        stats = new StatsModel(2,"12:00","89%","6:00");
        arrayList.add(stats);
        stats = new StatsModel(3,"12:00","89%","6:00");
        arrayList.add(stats);
        Log.d("Stats","add");
        statsAdapter = new StatsAdapter(arrayList);
        recyclerView.setAdapter(statsAdapter);
        Log.d("Stats","리사이클러뷰?");

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }
}
