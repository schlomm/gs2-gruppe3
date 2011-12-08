package ger.geosoft.activities;

import ger.geosoft.R;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

public class ManualMeasureActivity extends Activity implements SurfaceHolder.Callback{
	
    private ImageView iv_image;    
    private SurfaceView sv;  
  
     
    private Bitmap bmp;  
  
    //Camera variables   
    private SurfaceHolder sHolder;  
    private Camera mCamera;   
    private Parameters parameters;
    private Camera.PictureCallback mCall;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualmeasure);
		
		
        iv_image = (ImageView) findViewById(R.id.imageView);  
        
        //get the Surface View at the main.xml file  
        sv = (SurfaceView) findViewById(R.id.surfaceView);  
  
        //Get a surface  
        sHolder = sv.getHolder();  
  
        //add the callback interface methods defined below as the Surface View callbacks  
        sHolder.addCallback(this);  
  
        //tells Android that this surface will have its data constantly replaced  
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        
//        bmp = BitmapFactory.decodeByteArray(new byte[]{0}, 0,1);
        
        mCall = new Camera.PictureCallback()  
        {  
        	
        	
            @Override  
            public void onPictureTaken(byte[] data, Camera camera)  
            {  
                //decode the data obtained by the camera into a Bitmap 
            	Log.i("hallo","wat"+data);
                bmp = BitmapFactory.decodeByteArray(
                		data,
                		0,
                		data.length);  
                
                //set the iv_image
                
                iv_image.setImageBitmap(bmp); 
//                iv_image.setImageResource(R.drawable.automeasure_green);
                
		        //stop the preview  
		        mCamera.stopPreview();  
		        //release the camera  
		        mCamera.release();  
		        //unbind the camera from this object  
		        mCamera = null; 
            }  
        };
		
	}
	
	public void onClick(View v){
		switch(v.getId()){
			case R.id.surfaceView:
				mCamera.takePicture(null, null, mCall);
//				mCamera.takePicture(null, mCall, mCall);
				
				
				sv.setVisibility(View.GONE);
				iv_image.setVisibility(View.VISIBLE);
//		        //stop the preview  
//		        mCamera.stopPreview();  
//		        //release the camera  
//		        mCamera.release();  
//		        //unbind the camera from this object  
//		        mCamera = null; 
			break;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
        //get camera parameters  
        parameters = mCamera.getParameters(); 
 
        //set camera parameters  
        mCamera.setParameters(parameters);  
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();  
 
        //sets what code should be executed after the picture is taken  
  
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where  
        // to draw the preview.  
        mCamera = Camera.open();  
        try {  
           mCamera.setPreviewDisplay(holder);  
  
        } catch (IOException exception) {  
            mCamera.release();  
            mCamera = null;  
        }
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
//        //stop the preview  
//        mCamera.stopPreview();  
//        //release the camera  
//        mCamera.release();  
//        //unbind the camera from this object  
//        mCamera = null; 
		
	}
	
	
}
