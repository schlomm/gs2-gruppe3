package ger.geosoft.store;

import android.app.Application;

public class Store extends Application {
	public static final int NETWORK_PROBLEM = 2; 
	
	public static final String url = "http://giv-geosofta.uni-muenster.de/potholes/potspot_web.php";
	
	public static final String ApplicationKey = "geosoft2potspot";
	
	public boolean loggedin = false;
	
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
}
