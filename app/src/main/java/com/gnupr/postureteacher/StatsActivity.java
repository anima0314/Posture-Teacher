package com.gnupr.postureteacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {
    private ArrayList<StatsModel> arrayList;
    private StatsAdapter statsAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        recyclerView = findViewById(R.id.rv_stats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();

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
