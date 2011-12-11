package ger.geosoft.store;

public class User {
	
	public final String sessionID;
	
	public final String username;
	
	public final String email;
	
	public User(String username, String sessionID, String email){
		this.sessionID = sessionID;
		this.username = username;
		this.email = email;
	}


}
