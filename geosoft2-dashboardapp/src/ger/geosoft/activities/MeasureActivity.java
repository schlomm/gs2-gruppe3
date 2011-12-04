package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MeasureActivity extends Activity implements SensorEventListener{
	
	private SensorManager sensorManager;
	private TextView tv;
	
	SQLiteDatabase myDB = null; 
	final static String MY_DB_NAME = "geosoft";
	final static String MY_DB_TABLE = "potholes";


	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measurementactivity);
		tv = (TextView)findViewById(R.id.measureTv);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		
		onCreateDBAndDBTabled();
	}


	private void onCreateDBAndDBTabled() {
		myDB = this.openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
	    myDB.execSQL("CREATE TABLE IF NOT EXISTS " + MY_DB_TABLE +"(id integer primary key autoincrement,lat double(20), lon double(20),strength double(20));");
		
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];

			float accelationSquareRoot = (x * x + y * y + z * z)
					/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

			// if the acceleration is bigger than 2, the "Schlagloch"-toast is
			// displayed on the screen

			if (accelationSquareRoot >= 2) {
				Toast.makeText(this, "Schlagloch", Toast.LENGTH_SHORT).show();
				myDB.execSQL("INSERT INTO "+MY_DB_TABLE+"(lat, lon, strength) VALUES ('52','7','"+accelationSquareRoot+"');");

			}
			
			
		}
		
	}
}
