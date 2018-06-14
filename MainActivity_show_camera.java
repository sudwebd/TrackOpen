package com.example.sudhanshus.kholcamera;

/**
 * Created by sudhanshu.s on 6/13/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.TrackerKCF;

// OpenCV Classes

public class MainActivity_show_camera extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    static {
        System.loadLibrary("opencv_java3");
    }

    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;
    Rect2d bbox ;
    TrackerKCF tracker;
    //    Intent i = new Intent(this, Second.class)
//    Rect2d bbox;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity_show_camera() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.show_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        Intent intentr = getIntent();
//    Bitmap photo = (Bitmap) intent.getParcelableExtra("Image");
        Bundle ext = intentr.getExtras();
        float [] ans = ext.getFloatArray("bbox");
        bbox = new Rect2d((int)(ans[0]),(int)(ans[1]),(int)(ans[2]),(int)(ans[3]));
//        bbox = new Rect2d(100,200,100,100);
        tracker = TrackerKCF.create();

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        //bbox = new Rect2d(mRgba.rows()/10,mRgba.cols()/10,mRgba.rows()/3,mRgba.cols()/3);
        tracker.init(mRgba, bbox);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // TODO Auto-generated method stub

        mRgba = inputFrame.rgba();
        tracker.update(mRgba,bbox);
//        if (ok)
//        {
            Point p1 = new Point(bbox.x,bbox.y);
            Point p2 = new Point(bbox.x+bbox.width,bbox.y+bbox.height);
            //new Point(mRgba.rows()/10,mRgba.cols()/10),new Point(mRgba.rows()/2,mRgba.cols()/2)
            Imgproc.rectangle(mRgba,new Point(mRgba.rows()/10,mRgba.cols()/10),new Point(mRgba.rows()/2,mRgba.cols()/2),new Scalar(255),10);
       // }
        // Rotate mRgba 90 degrees
       Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Imgproc.putText(mRgba, "Edited by me", new Point(mRgba.rows()/2,mRgba.cols()/2),Core.FONT_ITALIC, 1.0 ,new  Scalar(255));
        Core.flip(mRgbaF, mRgba, 1 );

        return mRgba; // This function must return
    }
}
