package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
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
		
	}
	
	public void startAutoMeasure(View v){
		startActivity(new Intent(Dashboard.this, MeasureActivity.class));
	}

	public void startViewer(View v){
		startActivity(new Intent(Dashboard.this, ListActivity.class));
	}
	
	public void startSettings(View v){
		
	}
	
	public void startExit(View v){
		finish();
		
	}
}
