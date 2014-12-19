package com.example.scanner_test2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
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
	ImageView imageView, debug_imageView;
	//ImageView debug_imageView1, debug_imageView2, debug_imageView3;
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
        debug_imageView = (ImageView)findViewById(R.id.debug_image);
        /*
        debug_imageView1 = (ImageView)findViewById(R.id.debug_image1);
        debug_imageView2 = (ImageView)findViewById(R.id.debug_image2);
        debug_imageView3 = (ImageView)findViewById(R.id.debug_image3);
        */
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
			
			debug_imageView.setVisibility(View.GONE);
			/*
			debug_imageView1.setVisibility(View.GONE);
			debug_imageView2.setVisibility(View.GONE);
			debug_imageView3.setVisibility(View.GONE);
			*/
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
        
        imageView.setVisibility(View.VISIBLE);
        debug_imageView.setVisibility(View.VISIBLE);
        /*
        debug_imageView1.setVisibility(View.VISIBLE);
        debug_imageView2.setVisibility(View.VISIBLE);
        debug_imageView3.setVisibility(View.VISIBLE);
        */
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
        Mat src4 = new Mat();
        Mat src5 = new Mat();
        Mat src6 = new Mat();
        
        Mat lines = new Mat();
         
        // find the img file from cell-phone
        srcBitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        		+"/scanner_test2_pic.jpg");
        
        // create a new bitmap which has the same size as srcBitmap
        Bitmap debug_bm = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);

        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B. 
        

        // process the image
        Imgproc.cvtColor(rgbMat, src1, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray processMat
        Imgproc.medianBlur(src1, src2, 11);
        //Imgproc.Laplacian(src2, src3, 3);
        //Imgproc.equalizeHist(src2,src2);
        Imgproc.Canny(src2, src4, 100, 100,3,true);

        
        //Imgproc.equalizeHist(src3,src4);
        //rgbMat.convertTo(src4, -1, 2.0, -180);
        //Imgproc.cvtColor(src4, src4, Imgproc.COLOR_RGB2GRAY);

        //Imgproc.medianBlur(src4, src4, 11);
        //Imgproc.Canny(src4, src4, 50, 100,3,true);
        

        /*
        src5 = Mat.zeros(rgbMat.size(), CvType.CV_8UC3);
        src5.setTo(new Scalar(255, 255, 255));
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src3,contours,src2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_TC89_KCOS);

		MatOfPoint2f approxCurve = new MatOfPoint2f();
        for (int idx = 0; idx < contours.size(); idx++){
        	if(Imgproc.arcLength(new MatOfPoint2f( contours.get(idx).toArray()), false) > 300){
				Imgproc.approxPolyDP(new MatOfPoint2f( contours.get(idx).toArray()), approxCurve, 
						Imgproc.arcLength(new MatOfPoint2f( contours.get(idx).toArray()), true)*0.02, true);

				Imgproc.drawContours(src5, contours, idx, new Scalar(94,255,11), 10);	
	        	

        	}
        }
        
        Imgproc.cvtColor(src5, src4, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(src4, src4, 100, 100);
        */


        // two ways to retrieve points
        // case 1. use mostLeft , mostRight, mostTop, and mostTop to obtain the max size of those four points
        // case 2. use maxSum, minSum, maxDiff, and minDiff to find four points (This may not form a rectangle)
        Imgproc.HoughLinesP(src4, lines, 1, Math.PI / 180, 50, 20, 20);

       	double mostLeft = Double.POSITIVE_INFINITY;
    	double mostRight= Double.NEGATIVE_INFINITY;
    	double mostTop= Double.POSITIVE_INFINITY;
    	double mostBot= Double.NEGATIVE_INFINITY; 
    	ArrayList<Line> all_lines= new ArrayList<Line>();
/*    	
    	Point leftTop=new Point(0,0);
    	Point leftBot = new Point(0,0);
    	Point rightTop = new Point(0,0);
    	Point rightBot = new Point(0,0);
    	double maxSum=Double.NEGATIVE_INFINITY, 
    			minSum = Double.POSITIVE_INFINITY,
    			maxDiff = Double.NEGATIVE_INFINITY, 
    			minDiff = Double.POSITIVE_INFINITY;*/
        for (int x = 0; x < lines.cols(); x++) 
        {
              double[] vec = lines.get(0, x);
              double x1 = vec[0], 
                     y1 = vec[1],
                     x2 = vec[2],
                     y2 = vec[3];
              
              Point p1 = new Point(x1,y1);
              Point p2 = new Point(x2,y2);
      
        	  all_lines.add(new Line(p1,p2));
        	  
        	  
        	  Core.line(src4, p1, p2, new Scalar(255,255,255));
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
              	//======================
              	/*
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
              	}*/

        }
        
        ArrayList<Line> temp_all_lines = new ArrayList<Line>();
        
        for(int i=0; i<all_lines.size(); i++){
        	temp_all_lines.add(all_lines.get(i));
        	for (int j = i + 1; j < all_lines.size(); j++){
        		Line linei = all_lines.get(i);
        		Line linej = all_lines.get(j);
        		double div = Math.sqrt((linei.getStart().x-linei.getEnd().x)*(linei.getStart().x-linei.getEnd().x)
        					+ (linei.getStart().y-linej.getEnd().y)*(linei.getStart().y-linej.getEnd().y) + 0.001);
        		
        		double d1 = ((linei.getStart().x-linei.getEnd().x) * (linei.getEnd().y-linej.getStart().y)
        					- (linei.getStart().y-linei.getEnd().y) * (linei.getEnd().x-linej.getStart().x))/div;
        		double d2 = ((linei.getStart().x-linei.getEnd().x) * (linei.getEnd().y-linej.getEnd().y)
    					- (linei.getStart().y-linei.getEnd().y) * (linei.getEnd().x-linej.getEnd().x))/div;
        		
        		if (d1*d1 < 100 && d2*d2 < 100) {
        			temp_all_lines.remove(all_lines.get(i));
                    break;
                }
        	}
        }
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = 0; i < temp_all_lines.size(); i++){
        	for (int j = i+1; j < temp_all_lines.size(); j++){
        		Point pt = computeIntersect(temp_all_lines.get(i), temp_all_lines.get(j));
        		// need to change this filter!
        		if (pt.x >= -100 && pt.y >= -100){
        			points.add(pt);
        		}
        	}
        }
        
        // find four corner points
    	Point leftTop=new Point(0,0);
    	Point leftBot = new Point(0,0);
    	Point rightTop = new Point(0,0);
    	Point rightBot = new Point(0,0);
    	double maxSum=Double.NEGATIVE_INFINITY, 
    			minSum = Double.POSITIVE_INFINITY,
    			maxDiff = Double.NEGATIVE_INFINITY, 
    			minDiff = Double.POSITIVE_INFINITY;
        for(int i=0; i<points.size(); i++){
          	//  find  maxSum, minSum, maxDiff, and minDiff starts ========================
        	double x1 = points.get(i).x;
        	double y1 = points.get(i).y;
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
        }
        if(leftTop.x<0 || leftTop.y<0){
        	Log.wtf("~~~~~~~~~~~~~~~~", 1+"");
        	leftTop.x = mostLeft;
        	leftTop.y = mostTop;
        }
        if(rightBot.x>rgbMat.width() || rightBot.y>rgbMat.height()){
        	Log.wtf("~~~~~~~~~~~~~~~~", 2+"");
        	rightBot.x = mostRight;
        	rightBot.y = mostBot;
        }
        if(leftBot.x<0 || leftBot.y>rgbMat.height()){
        	Log.wtf("~~~~~~~~~~~~~~~~", 3+"");
        	leftBot.x = mostLeft;
        	leftBot.y = mostBot;
        }
        if(rightTop.x>rgbMat.width() || rightTop.y<0){
        	Log.wtf("~~~~~~~~~~~~~~~~", 4+"");
        	rightTop.x = mostRight;
        	rightTop.y = mostTop;
        }
        
        
        
        for (int i = 0; i < temp_all_lines.size(); i++)
        {
        	//Core.circle(src4, temp_all_lines.get(i).getStart(), 50, new Scalar(255,255,255));
        	//Core.circle(src4, temp_all_lines.get(i).getEnd(), 50, new Scalar(255,255,255));
        	Core.line(src4, temp_all_lines.get(i).getStart(), temp_all_lines.get(i).getEnd(), new Scalar(255,255,255), 20);
        	
        }
        /*
        Point upleft = new Point(mostLeft,mostTop);
        Point upright = new Point(mostRight,mostTop);
        Point botleft = new Point(mostLeft,mostBot);
        Point botright = new Point(mostRight,mostBot);
        Core.line(src3, upleft, upleft, new Scalar(255,255,255), 50);
        Core.line(src3, upright, upright, new Scalar(255,255,255), 50);
        Core.line(src3, botleft, botleft, new Scalar(255,255,255), 50);
        Core.line(src3, botright, botright, new Scalar(255,255,255), 50);
        */
        // the following codes is for drawing 4 different corner points(case 2) on src3
        
        /*
        Core.line(src5, leftTop, leftTop, new Scalar(255,0,0), 50);
        Core.line(src5, rightTop, rightTop, new Scalar(255,0,0), 75);
        Core.line(src5, leftBot, leftBot, new Scalar(255,0,0), 100);
        Core.line(src5, rightBot, rightBot, new Scalar(255,0,0), 125);
        */
        Core.line(src4, leftTop, leftTop, new Scalar(255,255,255), 50);
        Core.line(src4, rightTop, rightTop, new Scalar(255,255,255), 75);
        Core.line(src4, leftBot, leftBot, new Scalar(255,255,255), 100);
        Core.line(src4, rightBot, rightBot, new Scalar(255,255,255), 125);
        
        
        
        // The following codes is for image transformation (case 2)
        
        int dst_width = 1000;
        int dst_height = 600;
        double pt_width = Math.sqrt(Math.pow((rightBot.x - leftBot.x), 2) + Math.pow((rightBot.y - leftBot.y), 2));
        double pt_height = Math.sqrt(Math.pow((leftTop.x - leftBot.x), 2) + Math.pow((leftTop.y - leftBot.y), 2));
        Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
        Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);
        //src_mat.put(0,0,leftTop.x,leftTop.y,rightTop.x, rightTop.y, leftBot.x, leftBot.y, rightBot.x, rightBot.y );
        //dst_mat.put(0,0, dst_width,0, 0,dst_height, dst_width,dst_height);
        if(pt_width > pt_height)
        	src_mat.put(0,0,leftTop.x,leftTop.y,rightTop.x, rightTop.y, leftBot.x, leftBot.y, rightBot.x, rightBot.y );
        else
        	src_mat.put(0,0,leftBot.x,leftBot.y,leftTop.x, leftTop.y, rightBot.x, rightBot.y, rightTop.x, rightTop.y );
        	
        dst_mat.put(0,0, 0,0,dst_width,0, 0,dst_height, dst_width,dst_height);
        Mat tempMat = Imgproc.getPerspectiveTransform(src_mat, dst_mat);       
        Mat dstMat=rgbMat.clone();
        Bitmap tempBm = Bitmap.createBitmap((int)dst_width,(int)dst_height, Config.RGB_565);
        Imgproc.warpPerspective(rgbMat, dstMat, tempMat, new Size(dst_width,dst_height));
        
        
        Utils.matToBitmap(dstMat, tempBm); //convert mat to bitmap
        Utils.matToBitmap(src4, debug_bm);
        debug_imageView.setImageBitmap(debug_bm);
        processBitmap = tempBm;
        // cutting the image based on the coordinates (Case 1, only for rectangle size)
        //processBitmap = Bitmap.createBitmap(tempBm, (int)mostLeft, (int)mostTop, (int)(mostRight-mostLeft), (int)(mostBot-mostTop));
        Log.i("~~~~~~~", "Picture process sucess...");  
    }

	private double min(double d, double e) {
		// TODO Auto-generated method stub
		if(d>=e)
			return d;
		else
			return e;
	}
	
	
	private class Line{
		Point start;
		Point end;
		Line(){
			start = new Point();
			end = new Point();
		}
		Line(Point X, Point Y){
			start = X;
			end = Y;
		}
		public Point getStart(){
			return start;
		}
		public Point getEnd(){
			return end;
		}
	}
	
	Point computeIntersect(Line a, Line b){
		double x1 = a.getStart().x;
		double y1 = a.getStart().y;
		double x2 = a.getEnd().x;
		double y2 = a.getEnd().y;
		double x3 = b.getStart().x;
		double y3 = b.getStart().y;
		double x4 = b.getEnd().x;
		double y4 = b.getEnd().y;
		double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
		if (d!=0){
			Point pt = new Point();
		    pt.x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
		    pt.y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
		    Point aa = new Point();
		    Point bb = new Point();
		    
		    if((x1-pt.x) * (x1-pt.x) + (y1 - pt.y) * (y1-pt.y) > (x2-pt.x) * (x2-pt.x) + (y2-pt.y)* (y2-pt.y)){
	            aa.x = x1;
	            aa.y = y1;
		    }else{
		         aa.x = x2;
		         aa.y = y2;
		    }
		    
		    if((x3-pt.x) * (x3-pt.x) + (y3 - pt.y) * (y3-pt.y) > (x4-pt.x) * (x4-pt.x) + (y4-pt.y)* (y4-pt.y)){
	            bb.x = x3;
	            bb.y = y3;	
		    }else{
	            bb.x = x4;
	            bb.y = y4;
		    }
		    
		    double ang = angle(aa, bb, pt);
		    if (ang > 0.5){
		    	return (new Point(-1000, -1000));
		    }
		    return pt;
		}else{
			return (new Point(-1000, -1000));
		}
		
	}

	double angle(Point pt1, Point pt2, Point pt0) {
		// TODO Auto-generated method stub
	    double dx1 = pt1.x - pt0.x;
	    double dy1 = pt1.y - pt0.y;
	    double dx2 = pt2.x - pt0.x;
	    double dy2 = pt2.y - pt0.y;
	    
	    return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
	}

}
