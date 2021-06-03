package com.dualfie.maindirs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dualfie.maindirs.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_master = findViewById(R.id.btn_master);
        btn_master.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent controlIntent = new Intent(getApplicationContext(), ControlActivity.class);
                finish();
                startActivity(controlIntent);
            }
        });

        Button btn_shutter = findViewById(R.id.btn_localcamera);
        btn_shutter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                finish();
                startActivity(cameraIntent);
            }
        });

    }


}
