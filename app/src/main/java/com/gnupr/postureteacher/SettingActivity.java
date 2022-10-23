package com.gnupr.postureteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    Button probt;
    Button libt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        probt = findViewById(R.id.bt_programmer);
        libt = findViewById(R.id.bt_license);

        probt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProgrammerActivity.class);
                startActivity(intent);
            }
        });

        libt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LicenseActivity.class);
                startActivity(intent);
            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);	//intent 에 명시된 액티비티로 이동
        finish();
    }
    //화면전환 메소드
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
        finish();
    }
}