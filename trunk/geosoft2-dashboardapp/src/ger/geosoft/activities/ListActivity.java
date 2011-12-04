package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class ListActivity extends Activity{
	
	SQLiteDatabase myDB = null; 
	final static String MY_DB_NAME = "geosoft";
	final static String MY_DB_TABLE = "potholes";
	TextView tv;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measurelist);
		onCreateDBAndDBTabled();
		
		tv = (TextView)findViewById(R.id.measurelistTv);
		
		Cursor c = myDB.rawQuery("SELECT * FROM "+ MY_DB_TABLE +";", null);
		int id = c.getColumnIndex("id");
		int lat = c.getColumnIndex("lat");
		int lon = c.getColumnIndex("lon");
		int strength = c.getColumnIndex("strength");
		c.moveToFirst();
		
		if(c.getCount() > 0){
			while(!c.isLast()){
				String tmp = c.getInt(id)+",   "+c.getDouble(lat)+",   "+c.getDouble(lon)+",   "+c.getDouble(strength);
				tv.setText(Html.fromHtml(""+tv.getText()+"</br>"+tmp));
				c.moveToNext();
			}
			String tmp = c.getInt(id)+",   "+c.getDouble(lat)+",   "+c.getDouble(lon)+",   "+c.getDouble(strength);
			tv.setText(Html.fromHtml(""+tv.getText()+"</br>"+tmp));
		}
		c.close();
	}
	
	private void onCreateDBAndDBTabled() {
		myDB = this.openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
	    myDB.execSQL("CREATE TABLE IF NOT EXISTS " + MY_DB_TABLE +"(id integer primary key autoincrement,lat double(20), lon double(20),strength double(20));");	
	}
}
