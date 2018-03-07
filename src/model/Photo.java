package model;

import java.io.Serializable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * This class represents a photo.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class Photo implements Serializable {

	private String path;
	private int index;
	private Image thumbnail;
	private String caption;
	private long dateOfPhoto;
	private ObservableList<Tag> tags;
	private VBox container = null;
	
	/**
	 * Constructor for photo
	 * 
	 * @param path			Location of photo
	 * @param index			Index of photo
	 * @param thumbnail		Thumbnail
	 * @param caption		Caption
	 * @param dateOfPhoto	Date of photo
	 */
	public Photo(String path, int index, Image thumbnail, String caption, long dateOfPhoto) {
		this.path = path;
		this.index = index;
		this.thumbnail = thumbnail;
		this.caption = caption;
		this.dateOfPhoto = dateOfPhoto;
		this.tags = FXCollections.observableArrayList();
	}
	
	/**
	 * Get the location of the photo.
	 * 
	 * @return	path to photo
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Get the thumbnail of the photo.
	 * 
	 * @return	Image
	 */
	public Image getThumbnail() {
		return thumbnail;
	}
	
	/**
	 * Set the index of a photo.
	 * 
	 * @param newIndex	The new index of the photo
	 */
	public void setIndex(int newIndex) {
		this.index = newIndex;
	}
	
	/**
	 * Get the index of the photo.
	 * 
	 * @return	int
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set the caption of a photo.
	 * 
	 * @param cap	Caption
	 */
	public void setCaption(String cap) {
		this.caption = cap;
	}
	
	/**
	 * Get the caption of a photo 
	 * 
	 * @return	Caption
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Get the date of the photo.
	 * 
	 * @return	long, to be converted into date format
	 */
	public long getDate() {
		return dateOfPhoto;
	}
	
	/**
	 * Add a tag to the photo.
	 * 
	 * @param type	tag type
	 * @param value	tag value
	 */
	public void addTag(String type, String value) {
		Tag tag = new Tag(type, value);
		tags.add(tag);
	}
	
	/**
	 * Get the list of a photo's tags
	 * 
	 * @return	Tag list
	 */
	public ObservableList<Tag> getTags() {
		return tags;
	}
	
	/**
	 * Search if a tag already exists
	 * 
	 * @param string	A tag as string value
	 * @return			True if found, else false
	 */
	public boolean tagFound(String string) {
		String[] a = string.split(",");
		
		for(Tag t : tags) {
			if(t.getTagType().equalsIgnoreCase(a[0].trim())) {
				if(t.getTagValue().equalsIgnoreCase(a[1].trim())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Set the VBox a photo is associated with.
	 * 
	 * @param vb	VBox
	 */
	public void setVB(VBox vb) {
		this.container = vb;
	}
	
	/**
	 * Get the VBox a photo is associated with.
	 * 
	 * @return	VBox
	 */
	public VBox getVB() {
		return container;
	}

	
}
