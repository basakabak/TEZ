package com.basak.points.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;

import com.basak.points.Common.Common;
import com.basak.points.R;

import static com.basak.points.R.drawable.round_button;

public class GridViewAnswerAdapter extends BaseAdapter {

    private char[] answerCharacter;
    private Context context;
    Animation animation;

    public GridViewAnswerAdapter(char[] answerCharacter, Context context) {

        this.answerCharacter = answerCharacter;
        this.context = context;
    }


    @Override
    public int getCount() {
        return answerCharacter.length;
    }

    @Override
    public Object getItem(int i) {
        return answerCharacter[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Button button;
        if (view == null){
            button = new Button(context);
            button.setLayoutParams(new GridView.LayoutParams(100,100));
            button.setPadding(1,1,1,1);
            button.setBackgroundColor(Color.DKGRAY);
            button.setTextColor(Color.YELLOW);
            button.setBackgroundResource(R.drawable.round_button2);
            button.setText(String.valueOf(answerCharacter[i]));
        }
        else
            button = (Button)view;
        return button;
    }
}
