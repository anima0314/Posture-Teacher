package com.gnupr.postureteacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button imageButton = findViewById(R.id.start_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
                startActivity(intent);
            }
        });
        //바텀네비게이션
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home_fragment:
                        break;
                    case R.id.stats_fragment:
                        myStartActivity(StatsActivity.class);
                        break;
                    case R.id.setting_fragment:
                        myStartActivity(SettingActivity.class);
                        break;
                }
                return true;
            }
        });
    }
    //화면전환 메소드
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
        finish();
    }
}
