package com.basak.points;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Element;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Locale;


public class TesseractExp extends AppCompatActivity {

    ImageView okumak;
    TextView okumakicin;
    Button btn;
    TextToSpeech ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesseract_exp);

        okumak = findViewById(R.id.okumakFoto);
        okumakicin = findViewById(R.id.okunanfoto);
        btn=findViewById(R.id.btnn);


        final Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.adsiz);

        okumak.setImageBitmap(bitmap);

        ts = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    ts.setLanguage(new Locale("tr", "TR"));
                }
            }
        });


        ts.speak(okumakicin.getText().toString(), TextToSpeech.QUEUE_FLUSH,null);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!textRecognizer.isOperational()){
                    Log.e("ERROR","olmiyyy");
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items =  textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i<items.size();i++){
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getLanguage());
                        stringBuilder.append("\n");
                    }
                    String blocks = "";
                    String lines = "";
                    String words = "";
                    for (int index = 0; index < items.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = items.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                words = words + element.getValue() + ", ";

                            }
                        }
                    }

                    okumakicin.setText(okumakicin.getText()+ lines + "\n");

                                    }
*/
                ts.speak(okumakicin.getText().toString(), TextToSpeech.QUEUE_FLUSH,null);

            }
        });
    }

}
