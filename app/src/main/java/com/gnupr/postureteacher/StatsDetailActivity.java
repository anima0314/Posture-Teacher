package com.gnupr.postureteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gnupr.postureteacher.Databases.DaoClass.MeasureDatasDAO;
import com.gnupr.postureteacher.Databases.DaoClass.MeasureRoundsDAO;
import com.gnupr.postureteacher.Databases.EntityClass.MeasureDatasEntity;
import com.gnupr.postureteacher.Databases.EntityClass.MeasureRoundsEntity;
import com.gnupr.postureteacher.Databases.MeasureRoomDatabase;
import com.gnupr.postureteacher.databinding.ActivityStatsDetailBinding;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatsDetailActivity extends AppCompatActivity {
    private String id_str;
    private String time;
    private String percent;
    private String unstable;
    private ArrayList<StatsDetailModel> arrayList;
    private StatsDetailAdapter statsDetailAdapter;
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
        time = intent.getStringExtra("time");
        percent = intent.getStringExtra("percent");
        unstable = intent.getStringExtra("unstable");
        Log.d("rcV","측정 id : " + id_str+ "번");

        TextView tv1=findViewById(R.id.stats_id);
        tv1.setText("측정 id : " + id_str);
        TextView tv2=findViewById(R.id.stats_time);
        tv2.setText("총 측정시간 : "+time);
        TextView tv3=findViewById(R.id.stats_percent);
        tv3.setText("올바른 자세 비율 : "+percent);
        TextView tv4=findViewById(R.id.stats_unstable);
        tv4.setText(unstable);
        //리사이클러뷰 데이터 입력
        MeasureDatasDAO measureDatasDAO = db.getMeasureDatasDao();
        MeasureRoundsDAO measureRoundsDAO = db.getMeasureRoundsDao();
        MeasureDatasEntity mdentity;

        int id = Integer.parseInt(id_str);
        //id에 맞는 라운드 데이터 가져오기
        MeasureRoundsEntity mrentity= measureRoundsDAO.getRoundData(id);

        LocalDateTime mrstrart = mrentity.getMeasureRoundStartTime();    //측정 회차 시작시간

        List<MeasureDatasEntity> mdarray = measureDatasDAO.getTimeData(mrstrart);   //시작시간 기준으로 상세데이터 가져오기

        int mdalen = mdarray.size(); //mdarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음

        Log.d("rif","if문 들어감");
        //if(mdalen == 0){// 자세가 불안정해진적 없는 경우

        //}
        //else{//자세가 불안정한 경우

            for(int k = 0; k<mdalen; k++){ //불안정해진 사이클 모두 불러옴
                StatsDetailModel statsDetailModel = new StatsDetailModel();   //입력 개체
                Log.d("rcV","k = " + k);
                //몇 번째 어긋난 라운드
                statsDetailModel.setId(k+1); // ex)1

                mdentity = mdarray.get(k);
                //어긋나기 시작한 시간
                Duration diffunstart = Duration.between(mrstrart,mdentity.getMeasureDataStartTime()); // 측정 시작 시간에서부터 자세가 불안정해진 시간까지
                statsDetailModel.setFirst_time(diffunstart.toMinutes() + "분 " + diffunstart.getSeconds() % 60 +"초");

                //자세가 회복된 시간
                Duration diffunend = Duration.between(mrstrart,mdentity.getMeasureDataEndTime()); // 측정 시작 시간에서부터 자세가 회복된 시간
                statsDetailModel.setEnd_time(diffunend.toMinutes() + "분 " + diffunend.getSeconds() % 60 +"초");

                //자세가 불안정한 총 시간
                Duration diffmde = Duration.between(mdentity.getMeasureDataStartTime(), mdentity.getMeasureDataEndTime() ); //불안정한 자세 시작과 끝 시간
                statsDetailModel.setTotal_time(diffmde.toMinutes() + "분 " + diffmde.getSeconds() % 60 +"초");

                arrayList.add(statsDetailModel);
            }

        //}
        Log.d("rcV","뿌려주기");
        //어긋난시간대별로 리스트 만들어서 리스트뷰 뿌려주기
        statsDetailAdapter = new StatsDetailAdapter(arrayList);
        recyclerView.setAdapter(statsDetailAdapter);

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }
}