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
    private int cycletime;
    private String percent;
    private int laps;
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
        cycletime = intent.getIntExtra("cycletime",0);
        percent = intent.getStringExtra("percent");
        laps = intent.getIntExtra("laps",0);
        Log.d("rcV","측정 id : " + id_str+ "번");
        TextView tv1=findViewById(R.id.stats_id);
        tv1.setText("측정 id : " + id_str+ "번");
        TextView tv2=findViewById(R.id.stats_time);
        tv2.setText("한회당 측정시간 : "+"회당 측정시간 : "+cycletime/60+"분"+cycletime%60+"초");
        TextView tv3=findViewById(R.id.stats_percent);
        tv3.setText("목표대비 달성율 : "+percent);
        TextView tv4=findViewById(R.id.stats_unstable);
        tv4.setText("실제 측정 횟수 : "+laps);

        //리사이클러뷰 데이터 입력
        Measure2DatasDAO measure2DatasDAO = db.getMeasure2DatasDao();
        Measure2RoundsDAO measure2RoundsDAO = db.getMeasure2RoundsDao();
        Measure2DatasEntity mdentity;
        Log.d("rcV","측정 id : " + id_str+ "번");
        int id = Integer.parseInt(id_str);

        //id에 맞는 라운드 데이터 가져오기
        Measure2RoundsEntity mrentity= measure2RoundsDAO.getRoundData(id);
        LocalDateTime mrstrart = mrentity.getMeasure2RoundStartTime();    //측정 회차 시작시간
        Log.d("rcV","길이 : " + laps+ "번");

        int len = laps;
        for(int i=0;i<len;i++){
            int sumtime=0;
            List<Measure2DatasEntity> mddarray = measure2DatasDAO.getDetectData(mrstrart,i+1);   //시작시간 기준으로 상세데이터 가져오기
            int mddalen = mddarray.size(); //mdarray 길이 :반복문에서 시간 지연을 줄이기 위해 넣음
            Log.d("rcV",(i+1)+"번째의 길이 "+mddalen);
            Stats2DetailModel statsDetailModel = new Stats2DetailModel();   //입력 개체
            //몇 번째 플랭크
            statsDetailModel.setId(i+1); // ex)1
            //자세가 불안정하지않으면
            if(mddalen == 0){
                statsDetailModel.setTime("완벽한 자세였습니다.");
                statsDetailModel.setPercent("100%");
                arrayList.add(statsDetailModel);
                continue;
            }
            //자세가 올바른 시간
            //올바른 자세 비율
            for(int k = 0; k<mddalen; k++) {
                mdentity = mddarray.get(k);
                Duration dif = Duration.between(mdentity.getMeasure2DataStartTime(), mdentity.getMeasure2DataEndTime());
                sumtime += dif.getSeconds();
            }
            statsDetailModel.setTime(sumtime/60 + "분 " + sumtime%60 + "초");
            statsDetailModel.setPercent( sumtime/(float)(cycletime)*100+"%");
            arrayList.add(statsDetailModel);
        }
        //어긋난시간대별로 리스트 만들어서 리스트뷰 뿌려주기
        stats2DetailAdapter = new Stats2DetailAdapter(arrayList);
        recyclerView.setAdapter(stats2DetailAdapter);

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Stats2Activity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }
}