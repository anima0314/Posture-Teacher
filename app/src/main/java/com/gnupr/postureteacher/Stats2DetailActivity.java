package com.gnupr.postureteacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class Stats2DetailActivity extends AppCompatActivity {
    private String id_str;
    private String cycletime;
    private String percent;
    private String laps;
    private ArrayList<Stats2DetailModel> arrayList;
    private Stats2DetailAdapter stats2DetailAdapter;
    private RecyclerView recyclerView;
    private MeasureRoomDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_detail);
        db = MeasureRoomDatabase.getDatabase(this);
        recyclerView = findViewById(R.id.rv_stats_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();

        //인텐트정보 받기
        Intent intent = getIntent();
        id_str = intent.getStringExtra("id");
        cycletime = intent.getStringExtra("cycletime");
        percent = intent.getStringExtra("percent");
        laps = intent.getStringExtra("laps");
        Log.d("rcV","측정 id : " + id_str+ "번");
        TextView tv1=findViewById(R.id.stats_id);
        tv1.setText("측정 id : " + id_str+ "번");
        TextView tv2=findViewById(R.id.stats_time);
        tv2.setText("한회당 측정시간 : "+cycletime);
        TextView tv3=findViewById(R.id.stats_percent);
        tv3.setText("목표대비 달성율 : "+percent);
        TextView tv4=findViewById(R.id.stats_unstable);
        tv4.setText("실제 측정 횟수 : "+laps);

        //리사이클러뷰 데이터 입력
        Measure2DatasDAO measure2DatasDAO = db.getMeasure2DatasDao();
        Measure2RoundsDAO measure2RoundsDAO = db.getMeasure2RoundsDao();
        Measure2DatasEntity mdentity;

        int id = Integer.parseInt(id_str);
        //id에 맞는 라운드 데이터 가져오기
        Measure2RoundsEntity mrentity= measure2RoundsDAO.getRoundData(id);

        LocalDateTime mrstrart = mrentity.getMeasure2RoundStartTime();    //측정 회차 시작시간

        List<Measure2DatasEntity> mdarray = measure2DatasDAO.getTimeData(mrstrart);   //시작시간 기준으로 상세데이터 가져오기

        int mdalen = mdarray.size(); //mdarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음

        Log.d("rif","if문 들어감");
        //if(mdalen == 0){// 자세가 불안정해진적 없는 경우

        //}
        //else{//자세가 불안정한 경우

            for(int k = 0; k<mdalen; k++){ //불안정해진 사이클 모두 불러옴
                Stats2DetailModel statsDetailModel = new Stats2DetailModel();   //입력 개체
                Log.d("rcV","k = " + k);
                //몇 번째 어긋난 데이터
                statsDetailModel.setId(k+1); // ex)1

                mdentity = mdarray.get(k);
                //어긋나기 시작한 시간
                Duration diffunstart = Duration.between(mrstrart,mdentity.getMeasure2DataStartTime()); // 측정 시작 시간에서부터 자세가 불안정해진 시간까지
                statsDetailModel.setPercent(diffunstart.toMinutes() + "분 " + diffunstart.getSeconds() % 60 +"초");

                //자세가 회복된 시간
                Duration diffunend = Duration.between(mrstrart,mdentity.getMeasure2DataEndTime()); // 측정 시작 시간에서부터 자세가 회복된 시간
                statsDetailModel.setTime(diffunend.toMinutes() + "분 " + diffunend.getSeconds() % 60 +"초");

                arrayList.add(statsDetailModel);
            }


        //}
        Log.d("rcV","뿌려주기");
        //어긋난시간대별로 리스트 만들어서 리스트뷰 뿌려주기
        stats2DetailAdapter = new Stats2DetailAdapter(arrayList);
        recyclerView.setAdapter(stats2DetailAdapter);

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }
}