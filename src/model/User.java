package model;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class represents a user.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class User implements Serializable {
	
	private String username;
	private Album currentAlbum;
	private ObservableList<Album> albumList;
	
	/**
	 * User constructor.
	 * 
	 * @param username
	 */
	public User(String username) {
		this.username = username;
		this.albumList = FXCollections.observableArrayList();
		this.currentAlbum = null;
	}
	
	/**
	 * Get the username of the user.
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Add an album.
	 * 
	 * @param name	Name of the album
	 */
	public void addAlbum(String name) {
		Album toAdd = new Album(name);
		albumList.add(toAdd);
	}

	/**
	 * Get the user's list of albums
	 * 
	 * @return	List of albums
	 */
	public ObservableList<Album> getAlbumList() {
		return albumList;
	}
	
	/**
	 * Get an album.
	 * 
	 * @param name	Name of the album
	 * @return		Album
	 */
	public Album getAlbum(String name) {
		for(Album a : albumList) {
			if(a.getAlbumName().get().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}
	
	/**
	 * Set the selected album.
	 * 
	 * @param album
	 */
	public void setCurrentAlbum(Album album) {
		this.currentAlbum = album;
	}
	
	/**
	 * Get the current album.
	 * 
	 * @return	Selected album
	 */
	public Album getCurrentAlbum() {
		return currentAlbum;
	}
	
}
