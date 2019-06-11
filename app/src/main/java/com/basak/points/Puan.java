package com.basak.points;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Puan extends AppCompatActivity {

    ImageView imageView;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puan);

        imageView = findViewById(R.id.img);

        animation = AnimationUtils.loadAnimation(this,R.anim.frombottom);

        imageView.setAnimation(animation);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ViewGroup layout = (ViewGroup) imageView.getParent();
                layout.removeView(imageView);
            }
        }, 1500);   //5 seconds
    }

    private void remove() {
        ViewGroup layout = (ViewGroup) imageView.getParent();
        layout.removeView(imageView);
    }
}
