package model;

import java.io.File;
import java.io.Serializable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for the whole application.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class Model implements Serializable {

	private ObservableList<User> userList;
	private User currentUser;
	private Album currentAlbum;
	
	/**
	 * Application model constructor.
	 */
	public Model() {
		this.userList = FXCollections.observableArrayList();
		this.currentUser = null;
	}
	
	/**
	 * Add a new user.
	 * 
	 * @param name	Username of user being added
	 */
	public void addUser(String name) {
		User toAdd = new User(name);
		userList.add(toAdd);
	}
	
	/**
	 * Get a specific user.
	 * 
	 * @param username	Username of user
	 * @return			User, or null if not found
	 */
	public User getUser(String username) {
		for(User u : userList) {
			if(u.getUsername().equalsIgnoreCase(username)) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Get all the users in application.
	 * 
	 * @return	List of users
	 */
	public ObservableList<User> getAllUsers() {
		return userList;
	}
	
	/**
	 * Set the current user.
	 * 
	 * @param username
	 */
	public void setCurrentUser(String username) {
		this.currentUser = getUser(username);
	}
	
	/**
	 * Get the current user.
	 * 
	 * @return User
	 */
	public User getCurrentUser() {
		return currentUser;
	}
	
}
