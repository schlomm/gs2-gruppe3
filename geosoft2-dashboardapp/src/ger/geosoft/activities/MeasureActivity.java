package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.TextView;
import android.widget.Toast;

public class MeasureActivity extends Activity implements SensorEventListener,
		LocationListener {

	private SensorManager sensorManager;
	private LocationManager locationManager;
	private Location location;
	private double latitude, longitude;
	private Criteria crit;

	private boolean settingsShowing = false;

	SQLiteDatabase myDB = null;
	final static String MY_DB_NAME = "geosoft";
	final static String MY_DB_TABLE = "potholes";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measurementactivity);
		// tv = (TextView)findViewById(R.id.measureTv);

		latitude = 0;
		longitude = 0;

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);

		onCreateDBAndDBTabled();
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 * this piece of code has to be here, because the onResume() Method is
		 * also called after onCreate. Putting the check for the GPS here makes
		 * shure the user does not get the AlertDialog twice and that the user
		 * can not use the automatic Measurement without gps
		 */
		if (checkGpsAvailable()) {
			locationManager.requestLocationUpdates(
					locationManager.getBestProvider(crit, true), 1, 100, this);
		} else {
			createGpsDisabledAlert();
		}
	}

	// this Method checks if the GPS is switched on.
	// @return true wether the GPS is switched on or off
	private boolean checkGpsAvailable() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	// this Method creates an alert Dialog which notifies the User of the
	// disabled GPS
	// and takes him to the GPS Settings
	private void createGpsDisabledAlert() {
		new AlertDialog.Builder(this)
				.setMessage(
						"It seems your GPS is disabled. Do you want to change that?")
				.setCancelable(false)
				.setPositiveButton("Go to settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showGpsOptions();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				}).show();
	}

	// This Method starts the Location Settings of Android
	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	private void onCreateDBAndDBTabled() {
		myDB = this.openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
		myDB.execSQL("CREATE TABLE IF NOT EXISTS "
				+ MY_DB_TABLE
				+ "(id integer primary key autoincrement,lat double(20), lon double(20),strength double(20));");

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
				myDB.execSQL("INSERT INTO " + MY_DB_TABLE
						+ "(lat, lon, strength) VALUES ('" + latitude + "',' "
						+ longitude + "','" + accelationSquareRoot + "');");
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(this, "New location received", Toast.LENGTH_SHORT)
				.show();
		this.location = location;
		latitude = location.getLatitude();
		longitude = location.getLongitude();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(),
				"Please enable your GPS sensor...", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
