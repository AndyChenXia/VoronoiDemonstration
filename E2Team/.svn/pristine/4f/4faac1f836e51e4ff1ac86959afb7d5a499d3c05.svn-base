package model.math;

import java.awt.geom.Line2D;

/**
 * A line object which uses MarginedBigDecimals
 * @author mxw449
 */
public class LineBD {
	private PointBD p1, p2;

	public LineBD(PointBD p1, PointBD p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public LineBD(int x1, int y1, int x2, int y2) {
		this.p1 = new PointBD(x1, y1);
		this.p2 = new PointBD(x2, y2);
	}

	public LineBD(double x1, double y1, double x2, double y2) {
		this.p1 = new PointBD(x1, y1);
		this.p2 = new PointBD(x2, y2);
	}

	public LineBD(MarginedBigDecimal x1, MarginedBigDecimal y1, MarginedBigDecimal x2, MarginedBigDecimal y2) {
		this.p1 = new PointBD(x1, y1);
		this.p2 = new PointBD(x2, y2);
	}

	public LineBD(Line2D l2d) {
		this.p1 = new PointBD(l2d.getX1(), l2d.getY1());
		this.p2 = new PointBD(l2d.getX2(), l2d.getY2());
	}

	/**
	 * Convert the line to a java.awt.geom.Line2D object
	 * 
	 * @return
	 */
	public Line2D getLine2D() {
		return new Line2D.Double(p1.getPoint2D(), p2.getPoint2D());
	}

	/**
	 * Does the line contain a point?
	 */
	public boolean contains(PointBD point) {
		return getLine2D().ptSegDist(point.getPoint2D()) <= 0.01;
	}

	/**
	 * Do these two lines intersect?
	 */
	public boolean intersectsLine(LineBD other) {
		return this.getLine2D().intersectsLine(other.getLine2D());
	}

	/**
	 * Find the point where two LineBDs intersect
	 */
	public PointBD getIntersectionPoint(LineBD line) {
		double x1 = line.getX1().value();
		double y1 = line.getY1().value();
		double x2 = line.getX2().value();
		double y2 = line.getY2().value();
		double x3 = getX1().value();
		double y3 = getY1().value();
		double x4 = getX2().value();
		double y4 = getY2().value();
		double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		if (denom == 0.0) { // Lines are parallel.
			return null;
		}
		double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
		double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
		if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
			// Get the intersection point.
			return new PointBD(x1 + ua * (x2 - x1), y1 + ua * (y2 - y1));
		}

		return null;
	}

	
	/**
	 * Gets the centre point of a line as a PointBD
	 */
	public PointBD getCentrePoint() {
		MarginedBigDecimal x = getX1().add(getX2()).divide(MarginedBigDecimal.TWO);
		MarginedBigDecimal y = getY1().add(getY2()).divide(MarginedBigDecimal.TWO);
		return new PointBD(x, y);
	}
	
	public PointBD getP1() {
		return p1;
	}

	public PointBD getP2() {
		return p2;
	}

	public MarginedBigDecimal getX1() {
		return getP1().getX();
	}

	public MarginedBigDecimal getY1() {
		return getP1().getY();
	}

	public MarginedBigDecimal getX2() {
		return getP2().getX();
	}

	public MarginedBigDecimal getY2() {
		return getP2().getY();
	}
	
	public MarginedBigDecimal getDistance(PointBD p) {
		double val = getLine2D().ptSegDist(p.getPoint2D());
		return new MarginedBigDecimal(val);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LineBD)) {
			return false;
		}

		LineBD pbd = (LineBD) obj;

		return (pbd.p1.equals(p1) && pbd.p2.equals(p2)) || (pbd.p1.equals(p2) && pbd.p2.equals(p1));
	}

	@Override
	public String toString() {
		return "LineBD(" + p1 + " to " + p2 + ")";
	}

}
