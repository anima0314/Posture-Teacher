package com.gnupr.postureteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ProgrammerActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmer);
        tv = findViewById(R.id.tv_programmer);
        tv.setText("\n팀명 : postureteacher\n\n" +
                "박주철 : valurauta628@gmail.com \n" +
                "류경복 : anima0314@gmail.com");
    }
}