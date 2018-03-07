package model;

import java.io.Serializable;

/**
 * Represents a photo's tag.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class Tag implements Serializable {

	private String type;
	private String value;
	
	/**
	 * Tag constructor
	 * 
	 * @param type	Tag type
	 * @param value	Tag value
	 */
	public Tag(String type, String value) {
		this.type = type.trim();
		this.value = value.trim();
	}
	
	/**
	 * Get the tag type.
	 * 
	 * @return	Type
	 */
	public String getTagType() {
		return type.trim();
	}
	
	/**
	 * Get the tag value.
	 * 
	 * @return	Value
	 */
	public String getTagValue() {
		return value.trim();
	}
	
	/**
	 * Represent the tag as a string.
	 * 
	 * @return	String
	 */
	public String toString() {
		return type + " : " + value;
	}
	
}
