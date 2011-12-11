package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListActivity extends Activity {
	private SimpleCursorAdapter adapter = null;
	private Cursor c = null;
	SQLiteDatabase myDB = null;
	final static String MY_DB_NAME = "geosoft";
	final static String MY_DB_TABLE = "potholes";
	ListView list;
	TextView tv;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measurelist);
		list = (ListView) this.findViewById(R.id.measurelistTv);
		onCreateDBAndDBTable();

		
//		tv = (TextView)findViewById(R.id.measurelistTv);
		
		c = myDB.rawQuery("SELECT "+MY_DB_TABLE+".id as _id,* FROM "+ MY_DB_TABLE +";", null);
		int id = c.getColumnIndex("id");
		int lat = c.getColumnIndex("lat");
		int lon = c.getColumnIndex("lon");
		int strength = c.getColumnIndex("strength");
//		c.moveToFirst();
		adapter = new SimpleCursorAdapter(ListActivity.this, R.layout.measurements_list_item, c,new String[]{"lat","lon","strength"} ,new int[]{R.id.lat,R.id.lon, R.id.stren});
		list.setAdapter(adapter);
//		c.close();
//		if(c.getCount() > 0){
//			while(!c.isLast()){
//				String tmp = c.getInt(id)+",   "+c.getDouble(lat)+",   "+c.getDouble(lon)+",   "+c.getDouble(strength);
//				tv.setText(Html.fromHtml(""+tv.getText()+"</br>"+tmp));
//				c.moveToNext();
//			}
//			String tmp = c.getInt(id)+",   "+c.getDouble(lat)+",   "+c.getDouble(lon)+",   "+c.getDouble(strength);
//			tv.setText(Html.fromHtml(""+tv.getText()+"</br>"+tmp));
//		}
		
	}

	private void onCreateDBAndDBTable() {
		myDB = this.openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
		myDB.execSQL("CREATE TABLE IF NOT EXISTS "
				+ MY_DB_TABLE
				+ "(id integer primary key autoincrement,lat double(20), lon double(20),strength double(20));");
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.measurement_list_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.measurement_list_reset:
	        clearDB();
	        c.requery();
	        return true;
	    case R.id.measurement_list_submit:
	        //TODO submitten, leude
	        return false;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void clearDB() {
		myDB.delete(MY_DB_TABLE, null, null);
	}

}
