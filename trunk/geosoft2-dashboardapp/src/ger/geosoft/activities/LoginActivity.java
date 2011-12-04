package ger.geosoft.activities;

import ger.geosoft.R;
import android.app.Activity;
import android.os.Bundle;

public class LoginActivity extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
	}
	
//    private void authenticate() {
//    	if (username.getText().toString().equals("")
//				|| password.getText().toString().equals("")) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("Missing data");
//			builder.setIcon(android.R.drawable.ic_dialog_alert);
//			builder.setMessage("Please enter username and password.")
//				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//					}
//				});
//			builder.create().show();
//		} else {
//			progressDialog.show();
//			new Thread() {
//				public void run() {
//					try {
//						HttpClient httpClient = new DefaultHttpClient();
//						HttpPost postMethod = new HttpPost(Config.REST_URL
//								+ "/authentification.php");
//						ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
//				        parameters.add(new BasicNameValuePair("u", username.getText().toString()));
//				        parameters.add(new BasicNameValuePair("p", password.getText().toString()));
//						postMethod.setEntity(new UrlEncodedFormEntity(parameters));
//				        HttpResponse response = httpClient.execute(postMethod);
//				        String responseBody = EntityUtils.toString(response.getEntity());
//				        String result = responseBody;
//				        JSONObject json = new JSONObject(result);
//				        String status = json.getString("status");
//				        if (status.equals("STATUS_NOT_VALID")) {
//				        	handler.sendEmptyMessage(STATUS_NOT_VALID);
//				        }
//				        else if (status.equals("STATUS_OK")) {
//				        	User user = null;
//				        	if (json.has("data")) {
//				        		JSONObject data = json.getJSONObject("data");
//				        		if (data.getInt("st") > 3)
//				        			user = new Student();
//				        		else
//				        			user = new Lecturer();
//				        		user.setUid(data.getLong("uid"));
//				        		user.setFirstName(data.getString("first_name"));
//				        		user.setLastName(data.getString("last_name"));
//				        	}
//				        	store.setUser(user);
//				        	handler.sendEmptyMessage(STATUS_OK);
//				        }
//				        progressDialog.dismiss();
//					}
//			        catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					} catch (ClientProtocolException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//						handler.sendEmptyMessage(Store.NETWORK_PROBLEM);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//			    };
//			    private Handler handler = new Handler() {
//	            	@Override
//	            	public void handleMessage(Message msg) {
//	            		switch (msg.what) {
//						case Store.NETWORK_PROBLEM:
//							Toast.makeText(getApplicationContext(), 
//	            					"There is a problem with the network connection.", 
//	            					Toast.LENGTH_LONG).show();
//							break;
//						case STATUS_NOT_VALID:
//							Toast.makeText(getApplicationContext(), 
//	            					"This combination of username and password is not valid.", 
//	            					Toast.LENGTH_LONG).show();
//							password.setText("");
//							break;
//						case STATUS_OK:
//							Toast.makeText(
//									getApplicationContext(),
//									"Welcome " + store.getUser().getFirstName()
//											+ " "
//											+ store.getUser().getLastName()
//											+ "!", Toast.LENGTH_LONG).show();
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
//							break;
//						default:
//							break;
//						}
//	            		progressDialog.dismiss();
//	            	}
//	            };
//			}.start();
//		}
//	}
}
