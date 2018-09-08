package model.math;

import java.util.ArrayList;
import java.util.List;

/**
 * A number of helpers methods
 * 
 * @author mxw449, thh489
 */
public class Helpers {
	private static final double FULL_ANGLE_DEG = 360;

	/**
	 * Find the angle between two points in degrees
	 */
	public static double angleBetweenPoints(PointBD p1, PointBD p2) {
		double angle = Math.toDegrees(Math.atan2(p2.getYVal() - p1.getYVal(), p2.getXVal() - p1.getXVal()) % FULL_ANGLE_DEG);

		while (angle < FULL_ANGLE_DEG) {
			angle += FULL_ANGLE_DEG;
		}

		return angle;
	}

	/**
	 * Remove all duplicates from a list
	 */
	public static <E> List<E> removeDuplicates(List<E> list) {
		List<E> ret = new ArrayList<E>();

		for (int i = 0; i < list.size(); i++) {
			if (!ret.contains(list.get(i))) ret.add(list.get(i));
		}

		return ret;

	}

}
