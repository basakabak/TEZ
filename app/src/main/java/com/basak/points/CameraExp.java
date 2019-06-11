package com.basak.points;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//import java.nio.file.Paths;

public class CameraExp extends AppCompatActivity {

    ImageView imageView;
    Button btnCamera;
    TextView textView;
    private static final String ASSET_SOURCE_DIR = "source";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_exp);

        imageView = findViewById(R.id.imageViewC);
        btnCamera = findViewById(R.id.btnCamera);
        textView = (TextView) findViewById(R.id.letter);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });



        Log.d("valueee", "try dan once");
        Python.start(new AndroidPlatform(getApplicationContext()));
        Python py = Python.getInstance();
        PyObject pym =py.getModule("ab");
        //PyObject pyf = pym.callAttr("test");
        Log.d("ayyy hadii",pym.toString());
    }
}
