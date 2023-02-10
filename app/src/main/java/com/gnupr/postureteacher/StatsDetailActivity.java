package com.gnupr.postureteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gnupr.postureteacher.Databases.DaoClass.MeasureDatasDAO;
import com.gnupr.postureteacher.Databases.DaoClass.MeasureRoundsDAO;
import com.gnupr.postureteacher.Databases.EntityClass.MeasureDatasEntity;
import com.gnupr.postureteacher.Databases.EntityClass.MeasureRoundsEntity;
import com.gnupr.postureteacher.Databases.MeasureRoomDatabase;
import com.gnupr.postureteacher.databinding.ActivityStatsDetailBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatsDetailActivity extends AppCompatActivity {
    private String id_str;
    private ArrayList<StatsModel> arrayList;
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
        Log.d("rcV","측정 id : " + id_str+ "번");
        //리사이클러뷰 데이터 입력
        MeasureDatasDAO measureDatasDAO = db.getMeasureDatasDao();
        MeasureRoundsDAO measureRoundsDAO = db.getMeasureRoundsDao();


        int id = Integer.parseInt(id_str);
        //id에 맞는 라운드 데이터 가져오기
        MeasureRoundsEntity mrentity= measureRoundsDAO.getRoundData(id);

        LocalDateTime mrstrart = mrentity.getMeasureRoundStartTime();    //측정 회차 시작시간
        //어긋난 리스트가져옴
        List<MeasureDatasEntity> mdarray = measureDatasDAO.getTimeData(mrstrart);
        //어긋난시간대별로 리스트 만들어서 리스트뷰 뿌려주기

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }
}