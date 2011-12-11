package ger.geosoft.store;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class Store extends Application {
	public static final int NETWORK_PROBLEM = 2; 
	public static final int STATUS_OK = 11;
	public static final int STATUS_NOT_VALID = 12;
	public static final int STATUS_EMAIL_ALREADY_REGISTERED = 13;
	public static final int STATUS_EMAIL_NOT_VALID = 14;
	public static final int STATUS_INVALID_SESSION = 15;
	
	public static final String url = "http://giv-geosofta.uni-muenster.de/potholes/potspot_web.php";
	
	public static final String ApplicationKey = "geosoft2potspot";
	
	private boolean loggedin = false;
	
	private User user;

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
		loggedin = true;
	}
	
	public boolean isLoggedIn(){
		return loggedin;
	}
	

	public void submitManualMeasurement(final ProgressDialog progressDialog, final Bitmap bitmap, final Date when, final String description, final double lat, final double lon,final String location_sensor_type, final String accuracy, int degree ){
			progressDialog.show();
			new Thread() {
				public void run() {
					try {
						
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream); //compress to which format you want.
						byte [] byte_arr = stream.toByteArray();
						String image_str = Base64.encodeToString(byte_arr,Base64.DEFAULT);
						
						HttpClient httpClient = new DefaultHttpClient();
						HttpPost postMethod = new HttpPost(Store.url);
						ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
							parameters.add(new BasicNameValuePair("action","submit_manual"));
							parameters.add(new BasicNameValuePair("key",Store.ApplicationKey));
							parameters.add(new BasicNameValuePair("sessionID",user.sessionID));
							parameters.add(new BasicNameValuePair("email",user.email));
							parameters.add(new BasicNameValuePair("measurement_type","manual"));
							parameters.add(new BasicNameValuePair("when",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(when)));
							parameters.add(new BasicNameValuePair("lat",lat+""));
							parameters.add(new BasicNameValuePair("lon",lon+""));
							parameters.add(new BasicNameValuePair("device_name",Build.MODEL));
							parameters.add(new BasicNameValuePair("location_sensor_type",location_sensor_type));
							parameters.add(new BasicNameValuePair("accuracy",accuracy));
							parameters.add(new BasicNameValuePair("description",description));
							parameters.add(new BasicNameValuePair("image",image_str));
						postMethod.setEntity(new UrlEncodedFormEntity(parameters));
				        HttpResponse response = httpClient.execute(postMethod);
				        String responseBody = EntityUtils.toString(response.getEntity());
				        String result = responseBody;
				        Log.i("jsonresult",result);
				        JSONObject json = new JSONObject(result);
				        String status = json.getString("status");
				        if (status.equals("STATUS_INVALID_SESSION")) {
				        	handler.sendEmptyMessage(STATUS_INVALID_SESSION);
				        }
				        else if (status.equals("STATUS_OK")) {				        		
				        	handler.sendEmptyMessage(STATUS_OK);
				        }
				        progressDialog.dismiss();
					}
			        catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(Store.NETWORK_PROBLEM);
					} catch (JSONException e) {
						e.printStackTrace();
					}
			    };
			    private Handler handler = new Handler() {
	            	@Override
	            	public void handleMessage(Message msg) {
	            		switch (msg.what) {
						case Store.NETWORK_PROBLEM:
							Toast.makeText(getApplicationContext(), 
	            					"There is a problem with the network connection.", 
	            					Toast.LENGTH_LONG).show();
							break;
						case STATUS_INVALID_SESSION:
							Toast.makeText(getApplicationContext(), 
	            					"There is something wrong with your session.", 
	            					Toast.LENGTH_LONG).show();
							break;
						case STATUS_OK:
							Toast.makeText(
									getApplicationContext(),
									"Transfer successful", Toast.LENGTH_LONG).show();
//							switch (getIntent().getExtras().getInt("startActivity")) {
//							case START_MYCOURSES:
//								startActivity(new Intent(getApplicationContext(), MyCourses.class));
//								break;
//							case START_MYPROFILE:
//								startActivity(new Intent(getApplicationContext(), MyProfile.class));
//								break;
//							case START_DASHBOARD:
//								startActivity(new Intent(
//										getApplicationContext(),
//										Dashboard.class)
//										.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//								break;
//							}
//							finish();
							break;
						default:
							Toast.makeText(
									getApplicationContext(),
									"There is something very wrong!", Toast.LENGTH_LONG).show();
							break;
						}
	            		progressDialog.dismiss();
	            	}
	            };
			}.start();
	}

}
