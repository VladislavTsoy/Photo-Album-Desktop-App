package model;

import java.util.Comparator;

/**
 * This class is used to sort photos by date.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class DateComparator implements Comparator<Photo> {

	/**
	 * Compare by date
	 * 
	 * @param p1	Photo
	 * @param p2	Photo
	 */
	public int compare(Photo p1, Photo p2) {
		if(p1.getDate() - p2.getDate() < 0) {
			return -1;
		} else if(p1.getDate() - p2.getDate() > 0) {
			return 1;
		}
		return 0;
	}

}