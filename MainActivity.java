package com.example.sudhanshus.kholcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;


public class MainActivity extends AppCompatActivity {

//    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "OCVSample::Activity";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public Bitmap photo;
    float x1, y1, x2, y2;
    float [] coord = new float[4];
    int i=0;
    float [] bbox = new float[4];
    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.kholcamera);
//        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setContentView(R.layout.imagecaptured);
        this.imageView = (ImageView) this.findViewById(R.id.imageView2);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //if(event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float y = event.getX();
                    if (i <= 1)
                        Toast.makeText(getBaseContext(), "Touch coordinates : " + "x" + x + "y" + y, Toast.LENGTH_LONG).show();
                    if (i < 2) {
                        coord[i] = x;
                        coord[i + 1] = y;
                    }
                    i++;
                    return false;
                }
            });

        }
        if (i < 2) {
            Toast.makeText(getBaseContext(), "Select two Points First", Toast.LENGTH_LONG).show();
        }
        int height = photo.getHeight();
        int width = photo.getWidth();
//        Mat mat = new Mat(height, width, CvType.CV_8UC4);
//        Utils.bitmapToMat(photo,mat);
        bbox[0] = coord[0];
        bbox[1] = coord[1];
        bbox[2] = coord[2]-coord[0];
        bbox[3] = coord[3]-coord[1];
        final Intent opencvcam = new Intent(MainActivity.this, MainActivity_show_camera.class);
        Bundle data1 = new Bundle();
        data1.putFloatArray("bbox", bbox);
        opencvcam.putExtras(data1);
        Button camerabutton = (Button) this.findViewById(R.id.button2);
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(opencvcam);
            }
        });

    }


}

