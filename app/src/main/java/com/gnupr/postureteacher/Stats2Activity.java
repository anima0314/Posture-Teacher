package com.gnupr.postureteacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gnupr.postureteacher.Databases.DaoClass.Measure2DatasDAO;
import com.gnupr.postureteacher.Databases.DaoClass.Measure2RoundsDAO;
import com.gnupr.postureteacher.Databases.EntityClass.Measure2DatasEntity;
import com.gnupr.postureteacher.Databases.EntityClass.Measure2RoundsEntity;
import com.gnupr.postureteacher.Databases.MeasureRoomDatabase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Stats2Activity extends AppCompatActivity {
    private ArrayList<StatsModel> arrayList;
    private StatsAdapter statsAdapter;
    private RecyclerView recyclerView;
    private MeasureRoomDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats2);
        db = MeasureRoomDatabase.getDatabase(this);
        recyclerView = findViewById(R.id.rv_stats2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();

        //리사이클러뷰 데이터 입력
        Measure2DatasDAO measure2DatasDAO = db.getMeasure2DatasDao();
        Measure2RoundsDAO measure2RoundsDAO = db.getMeasure2RoundsDao();

        Measure2RoundsEntity mrentity;
        Measure2DatasEntity mdentity;
        List<Measure2RoundsEntity> mrarray= measure2RoundsDAO.getAllData();   //측정데이터 모든 값 가져오기
        Log.d("db","측정데이터 가져오기");
        int mralen = mrarray.size(); //mrarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음
        for(int i=0; i < mralen; i++){
            StatsModel statsModel = new StatsModel();   //입력 개체
            mrentity = mrarray.get(i);
            //측정ID
            statsModel.setId(mrentity.getMeasure2RoundID()); // ex)3
            //총측정시간
            Duration diff = Duration.between( mrentity.getMeasure2RoundStartTime(), mrentity.getMeasure2RoundEndTime());//총 측정시간 계산
            Log.d("db",diff.toMinutes() + ":" + diff.getSeconds() % 60);    //로그
            statsModel.setTime(diff.toMinutes() + "분 " + diff.getSeconds() % 60 +"초"); //분은 그대로 초는 60으로 나눈 나머지 "분:초" 꼴로 ex) 50:55
            //올바른 자세 비율
            LocalDateTime mrstrart = mrentity.getMeasure2RoundStartTime();    //측정 회차 시작시간
            //Log.d("db","측정시작 시간 : " + mrstrart);
            List<Measure2DatasEntity> mdarray = measure2DatasDAO.getTimeData(mrstrart);   //시작시간 기준으로 상세데이터 가져오기
            int totalsec = 0;
            int mdalen = mdarray.size(); //mdarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음
            if(mdalen == 0){// 자세가 불안정해진적 없는 경우
                statsModel.setPercent("100%"); //ex 50%
                statsModel.setUnstable("완벽한 자세였습니다."); //분은 그대로 초는 60으로 나눈 나머지 "분:초" 꼴로 ex) 12:55
                arrayList.add(statsModel);
                continue;
            }
            for(int k = 0; k<mdalen; k++){
                mdentity = mdarray.get(k);
                Duration diffmde = Duration.between(mdentity.getMeasure2DataStartTime(), mdentity.getMeasure2DataEndTime() ); //불안정한 자세 시작과 끝 시간
                totalsec += diffmde.getSeconds();
            }
            float secpercent = (1 - ( (float)totalsec/(float)diff.getSeconds() ) )   *100;// (계산 시간 / 총 측정시간)
            statsModel.setPercent( String.format("%.2f", secpercent) +"%"); //ex 50%
            //처음자세가 불안정해진 시간
            Duration diffunstart = Duration.between(mrstrart,mdarray.get(0).getMeasure2DataStartTime()); // 전체 측정 시작 시간에서부터 처음 자세가 불안정해진 시간까지
            Log.d("db","처음 자세가 불안정해진시간 : " + mdarray.get(0).getMeasure2DataStartTime());
            statsModel.setUnstable("처음 자세가 불안정해진 시간 : "+diffunstart.toMinutes() + "분 " + diffunstart.getSeconds() % 60 +"초"); //분은 그대로 초는 60으로 나눈 나머지 "분:초" 꼴로 ex) 12:55
            arrayList.add(statsModel);
        }
        statsAdapter = new StatsAdapter(arrayList);
        recyclerView.setAdapter(statsAdapter);

        Button button = findViewById(R.id.change_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
                startActivity(intent);
            }
        });

        itemClick();
    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }

    private void itemClick(){
        statsAdapter.setOnItemClickListener((v, position) -> {
            int pos = arrayList.size()-1;
            Intent intent = new Intent(this,Stats2DetailActivity.class);
            intent.putExtra("id",arrayList.get(pos-position).getId());//데이터가 역순으로 들어가있어서 역순으로 id를 찾아야함.
            intent.putExtra("time",arrayList.get(pos-position).getTime());
            intent.putExtra("percent",arrayList.get(pos-position).getPercent());
            intent.putExtra("unstable",arrayList.get(pos-position).getUnstable());
            startActivity(intent);
        });
    }
}
