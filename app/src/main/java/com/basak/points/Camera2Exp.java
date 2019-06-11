package com.basak.points;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
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
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.lang.Integer.*;


public class Camera2Exp extends AppCompatActivity {

    TextureView textureView;
    Button btnCap;
    ImageView im,im2,im3,im4,im5;
    TextView tx;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_exp);

        textureView = findViewById(R.id.textureView);
        btnCap = findViewById(R.id.btnCap);
        im = findViewById(R.id.immm);
        im2 = findViewById(R.id.imm2);
        im3 = findViewById(R.id.imm3);
        im4 = findViewById(R.id.imm4);
        im5 = findViewById(R.id.imm5);
        tx = findViewById(R.id.txttt);

        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);



        btnCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });


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
                    Toast.makeText(Camera2Exp.this,"Saved"+file,Toast.LENGTH_SHORT).show();
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


                            if(! Python.isStarted())
                                Python.start(new AndroidPlatform(getApplicationContext()));

                            Python py = Python.getInstance();

                            PyObject pym =py.getModule("ab");
                            PyObject pyf = pym.callAttr("test",file.toString());
                            im.setImageDrawable(Drawable.createFromPath((String.valueOf(pyf))));
                            //im.setImageDrawable(Drawable.createFromPath(file2.toString()));

                            //im.setImageURI(Uri.parse(Environment.getExternalStorageDirectory()+"/"+"myoutputfile"+".jpg"));

                            //im.setImageAlpha(Integer.parseInt(String.valueOf((pyf))));
                            im2.setImageDrawable(Drawable.createFromPath(file.toString()));

                            TessBaseAPI tessTwo = new TessBaseAPI();
                            String dataPath = MainApplication.instance.getTessDataParentDirectory();
                            tessTwo.init(dataPath,"tur");
                            //tx.setText(pyf.toString());


                            tessTwo.setImage(new File(Environment.getExternalStorageDirectory() + "/" + "myoutputfile" + ".png"));
                            //tessTwo.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "abcçdefghijklmnopqrsşŞtuvwxyz ");
                            tessTwo.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
                            String recognizedText = tessTwo.getUTF8Text();
                            tx.setText(recognizedText+" ve "+ tessTwo.meanConfidence()+ "\n");
                            ratio.put(recognizedText,tessTwo.meanConfidence());

                            String filePath = file2.getPath();
                            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + "myoutputfile" + ".png");
                            bitmap = RotateBitmap(bitmap,90);
                            im2.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText2 = tessTwo.getUTF8Text();
                            tx.append(recognizedText2+" ve "+ tessTwo.meanConfidence()+ "\n");

                            ratio.put(recognizedText2,tessTwo.meanConfidence());

                            bitmap =RotateBitmap(bitmap,90);
                            im3.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText3 = tessTwo.getUTF8Text();
                            tx.append(recognizedText3+" ve "+ tessTwo.meanConfidence() + "\n");

                            ratio.put(recognizedText3,tessTwo.meanConfidence());

                            bitmap =RotateBitmap(bitmap,90);
                            im4.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText4 = tessTwo.getUTF8Text();
                            tx.append(recognizedText4+" ve "+ tessTwo.meanConfidence()+ "\n");

                            ratio.put(recognizedText4,tessTwo.meanConfidence());

                            bitmap =RotateBitmap(bitmap,90);
                            im5.setImageBitmap(bitmap);
                            tessTwo.setImage(bitmap);
                            String recognizedText5 = tessTwo.getUTF8Text();
                            tx.append(recognizedText5+" ve "+ tessTwo.meanConfidence()+ "\n");

                            ratio.put(recognizedText5,tessTwo.meanConfidence());

                            int max=0;
                            String myC="";
                            for (String i : ratio.keySet()) {
                                if(ratio.get(i) > max) {
                                    max = ratio.get(i);
                                    myC = i;
                                }

                            }
                            tx.append("Bulunaaaaaaan  "+ myC+" ve "+ max+ "\n");


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

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    //Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
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
                    Toast.makeText(Camera2Exp.this,"Changed",Toast.LENGTH_SHORT).show();

                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {

        if (cameraDevice == null)
            Toast.makeText(Camera2Exp.this,"Error",Toast.LENGTH_SHORT).show();
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
