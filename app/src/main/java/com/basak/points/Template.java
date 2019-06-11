package com.basak.points;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basak.points.Adapter.GridViewAnswerAdapter;
import com.basak.points.Adapter.GridViewSuggestAdapter;
import com.basak.points.Common.Common;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Template extends AppCompatActivity {

    TextToSpeech ts,ts2;
    TextureView textureView;
    Button btnCap;
    ImageView im,im2,im3,im4,im5;
    TextView tx;
    Button myb,red;
    Animation animation;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private File file,file2;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

            cameraDevice.close();
            cameraDevice = null;
        }
    };

    public List<String> suggestSource = new ArrayList<>();

    public GridViewAnswerAdapter answerAdapter;
    public GridViewSuggestAdapter suggestAdapter;

    public Button btnSubmit;
    public GridView gridViewAnswer,gridViewSuggest;

    public ImageView imgViewQuestion;
    LinearLayout currentLayout;
    int[] image_list = {R.drawable.keci};
    //int[] image_list = {R.drawable.buzagi,R.drawable.keci,R.drawable.kedi,R.drawable.zurafa};

    public char[] answer;
    String correct_answer;
    HashMap<Integer, String> names;

    private Template mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //azzgetSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_template);

        textureView = findViewById(R.id.textureView);
        btnCap = findViewById(R.id.btnCap);
        red = findViewById(R.id.redGamer);
        /*
        im = findViewById(R.id.immm);
        im2 = findViewById(R.id.imm2);
        im3 = findViewById(R.id.imm3);
        im4 = findViewById(R.id.imm4);
        im5 = findViewById(R.id.imm5);*/
        //tx = findViewById(R.id.txttt);

        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        myb = findViewById(R.id.myPuan);

        names = new HashMap<>();
        names.put(R.drawable.zurafa,"zürafa");
        names.put(R.drawable.keci,"keçi");
        //names.put(R.drawable.sincap,"sincap");
        //names.put(R.drawable.kedi,"kedi");
        //names.put(R.drawable.buzagi,"buzağı");


       /* btnCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });*/

       /* ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                takePicture();
            }
        }, 0, 2, TimeUnit.MINUTES);*/



        Timer timer = new Timer();
        timer.schedule(new startCamera(), 0, 15000);
        initView();
    }

    class startCamera extends TimerTask {
        public void run() {
            takePicture();
        }
    }

    public void initView(){
        gridViewAnswer = findViewById(R.id.gridViewAnswer);
        //gridViewSuggest = findViewById(R.id.gridViewSuggest);

        //imgViewQuestion = findViewById(R.id.imgLogo);

        setupList();



       /* btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "";
                for (int i = 0; i< Common.user_submit_answer.length; i++){
                    result += String.valueOf(Common.user_submit_answer[i]);
                }
                if (result.equals(correct_answer)) {
                    Toast.makeText(getApplicationContext(), "Finish! This is " + result, Toast.LENGTH_SHORT).show();


                    Common.count = 0;
                    Common.user_submit_answer = new char[correct_answer.length()];

                    GridViewAnswerAdapter answerAdapter = new GridViewAnswerAdapter(setupNullList(), getApplicationContext());
                    gridViewAnswer.setAdapter(answerAdapter);
                    answerAdapter.notifyDataSetChanged();

                    GridViewSuggestAdapter suggestAdapter = new GridViewSuggestAdapter(suggestSource, getApplicationContext(), Template.this);
                    gridViewSuggest.setAdapter(suggestAdapter);
                    suggestAdapter.notifyDataSetChanged();

                    setupList();

                }
                else{
                    Toast.makeText(Template.this,"Incorrect!! ",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void setupList(){

        Random random = new Random();
        int imageSelected = image_list[random.nextInt(image_list.length)];
        currentLayout = findViewById(R.id.main_layout);
        currentLayout.setBackgroundResource(imageSelected);

        //imgViewQuestion.setImageResource(imageSelected);

        //correct_answer = getResources().getResourceName(imageSelected);
        correct_answer = names.get(imageSelected);
        //correct_answer = correct_answer.substring(correct_answer.lastIndexOf("/")+1);

        answer = correct_answer.toCharArray();
        Common.user_submit_answer = new char[answer.length];

        suggestSource.clear();
        for (char item:answer){
            suggestSource.add(String.valueOf(item));

        }

        for (int i = answer.length; i<answer.length*2;i++){
            suggestSource.add(Common.alphabet_character[random.nextInt(Common.alphabet_character.length)]);
        }

        Collections.shuffle(suggestSource);

        answerAdapter = new GridViewAnswerAdapter(setupNullList(),this);
        suggestAdapter = new GridViewSuggestAdapter(suggestSource,this,this);

        answerAdapter.notifyDataSetChanged();
        suggestAdapter.notifyDataSetChanged();

        //gridViewSuggest.setAdapter(suggestAdapter);
        gridViewAnswer.setAdapter(answerAdapter);



    }

    private char[] setupNullList() {
        char result[] = new char[answer.length];
        for (int i =0; i<answer.length;i++)
            result[i] = ' ';
        return result;

    }


    private void takePicture() {

        if(cameraDevice == null)
            return;
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes =null;
            if (characteristics != null)
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

            int width = 150;
            int height = 150;
            if(jpegSizes != null && jpegSizes.length >0)
            {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            final ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder  = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            file = new File(Environment.getExternalStorageDirectory()+"/"+"asd"+".jpeg");
            file2 = new File(Environment.getExternalStorageDirectory()+"/"+"myoutputfile"+".jpg");
            //file = new File(Environment.getExternalStorageDirectory()+"/"+UUID.randomUUID().toString()+".jpeg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader ımageReader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        {
                            if(image != null)
                                image.close();
                        }
                    }
                }
                private void save(byte[] bytes)  throws IOException{
                    OutputStream outputStream = null;
                    try{
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                    }finally {
                        if(outputStream != null)
                            outputStream.close();
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(Template.this,"Saved"+file,Toast.LENGTH_SHORT).show();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.toString(), bmOptions);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HashMap<String, Integer> ratio = new HashMap<String, Integer>();

                            // Stuff that updates the UI
                            //Bitmap bitmap = BitmapFactory.decodeFile(file2.toString());
                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.a);


                            String myC = null;

                            if (!Python.isStarted())
                                Python.start(new AndroidPlatform(getApplicationContext()));

                            Python py = Python.getInstance();

                            PyObject pym = py.getModule("ab");
                            PyObject pyf = pym.callAttr("test", file.toString());
                            //im.setImageDrawable(Drawable.createFromPath((String.valueOf(pyf))));
                            //im.setImageDrawable(Drawable.createFromPath(file2.toString()));

                            //im.setImageURI(Uri.parse(Environment.getExternalStorageDirectory()+"/"+"myoutputfile"+".jpg"));

                            //im.setImageAlpha(Integer.parseInt(String.valueOf((pyf))));
                            // im2.setImageDrawable(Drawable.createFromPath(file.toString()));

                            TessBaseAPI tessTwo = new TessBaseAPI();
                            String dataPath = MainApplication.instance.getTessDataParentDirectory();
                            tessTwo.init(dataPath, "tur");
                            //tx.setText(pyf.toString());


                            tessTwo.setImage(new File(Environment.getExternalStorageDirectory() + "/" + "myoutputfile" + ".png"));
                            //tessTwo.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "abcçdefghijklmnopqrsşŞtuvwxyz ");
                            tessTwo.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
                            String recognizedText = tessTwo.getUTF8Text();
                            //tx.setText(recognizedText+" ve "+ tessTwo.meanConfidence()+ "\n");
                            ratio.put(recognizedText, tessTwo.meanConfidence());

                            String filePath = file2.getPath();
                            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + "myoutputfile" + ".png");
                            bitmap = RotateBitmap(bitmap, 90);
                            //im2.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText2 = tessTwo.getUTF8Text();
                            //tx.append(recognizedText2+" ve "+ tessTwo.meanConfidence()+ "\n");

                            ratio.put(recognizedText2, tessTwo.meanConfidence());

                            bitmap = RotateBitmap(bitmap, 90);
                            //im3.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText3 = tessTwo.getUTF8Text();
                            //tx.append(recognizedText3+" ve "+ tessTwo.meanConfidence() + "\n");

                            ratio.put(recognizedText3, tessTwo.meanConfidence());

                            bitmap = RotateBitmap(bitmap, 90);
                            //im4.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText4 = tessTwo.getUTF8Text();
                            //tx.append(recognizedText4+" ve "+ tessTwo.meanConfidence()+ "\n");

                            ratio.put(recognizedText4, tessTwo.meanConfidence());

                            bitmap = RotateBitmap(bitmap, 90);
                            //im5.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText5 = tessTwo.getUTF8Text();
                            //tx.append(recognizedText5+" ve "+ tessTwo.meanConfidence()+ "\n");

                            ratio.put(recognizedText5, tessTwo.meanConfidence());

                            int max = 0;
                            myC = "";
                            for (String i : ratio.keySet()) {
                                if (ratio.get(i) > max) {
                                    max = ratio.get(i);
                                    myC = i;
                                }

                            }
                            //tx.setText("Bulunaaaaaaan  "+ myC+" ve "+ max+ "\n");
                            char c = myC.charAt(0);
                            final char c2 = Character.toLowerCase(c);
                            char[] stringToCharArray = correct_answer.toCharArray();
                            for (int i = 0; i < stringToCharArray.length; i++) {
                                if (stringToCharArray[i] == c2) {
                                    Common.user_submit_answer[i] = c2;
                                    //tx.append("\n "+ stringToCharArray[i]+ "\n"+ Common.user_submit_answer.toString());

                                    myb.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                                    myb.setVisibility(View.VISIBLE);
                                    myb.setBackgroundColor(Color.DKGRAY);
                                    myb.setBackgroundResource(R.drawable.round_button);
                                    myb.setTextColor(Color.WHITE);
                                    myb.setText(String.valueOf(c2));
                                    //im.setText(Common.user_submit_answer[i]);

                                    animation = AnimationUtils.loadAnimation(Template.this, R.anim.frombottom);
                                    myb.setAnimation(animation);

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {

                                            myb.setVisibility(View.INVISIBLE);
                                        }
                                    }, 150);   //5 seconds

                                    GridViewAnswerAdapter answerAdapter = new GridViewAnswerAdapter(Common.user_submit_answer, getApplicationContext());
                                    gridViewAnswer.setAdapter(answerAdapter);
                                    ts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                        @Override
                                        public void onInit(int i) {
                                            if (i != TextToSpeech.ERROR) {
                                                ts.setLanguage(new Locale("tr", "TR"));
                                                speakOut(c2);

                                                String result2 = "";
                                                for (int iv = 0; iv< Common.user_submit_answer.length; iv++){
                                                    result2 += String.valueOf(Common.user_submit_answer[iv]);
                                                    Toast.makeText(getApplicationContext(), result2, Toast.LENGTH_SHORT).show();
                                                }
                                                if (result2.equals(correct_answer)) {
                                                    //Toast.makeText(getApplicationContext(), "Finish! This is " + result2, Toast.LENGTH_SHORT).show();

                                                    Toast.makeText(getApplicationContext(), correct_answer, Toast.LENGTH_SHORT).show();
                                                    speakOut2(correct_answer);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                            //if(stringToCharArray[0] == c2)
                            //tx.append("\n "+ correct_answer + "Girmedi");
                            //tx.append("\n "+ stringToCharArray[0]);



                           /* ResultIterator iterator = tessTwo.getResultIterator();
                            String lastUTF8Text;
                            float lastConfidence;



                            iterator.begin();
                            do {
                                lastUTF8Text = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL);
                                lastConfidence = iterator.confidence(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL);

                                Log.i("string, intConfidence",lastUTF8Text+", "+lastConfidence);
                            } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL));
                            tessTwo.end();
                            createCameraPreview();*/

                        }
                    });


                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

                    try {
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    private void startCountAnimation() {
        String a = (String) red.getText();
        int  b = Integer.parseInt(a) + 2;
        ValueAnimator animator = ValueAnimator.ofInt(Integer.parseInt(a), b); //0 is min number, 600 is max number
        animator.setDuration(1000); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                red.setText(animation.getAnimatedValue().toString());

            }
        });
        animator.start();
    }


    private void speakOut(char c) {

        ts.speak(String.valueOf(c), TextToSpeech.QUEUE_FLUSH, null);
        Animation img = new TranslateAnimation(Animation.ABSOLUTE,Animation.ABSOLUTE,Animation.ABSOLUTE,-70);
        img.setDuration(1000);
        img.setFillAfter(true);

        red.startAnimation(img);


        startCountAnimation();
    }

    private void speakOut2(String c) {

        ts.speak(c, TextToSpeech.QUEUE_FLUSH, null);

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void createCameraPreview() {
        try{
            SurfaceTexture texture =textureView.getSurfaceTexture();
            assert texture !=null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

                    if (cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(Template.this,"Changed",Toast.LENGTH_SHORT).show();

                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {

        if (cameraDevice == null)
            Toast.makeText(Template.this,"Error",Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null ;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;

            }
            manager.openCamera(String.valueOf(1),stateCallBack,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            if (grantResults[0] !=  PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"You cant use camera without permission",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        startBackgroundThread();
        if(textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {

        if(ts!=null){
            ts.stop();
            ts.shutdown();
        }

        super.onPause();
        stopBackgroundThread();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

}
