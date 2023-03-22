package com.gnupr.postureteacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    int switchButtonCheck = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimePicker picker = findViewById(R.id.timePicker);
        TextView tvSwitch = findViewById(R.id.SwitchText);
        picker.setIs24HourView(true);
        picker.setHour(1);
        picker.setMinute(0);

        Button imageButton = findViewById(R.id.start_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(picker.getHour() == 0 && picker.getMinute() == 0) { }
                else{
                    Intent intent;
                    if(switchButtonCheck==1) {
                        intent = new Intent(getApplicationContext(), PlankActivity.class);
                    }
                    else {
                        intent = new Intent(getApplicationContext(), MeasureActivity.class);
                    }
                    intent.putExtra("hour", picker.getHour());
                    intent.putExtra("minute", picker.getMinute());
                    startActivity(intent);
                    finish();
                }
            }
        });

        ImageView btset = findViewById(R.id.bt_setting);
        btset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(SettingActivity.class);
            }
        });

        Switch switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    switchButtonCheck = 0;
                    tvSwitch.setText("앉은 자세 측정 모드");
                } else {
                    switchButtonCheck = 1;
                    tvSwitch.setText("플랭크 측정 모드");
                }
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
                    case R.id.description_fragment:
                        myStartActivity(DescriptionActivity.class);
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
