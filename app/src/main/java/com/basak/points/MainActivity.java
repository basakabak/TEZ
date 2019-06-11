package com.basak.points;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button puan,ornek,gridexp,cameraexp,tesseract,cmr,pyexp,recrd,cm2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);


        puan = findViewById(R.id.puan);
        puan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Puan.class);
                startActivity(intent);
            }
        });
        ornek = findViewById(R.id.ornek);
        ornek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Template.class);
                startActivity(intent);
            }
        });

        gridexp = findViewById(R.id.gridexp);
        gridexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,GridExp.class);
                startActivity(intent);
            }
        });

        cameraexp = findViewById(R.id.CameraExp);
        cameraexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CameraExp.class);
                startActivity(intent);
            }
        });

        tesseract = findViewById(R.id.tesseract);
        tesseract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TesseractExp.class);
                startActivity(intent);
            }
        });

        cmr = findViewById(R.id.cmr);
        cmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CameraM.class);
                startActivity(intent);
            }
        });


        pyexp = findViewById(R.id.pyexp);
        pyexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PyExp.class);
                startActivity(intent);
            }
        });

        recrd = findViewById(R.id.recrd);
        recrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CameraRecorder.class);
                startActivity(intent);
            }
        });

        cm2 = findViewById(R.id.camera2Exp);
        cm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Camera2Exp.class);
                startActivity(intent);
            }
        });
    }
}
