package ger.geosoft.activities;

import ger.geosoft.R;
import ger.geosoft.store.Store;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

public class ManualMeasureActivity extends Activity implements
		SurfaceHolder.Callback {

	private ImageView iv_image;
	private SurfaceView sv;
	private RelativeLayout cameraImageLayout;

	private EditText description;
	private Button submit;
	private RatingBar rating;

	private Bitmap bmp;
	private Date measurementDate;

	// Camera variables
	private SurfaceHolder sHolder;
	private Camera mCamera;
	private Parameters parameters;
	private Camera.PictureCallback mCall;
	
	private Store store;
	private ProgressDialog progressDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualmeasure);
		store = (Store) getApplicationContext();
		
		iv_image = (ImageView) this.findViewById(R.id.imageView);
		sv = (SurfaceView) this.findViewById(R.id.surfaceView);
		submit = (Button) this.findViewById(R.id.manSubmitBtn);
		description = (EditText) this.findViewById(R.id.manDescriptionEdit);
		rating = (RatingBar) this.findViewById(R.id.manRating);
		cameraImageLayout = (RelativeLayout) this.findViewById(R.id.cameraImageLayout);
		
		// Get a surface
		sHolder = sv.getHolder();

		// add the callback interface methods defined below as the Surface View
		// callbacks
		sHolder.addCallback(this);

		// tells Android that this surface will have its data constantly
		// replaced
		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mCall = new Camera.PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// decode the data obtained by the camera into a Bitmap
				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

				// set the iv_image

				iv_image.setImageBitmap(bmp);

//				stopCamera();
			}
		};
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Registering.");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.surfaceView:
			takePicture();
			break;
		case R.id.manSubmitBtn:
			Log.i("manSumbitBtn","pressed");
			store.submitManualMeasurement(progressDialog, bmp, measurementDate, description.getText().toString().trim(), 52d, 7d, "yet to implement", "yet to implement", rating.getNumStars());
			break;
		}
	}

	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			stopCamera();
		}
	}

	private void resetToCamera() {

		startPreview();
		cameraImageLayout.setVisibility(View.VISIBLE);
		findViewById(R.id.taptoretake).setVisibility(View.GONE);
		iv_image.setVisibility(View.GONE);
	}

	private void takePicture() {
		mCamera.takePicture(null, null, mCall);
		measurementDate = new Date();
		cameraImageLayout.setVisibility(View.GONE);
//		sv.setVisibility(View.GONE);
		findViewById(R.id.taptoretake).setVisibility(View.VISIBLE);
		iv_image.setVisibility(View.VISIBLE);
	}

	private void stopCamera() {
		mCamera.stopPreview();
		// release the camera
		mCamera.release();
		// unbind the camera from this object
		mCamera = null;
	}
	
	private void startPreview(){
		// get camera parameters
		parameters = mCamera.getParameters();

		// set camera parameters
		mCamera.setParameters(parameters);
		mCamera.setDisplayOrientation(90);
		mCamera.startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		startPreview();

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
		// //stop the preview
		// mCamera.stopPreview();
		// //release the camera
		// mCamera.release();
		// //unbind the camera from this object
		// mCamera = null;

	}

	@Override
	public void onBackPressed() {
		if(cameraImageLayout.getVisibility() == View.GONE){
			resetToCamera();
		}else{
			finish();
		}

	}

}
