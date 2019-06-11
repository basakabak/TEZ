package com.basak.points.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.basak.points.Common.Common;
import com.basak.points.MainActivity;
import com.basak.points.R;
import com.basak.points.Template;

import java.util.List;

public class GridViewSuggestAdapter extends BaseAdapter {

    private List<String> suggestSource;
    private Context context;
    private Template mainActivity;
    Animation animation;
    Button im;



    public GridViewSuggestAdapter(List<String> suggestSource, Context context, Template mainActivity) {
        this.suggestSource = suggestSource;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return suggestSource.size();
    }

    @Override
    public Object getItem(int i) {
        return suggestSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        Button button = null;
        if (view == null){
            if (suggestSource.get(position).equals("null")){

            }
            else{
                button = mainActivity.findViewById(R.id.btnCap);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Ansqer createden önce",String.valueOf(suggestSource.get(position)));

                        //If correct answer contsins character user selected
                        if (String.valueOf(mainActivity.answer).contains(suggestSource.get(position))){
                            char compare = suggestSource.get(position).charAt(0);
                            for(int i =0;i<mainActivity.answer.length;i++){

                                if (compare == mainActivity.answer[i]) {
                                    Common.user_submit_answer[i] = compare;
                                    im = mainActivity.findViewById(R.id.myPuan);
                                    im.setLayoutParams(new LinearLayout.LayoutParams(110,110));
                                    im.setVisibility(View.VISIBLE);
                                    im.setBackgroundColor(Color.DKGRAY);
                                    im.setBackgroundResource(R.drawable.round_button);
                                    im.setTextColor(Color.WHITE);
                                    im.setText(String.valueOf(Common.user_submit_answer[i]));
                                    //im.setText(Common.user_submit_answer[i]);
                                    animation = AnimationUtils.loadAnimation(mainActivity,R.anim.frombottom);

                                    im.startAnimation(animation);

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {

                                            ViewGroup layout = (ViewGroup) im.getParent();
                                            im.setVisibility(View.INVISIBLE);
                                        }
                                    }, 150);   //5 seconds
                                }
                            }



                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    GridViewAnswerAdapter answerAdapter = new GridViewAnswerAdapter(Common.user_submit_answer,context);
                                    Log.d("Ansqer set önce",String.valueOf(mainActivity.answer));
                                    mainActivity.gridViewAnswer.setAdapter(answerAdapter);
                                    answerAdapter.notifyDataSetChanged();
                                    Log.d("Ansqer crsonra changed",String.valueOf(mainActivity.answer));
                                    mainActivity.suggestSource.set(position,"null");

                                    mainActivity.suggestAdapter = new GridViewSuggestAdapter(mainActivity.suggestSource,context,mainActivity);
                                    mainActivity.gridViewSuggest.setAdapter(mainActivity.suggestAdapter);
                                    mainActivity.suggestAdapter.notifyDataSetChanged();

                                    }},1000);


                        }
                        else {

                            mainActivity.suggestSource.set(position,"null");
                            mainActivity.suggestAdapter = new GridViewSuggestAdapter(mainActivity.suggestSource,context,mainActivity);
                            mainActivity.gridViewSuggest.setAdapter(mainActivity.suggestAdapter);
                            mainActivity.suggestAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }
        }
        else
            button = (Button)view;

        return button;
    }
}
