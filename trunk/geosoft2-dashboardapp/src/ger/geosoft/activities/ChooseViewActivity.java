package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ChooseViewActivity extends Activity implements OnClickListener{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooseview);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
    	case R.id.showListBtn:
    		startActivity(new Intent(ChooseViewActivity.this, ListActivity.class));
    		break;
    	case R.id.showMapBtn:
    		startActivity(new Intent(ChooseViewActivity.this, MapActivity.class));
    		break;
		}
		
	}
}
