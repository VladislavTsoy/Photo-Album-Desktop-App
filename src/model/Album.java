package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 * This class represents a photo album.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class Album implements Serializable {

	private SimpleStringProperty albumNameProperty;
	private SimpleIntegerProperty sizeProperty;
	private SimpleStringProperty startDateProperty, endDateProperty;
	private ObservableList<Photo> photosInAlbum;
	private Photo currentPhoto;

	public Album(String albumName) {
		this.albumNameProperty = new SimpleStringProperty(albumName);
		this.photosInAlbum = FXCollections.observableArrayList();
		this.currentPhoto = null;
	}
	
	/**
	 * Set the album name.
	 * 
	 * @param name	New album name
	 */
	public void setAlbumName(String name) {
		this.albumNameProperty.set(name);
	}
	
	/**
	 * Get the name of an album.
	 * 
	 * @return	Name of album
	 */
	public SimpleStringProperty getAlbumName() {
		return albumNameProperty;
	}
	
	/**
	 * Get the photos in the selected album.
	 * 
	 * @return	List of photos in the album
	 */
	public ObservableList<Photo> getPhotos() {
		return photosInAlbum;
	}
	
	/**
	 * Set size. Put here for TableView reasons.
	 * 
	 * @param size	Size
	 */
	public void setSize(int size) {
		return;
	}
	
	/**
	 * Get the number of photos in the album.
	 * 
	 * @return	Size of album
	 */
	public SimpleIntegerProperty getSize() {
		return new SimpleIntegerProperty(photosInAlbum.size());
	}
	
	/**
	 * Get the currently selected photo.
	 * 
	 * @param p	Photo
	 */
	public void setCurrentPhoto(Photo p) {
		this.currentPhoto = p;
	}
	
	/**
	 * Set the current photo to the one last clicked.
	 * 
	 * @return	Photo
	 */
	public Photo getCurrentPhoto() {
		return currentPhoto;
	}
	
	/**
	 * Here for TableView purposes.
	 * 
	 * @param date	Date
	 */
	public void setStartDate(long date) {
		return;
	}
	
	/**
	 * Get the date and time of the earliest photo in the album to populate cell.
	 * 
	 * @return	Date and time
	 */
	public SimpleStringProperty getStartDate() {
		List<Photo> byDate = photosInAlbum;
		if(byDate.size() == 0) {
			return new SimpleStringProperty("");
		}
		byDate.sort(new DateComparator());
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date(byDate.get(0).getDate());
		return new SimpleStringProperty(df.format(date));
	}
	
	/**
	 * Here for TableView purposes.
	 * 
	 * @param date	Date
	 */
	public void setEndDate(long date) {
		return;
	}
	
	/**
	 * Get the date and time of the most recent photo in the album to populate cell.
	 * 
	 * @return	Date and time
	 */
	public SimpleStringProperty getEndDate() {
		List<Photo> byDate = photosInAlbum;
		if(byDate.size() == 0) {
			return new SimpleStringProperty("");
		}
		byDate.sort(new DateComparator());
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date(byDate.get(getSize().get()-1).getDate());
		return new SimpleStringProperty(df.format(date));
	}
	
	/**
	 * Creates a photo and its thumbnail, then creates a new photo object and adds it to the album.
	 * 
	 * @param file	Photo file
	 */
	public Photo addPhoto(File file) {
		long date = file.lastModified();
		
		Image thumbnail = null;
		try {
			thumbnail = new Image(new FileInputStream(file), 125, 100, true, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int index = photosInAlbum.size();
		String caption = getAlbumName().get() + getSize().get();
		Photo photo = new Photo(file.getAbsolutePath(), index, thumbnail, caption, date);
		photosInAlbum.add(photo);
		if(index == 0) {
			this.currentPhoto = photo;
		}
		
		return photo;
	}
	
	/**
	 * Deletes a photo and updates the other indices.
	 *
	 * @param p	Photo
	 */
	
	public void deletePhoto(Photo p) {
		for(int i = 0; i < photosInAlbum.size(); i++) {
			if(photosInAlbum.get(i).equals(p)) {
				photosInAlbum.remove(i);
				for(int j = i + 1; j < photosInAlbum.size(); j++) {
					photosInAlbum.get(j).setIndex(j-1);
				}
			}
		}
		return;
	}
	
}
