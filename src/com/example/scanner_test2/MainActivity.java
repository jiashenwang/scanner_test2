package com.example.scanner_test2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	private static int TAKE_PICTURE = 1;
	private Uri imageUri;
	ImageView imageView;
	Button change;
	private static boolean flag = true;   
    private static boolean isFirst = true;
    Bitmap srcBitmap;  
    Bitmap processBitmap;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS ) {
            	Log.i("~~~~~~~", "Load successful!");
            } else {
                super.onManagerConnected(status);
                Log.i("~~~~~~~", "Load failed!");
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(cameraListener);
        imageView = (ImageView)findViewById(R.id.image);
        change = (Button) findViewById(R.id.change);
        
    }
    
	@Override
	protected void onResume(){
	    Log.i("~~~~", "Called onResume");
	    super.onResume();
	    
	    Log.i("~~~~", "Trying to load OpenCV library");
	    if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this, mLoaderCallback))
	    {
	        Log.e("~~~~", "Cannot connect to OpenCV Manager");
	    }
	      
	}
    
    private OnClickListener cameraListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			takePhoto(v);
		}
    };

	protected void takePhoto(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/scanner_test2_pic.jpg");
		imageUri = Uri.fromFile(photo);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, TAKE_PICTURE);
		
		
	}
	
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // call the parent
        super.onActivityResult(requestCode, resultCode, data);
        
        change.setVisibility(View.VISIBLE);
        isFirst = true;
        showPic();
        imageView.setImageBitmap(srcBitmap); 
        change.setOnClickListener(new ProcessClickListener());
    } 

	private class ProcessClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            if(isFirst)  
            {  
            	processImage();  
                isFirst = false;  
            }
            if(flag){  
                imageView.setImageBitmap(processBitmap);  
                change.setText("Check Original");  
                flag = false;  
            } 
            else{  
            	imageView.setImageBitmap(srcBitmap);  
                change.setText(" change ");  
                flag = true;  
            } 
		}
		
	}
	
	public void showPic(){
		srcBitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        		+"/scanner_test2_pic.jpg");
	}
	public void processImage(){  
        Mat rgbMat = new Mat();  
        Mat src1 = new Mat();
        Mat src2 = new Mat();
        Mat src3 = new Mat();
        Mat lines = new Mat();
         
        // find the img file from cell-phone
        srcBitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        		+"/scanner_test2_pic.jpg");
        
        // create a new bitmap which has the same size as srcBitmap
        Bitmap tempBm = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B. 
        

        // process the image
        Imgproc.cvtColor(rgbMat, src1, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray processMat 
        Imgproc.equalizeHist(src1, src2);
        Imgproc.Canny(src2, src3, 100, 700);
        //Photo.fastNlMeansDenoising(src3, src4, 20, 7, 21);
        //Size size = new Size(10,10);
        //Imgproc.blur(src4, src5, size, new Point(-1,-1) );
        //Photo.fastNlMeansDenoising(src4, src5, 20, 7, 21);
        
        // two ways to retrieve points
        // case 1. use mostLeft , mostRight, mostTop, and mostTop to obtain the max size of those four points
        // case 2. use maxSum, minSum, maxDiff, and minDiff to find four points (This may not form a rectangle)
        int w = rgbMat.width(), h = rgbMat.height(), min_w = 200;
        double scale = min(10.0, w * 1.0 / min_w);
        int w_proc = (int) (w * 1.0 / scale), h_proc = (int) (h * 1.0 / scale);
        Imgproc.HoughLinesP(src3, lines, 1, Math.PI / 180, w_proc / 3, w_proc / 3, 20);
        
    	double mostLeft = Double.POSITIVE_INFINITY;
    	double mostRight= Double.NEGATIVE_INFINITY;
    	double mostTop= Double.POSITIVE_INFINITY;
    	double mostBot= Double.NEGATIVE_INFINITY; 
    	
    	Point leftTop=new Point(0,0);
    	Point leftBot = new Point(0,0);
    	Point rightTop = new Point(0,0);
    	Point rightBot = new Point(0,0);

    	double maxSum=Double.NEGATIVE_INFINITY, 
    			minSum = Double.POSITIVE_INFINITY,
    			maxDiff = Double.NEGATIVE_INFINITY, 
    			minDiff = Double.POSITIVE_INFINITY;
    	
        for (int x = 0; x < lines.cols(); x++) 
        {
              double[] vec = lines.get(0, x);
              double x1 = vec[0], 
                     y1 = vec[1],
                     x2 = vec[2],
                     y2 = vec[3];
              
            //  find  mostLeft , mostRight, mostTop, and mostTop begin ========================
          	if(x1 < mostLeft){
          		mostLeft = x1;
          	}
          	if(x2 < mostLeft){
          		mostLeft = x2;
          	}
          	if(x1 > mostRight){
          		mostRight = x1;
          	}
          	if(x2 > mostRight){
          		mostRight = x2;
          	}
          	if(y1 < mostTop){
          		mostTop = y1;
          	}
          	if(y2 < mostTop){
          		mostTop = y2;
          	}
          	if(y1 > mostBot){
          		mostBot = y1;
          	}
          	if(y2 > mostBot){
          		mostBot = y2;
          	}
          	//  find  mostLeft , mostRight, mostTop, and mostTop ends ========================
          	//==============================
          	//  find  maxSum, minSum, maxDiff, and minDiff starts ========================
          	if(x1+y1 <= minSum){
          		minSum = x1+y1;
          		leftTop.x = x1;
          		leftTop.y = y1;
          	}
          	if(x1+y1 >= maxSum){
          		maxSum = x1+y1;
          		rightBot.x = x1;
          		rightBot.y = y1;
          	}
          	if(x1-y1 <= minDiff){
          		minDiff = x1-y1;
          		leftBot.x = x1;
          		leftBot.y = y1;
          	}
          	if(x1-y1 >= maxDiff){
          		maxDiff = x1-y1;
          		rightTop.x = x1;
          		rightTop.y = y1;
          	}
          	
          	if(x2+y2 <= minSum){
          		minSum = x2+y2;
          		leftTop.x = x2;
          		leftTop.y = y2;
          	}
          	if(x2+y2 >= maxSum){
          		maxSum = x2+y2;
          		rightBot.x = x2;
          		rightBot.y = y2;
          	}
          	if(x2-y2 <= minDiff){
          		minDiff = x2-y2;
          		leftBot.x = x2;
          		leftBot.y = y2;
          	}
          	if(x2-y2 >= maxDiff){
          		maxDiff = x2-y2;
          		rightTop.x = x2;
          		rightTop.y = y2;
          	}
          	//  find  maxSum, minSum, maxDiff, and minDiff ends ========================
          	//================================

        }
        
        Point upleft = new Point(mostLeft,mostTop);
        Point upright = new Point(mostRight,mostTop);
        Point botleft = new Point(mostLeft,mostBot);
        Point botright = new Point(mostRight,mostBot);
        Core.line(src3, upleft, upleft, new Scalar(255,255,255), 50);
        Core.line(src3, upright, upright, new Scalar(255,255,255), 50);
        Core.line(src3, botleft, botleft, new Scalar(255,255,255), 50);
        Core.line(src3, botright, botright, new Scalar(255,255,255), 50);
        
        // the following codes is for drawing 4 different corner points(case 2) on src3
        /*
        Core.line(src3, leftTop, leftTop, new Scalar(255,255,255), 50);
        Core.line(src3, rightTop, rightTop, new Scalar(255,255,255), 50);
        Core.line(src3, leftBot, leftBot, new Scalar(255,255,255), 50);
        Core.line(src3, rightBot, rightBot, new Scalar(255,255,255), 50);
        */
        
        
        // The following codes is for image transformation (case 2)
        /*
        double dst_width = Math.sqrt(Math.pow((rightBot.x - leftBot.x), 2) + Math.pow((rightBot.y - leftBot.y), 2));
        double dst_height = Math.sqrt(Math.pow((leftTop.x - leftBot.x), 2) + Math.pow((leftTop.y - leftBot.y), 2));
        Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
        Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);
        src_mat.put(0,0,leftTop.x,leftTop.y,rightTop.x, rightTop.y, leftBot.x, leftBot.y, rightBot.x, rightBot.y );
        dst_mat.put(0,0, 0,0, dst_width,0, 0,dst_height, dst_width,dst_height);
        Mat tempMat = Imgproc.getPerspectiveTransform(src_mat, dst_mat);       
        Mat dstMat=rgbMat.clone();
        Imgproc.warpPerspective(rgbMat, dstMat, tempMat, new Size(dst_width, dst_height));
        */
        
        Utils.matToBitmap(rgbMat, tempBm); //convert mat to bitmap  
        //processBitmap = tempBm;
        // cutting the image based on the coordinates (Case 1, only for rectangle size)
        processBitmap = Bitmap.createBitmap(tempBm, (int)mostLeft, (int)mostTop, (int)(mostRight-mostLeft), (int)(mostBot-mostTop));
        Log.i("~~~~~~~", "Picture process sucess...");  
    }

	private double min(double d, double e) {
		// TODO Auto-generated method stub
		if(d>=e)
			return d;
		else
			return e;
	}

}
