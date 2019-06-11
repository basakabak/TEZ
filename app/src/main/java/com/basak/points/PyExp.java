package com.basak.points;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;


public class PyExp extends AppCompatActivity implements SurfaceHolder.Callback {


    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    TextView tx;
    ImageView myIm;
    Image im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(! Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py = Python.getInstance();

        PyObject pym =py.getModule("ab");

       // myIm.setImageResource(R.drawable.c2);
        Drawable myDrawable = getResources().getDrawable(R.drawable.c2);
        Bitmap anImage      = ((BitmapDrawable) myDrawable).getBitmap();

        myIm = findViewById(R.id.im);
        //myIm.setImageResource(R.drawable.c2);


        //PyObject pyf = pym.callAttr("test",myIm);
        setContentView(R.layout.activity_py_exp);
        tx = findViewById(R.id.tx);
        tx.setText(String.valueOf(pym));
        //myIm = findViewById(R.id.im);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);


        //Matrix matrix = (Matrix)(String.valueOf(pyf));
        //myIm.setImageResource(Integer.parseInt(String.valueOf(pyf)));


        /*mSurfaceView = (SurfaceView) findViewById(R.id.surface_view2);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback((SurfaceHolder.Callback) pyf);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);*/


        //Log.d("ayyy hadii", String.valueOf(pyf.to));

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
