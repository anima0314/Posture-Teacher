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
    private ArrayList<Stats2Model> arrayList;
    private Stats2Adapter stats2Adapter;
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
        Measure2RoundsDAO measure2RoundsDAO = db.getMeasure2RoundsDao();

        Measure2RoundsEntity mrentity;
        List<Measure2RoundsEntity> mrarray= measure2RoundsDAO.getAllData();   //측정데이터 모든 값 가져오기
        Log.d("db","측정데이터 가져오기");
        int mralen = mrarray.size(); //mrarray의 길이 :반복문에서 시간 지연을 줄이기 위해 넣음
        for(int i=0; i < mralen; i++){
            Stats2Model statsModel = new Stats2Model();   //입력 개체
            mrentity = mrarray.get(i);
            //측정ID
            statsModel.setId(mrentity.getMeasure2RoundID()); // ex)3
            //한 사이클 측정 시간
            statsModel.setCycletime(mrentity.getMeasure2RoundPlankTimer());
            //목표대비 달성율
            statsModel.setPercent(mrentity.getMeasure2RoundCurrentCount()+"회 / "+mrentity.getMeasure2RoundTargetCount()+"회");//퍼센트가 나오길
            //측정횟수
            statsModel.setLaps(""+mrentity.getMeasure2RoundCurrentCount());

            arrayList.add(statsModel);

        }
        stats2Adapter = new Stats2Adapter(arrayList);
        recyclerView.setAdapter(stats2Adapter);

        Button button = findViewById(R.id.change_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
                startActivity(intent);
                finish();
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
        stats2Adapter.setOnItemClickListener((v, position) -> {
            int pos = arrayList.size()-1;
            Intent intent = new Intent(this,Stats2DetailActivity.class);
            intent.putExtra("id",arrayList.get(pos-position).getId());//데이터가 역순으로 들어가있어서 역순으로 id를 찾아야함.
            intent.putExtra("cycletime",arrayList.get(pos-position).getCycletime());
            intent.putExtra("percent",arrayList.get(pos-position).getPercent());
            intent.putExtra("laps",Integer.parseInt(arrayList.get(pos-position).getLaps()));
            startActivity(intent);
            finish();
        });
    }
}
