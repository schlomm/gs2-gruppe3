package ger.geosoft.activities;

import ger.geosoft.R;
import ger.geosoft.store.Store;
import ger.geosoft.store.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private EditText username;
	private EditText email;
	private EditText password;
	private EditText password2;
	private Button registerButton;
	private ProgressDialog progressDialog;

	private Store store;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		store = (Store) getApplicationContext();
		
		username = (EditText) this.findViewById(R.id.registerUsername);
		email = (EditText) this.findViewById(R.id.registerEmail); 
		password = (EditText) this.findViewById(R.id.registerPassword);
		password2 = (EditText) this.findViewById(R.id.registerPasswordAgain);
		registerButton = (Button) this.findViewById(R.id.submitregisterButton);
		
		password2.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == 0) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(password2.getWindowToken(), 0);
	                registerButton.performClick();
                }
				return false;
			}
        });
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Registering.");
	}
	
	public void onClick(View v){
		switch(v.getId()){
			case R.id.submitregisterButton:
				register();
			break;
		}
	}
	
    private void register() {
    	if (username.getText().toString().equals("") || email.getText().toString().equals("")
				|| password.getText().toString().equals("") || password2.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Missing data");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage("Please enter your name, a valid email address and two matching passwords.")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
			builder.create().show();
    	} else if (!password.getText().toString().equals(password2.getText().toString())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Password error");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage("The passwords you supplied are not matching")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
			builder.create().show();
       	} else if (!(email.getText().toString().contains("@") && email.getText().toString().contains("."))) {
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("Please verify Email adress");
    			builder.setIcon(android.R.drawable.ic_dialog_alert);
    			builder.setMessage("Check your Email")
    				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						dialog.cancel();
    					}
    				});
    			builder.create().show();			
		} else {
			progressDialog.show();
			new Thread() {
				public void run() {
					try {
						HttpClient httpClient = new DefaultHttpClient();
						HttpPost postMethod = new HttpPost(Store.url);
						ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(6);
						parameters.add(new BasicNameValuePair("action","register"));
				        parameters.add(new BasicNameValuePair("key",Store.ApplicationKey));						
				        parameters.add(new BasicNameValuePair("username", username.getText().toString().trim()));
				        parameters.add(new BasicNameValuePair("email", email.getText().toString().trim()));
				        parameters.add(new BasicNameValuePair("password", password.getText().toString()));
				        parameters.add(new BasicNameValuePair("password2", password2.getText().toString()));
				        postMethod.setEntity(new UrlEncodedFormEntity(parameters));
				        HttpResponse response = httpClient.execute(postMethod);
				        String responseBody = EntityUtils.toString(response.getEntity());
				        String result = responseBody;
				        Log.i("jsonresult",result);
				        JSONObject json = new JSONObject(result);
				        String status = json.getString("status");
				        
					        if (status.equals("STATUS_EMAIL_NOT_VALID")) {
					        	handler.sendEmptyMessage(Store.STATUS_EMAIL_NOT_VALID);
					        } else if (status.equals("STATUS_EMAIL_ALREADY_REGISTERED")) {
					        	handler.sendEmptyMessage(Store.STATUS_EMAIL_ALREADY_REGISTERED);
					        } else if (status.equals("STATUS_OK")) {
					        	JSONObject data = json.getJSONObject("data");
					        	store.setUser(new User(data.getString("username"),data.getString("sessionID"),data.getString("email")));
					        	handler.sendEmptyMessage(Store.STATUS_OK);
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
						case Store.STATUS_OK:
							Toast.makeText(
									getApplicationContext(),
									"Registration successful. Welcome "+store.getUser().username, Toast.LENGTH_LONG).show();
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
						case Store.STATUS_EMAIL_ALREADY_REGISTERED:
							Toast.makeText(getApplicationContext(), 
	            					"Your email address appears to be already registered", 
	            					Toast.LENGTH_LONG).show();
							password.setText("");
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

}
