package model.math;

import java.awt.geom.Point2D;

/**
 * A point class which uses MarginedBigDecimals
 * @author mxw449
 */
public class PointBD {
	private MarginedBigDecimal x;
	private MarginedBigDecimal y;

	public PointBD(int x, int y) {
		this.x = new MarginedBigDecimal(x);
		this.y = new MarginedBigDecimal(y);
	}

	public PointBD(double x, double y) {
		this.x = new MarginedBigDecimal(x);
		this.y = new MarginedBigDecimal(y);
	}

	public PointBD(MarginedBigDecimal x, MarginedBigDecimal y) {
		this.x = x;
		this.y = y;
	}

	public PointBD(Point2D p2d) {
		this.x = new MarginedBigDecimal(p2d.getX());
		this.y = new MarginedBigDecimal(p2d.getY());
	}

	public Point2D getPoint2D() {
		return new Point2D.Double(getXVal(), getYVal());
	}

	/**
	 * Get the square of the distance between two points.
	 */
	public MarginedBigDecimal getDistanceSqrd(PointBD other) {
		MarginedBigDecimal x_sqrd = getX().subtract(other.getX()).pow(2);
		MarginedBigDecimal y_sqrd = getY().subtract(other.getY()).pow(2);
		return x_sqrd.add(y_sqrd);
	}

	public MarginedBigDecimal getX() {
		return x;
	}

	public MarginedBigDecimal getY() {
		return y;
	}

	public double getXVal() {
		return x.value();
	}

	public double getYVal() {
		return y.value();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PointBD)) {
			return false;
		}

		PointBD pbd = (PointBD) obj;

		return pbd.x.equals(x) && pbd.y.equals(y);
	}

	@Override
	public String toString() {
		return "PointBD(" + x + ", " + y + ")";
	}
}
