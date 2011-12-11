package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}
	
	public void startLogin(View v){
		startActivity(new Intent(Dashboard.this, LoginActivity.class));
	}
	
	public void startManMeasure(View v){
		startActivity(new Intent(Dashboard.this, ManualMeasureActivity.class));
	}
	
	public void startAutoMeasure(View v){
		startActivity(new Intent(Dashboard.this, MeasureActivity.class));
	}

	public void startViewer(View v){
		startActivity(new Intent(Dashboard.this, ChooseViewActivity.class));
	}
	
	public void startSettings(View v){
		//TODO Die Kartenansicht dem richtigen Menü zuordnen
		//startActivity(new Intent(Dashboard.this, MapActivity.class));
	}
	
	public void startExit(View v){
		
		new AlertDialog.Builder(this)
			.setTitle("Sure?")
			.setMessage("Are you sure you want to quit?")
			.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setNegativeButton("No",null)
			.show();
		
	}
}
