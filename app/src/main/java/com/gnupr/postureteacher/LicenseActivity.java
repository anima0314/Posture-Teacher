package com.gnupr.postureteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class LicenseActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        tv = findViewById(R.id.tv_license);
        tv.setText("Copyright [2022] [PJC & RGB]\n"
                +" Licensed under the Apache License, Version 2.0 (the \"License\");"
                +"you may not use this file except in compliance with the License."
                +"You may obtain a copy of the License at\n"
                +"http://www.apache.org/licenses/LICENSE-2.0\n"
                +"Unless required by applicable law or agreed to in writing, software"
                +"distributed under the License is distributed on an \"AS IS\" BASIS,"
                +"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
                +"See the License for the specific language governing permissions and"
                +"limitations under the License.");
    }
}