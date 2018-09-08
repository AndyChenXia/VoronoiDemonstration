package model;

import static model.math.MarginedBigDecimal.TWO;
import static model.math.MarginedBigDecimal.ZERO;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import model.math.Helpers;
import model.math.LineBD;
import model.math.MarginedBigDecimal;
import model.math.PointBD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The container class for a Voronoi polygon.
 * 
 * @author mcs448, mxw449, std487, thh489
 *
 */
public class VoronoiPolygon {

	private static final Logger log = LoggerFactory.getLogger(VoronoiPolygon.class);

	/** The canvas onto which this polygon is placed */
	private final Canvas canvas;
	
	private final PointBD centre;
	private List<LineBD> edges;

	/** Stores whether the polygon has been modified since it was last drawn */
	private boolean modified = true;

	/** The converted java polygon object */
	private Polygon javaPoly = null;

	/**
	 * Create a new VoronoiPolygon with a centre point and the canvas it is
	 * being added to
	 */
	public VoronoiPolygon(PointBD centre, Canvas canvas) {
		this.centre = centre;
		this.canvas = canvas;
		edges = new ArrayList<>();
	}

	/**
	 * Returns the list of edges in the polygon.
	 */
	public List<LineBD> getEdges() {
		return edges;
	}

	/**
	 * Add an edge.
	 */
	public void addEdge(LineBD edge) {
		this.modified = true;
		edges.add(edge);
	}

	/**
	 * Remove duplicate edges from the polygon. Debug method only. Shouldn't be
	 * used in final product
	 */
	public void removeDuplicateEdges() {
		edges = Helpers.removeDuplicates(edges);
	}

	/**
	 * Get the bisection between the centre of the polygon, and a point p.
	 */
	public LineBD getBisection(PointBD p) {

		MarginedBigDecimal midX = (p.getX().add(centre.getX())).divide(TWO);
		MarginedBigDecimal midY = (p.getY().add(centre.getY())).divide(TWO);

		// If they are on the same Y location, the below code breaks
		// Simply create a new line going from the top to the bottom of the
		// canvas, along the mid-X coordinate
		if (p.getY().equals(centre.getY())) {
			PointBD p1 = new PointBD(midX, ZERO);
			PointBD p2 = new PointBD(midX, canvas.getHeight());
			return new LineBD(p1, p2);
		}

		//calculates the bisection by first finding the midpoint between the centres
		MarginedBigDecimal diffX = p.getX().subtract(centre.getX());
		MarginedBigDecimal diffY = p.getY().subtract(centre.getY());
		
		//then calculates the gradient that is at a tangent to the line between the two centre points
		MarginedBigDecimal gradient = diffX.negate().divide(diffY);
		MarginedBigDecimal constant = midY.subtract(gradient.multiply(midX));
		
		//as we need the bisection as a line that goes thought the entire canvas, 
		//we find where the line will intersect either the line y=0 or x=0
		if (gradient.compareTo(canvas.getHeight().multiply(canvas.getWidth())) > 0) {
			PointBD p1 = new PointBD(constant.negate().divide(gradient), ZERO);
			MarginedBigDecimal n = canvas.getHeight().subtract(constant);
			PointBD p2 = new PointBD(n.divide(gradient), canvas.getHeight());
			return new LineBD(p1, p2);
		} else {
			PointBD p1 = new PointBD(ZERO, constant);
			MarginedBigDecimal n = (gradient.multiply(canvas.getWidth())).add(constant);
			PointBD p2 = new PointBD(canvas.getWidth(), n);
			return new LineBD(p1, p2);
		}
	}

	/**
	 * Trim the polygon based on a new bisection produced by a new point. (ie
	 * removes lines that are closer to another centre point)
	 */
	public void trim(LineBD boundedBisection) {
		this.modified = true;

		ArrayList<LineBD> newEdges = new ArrayList<>();

		log.debug("Trimming with: {}", boundedBisection);

		/*
		 * works by drawing lines from each point in the polygon and the centre
		 * if any of these lines crosses the bisection lines, then that point
		 * needs to be removed and the lines altered. If both end points of a
		 * line nneed to be removed, the whole line is removed
		 */
		for (LineBD currentEdge : edges) {

			// Draw a line from each end of the edge to the centre point of the
			// polygon.
			// If this line crosses the intersection, we know that this line
			// needs to be trimmed
			LineBD p1toCentre = new LineBD(currentEdge.getP1(), centre);
			LineBD p2toCentre = new LineBD(currentEdge.getP2(), centre);

			boolean p1OverLine = p1toCentre.intersectsLine(boundedBisection);
			boolean p2OverLine = p2toCentre.intersectsLine(boundedBisection);

			if (p1OverLine && p2OverLine) {
				log.debug("Removing whole line: {}", currentEdge);
				// The line is "removed" by not adding it to the new edges
			} else if (p1OverLine || p2OverLine) {
				LineBD newEdge;

				if (p1OverLine) {
					if (currentEdge.contains(boundedBisection.getP1())) {
						newEdge = new LineBD(currentEdge.getP2(), boundedBisection.getP1());
						newEdges.add(newEdge);
					} else {
						newEdge = new LineBD(currentEdge.getP2(), boundedBisection.getP2());
						newEdges.add(newEdge);
					}
				} else {
					if (currentEdge.contains(boundedBisection.getP1())) {
						newEdge = new LineBD(currentEdge.getP1(), boundedBisection.getP1());
						newEdges.add(newEdge);
					} else {
						newEdge = new LineBD(currentEdge.getP1(), boundedBisection.getP2());
						newEdges.add(newEdge);
					}
				}
				log.debug("Removing part of line: {}", currentEdge);
				log.debug("Added part line: {}", newEdge);
			} else {
				log.debug("Keeping whole line: {}", currentEdge);
				newEdges.add(currentEdge);
			}
		}

		newEdges.add(boundedBisection);
		edges = newEdges;

	}

	/**
	 * Is the polygon complete? Are all of the edges joined?
	 */
	public boolean isCompleteWithoutEdges() {
		if (this.edges.isEmpty()) {
			return false;
		}

		// Get the number of edges on each side of the canvas
		int[] borderEdges = borderEdges();

		// If any of the edges has an odd number of edges on them, then the
		// polygon cannot be complete
		for (int i = 0; i < 4; i++) {
			if (borderEdges[i] % 2 == 1) return false;
		}

		// Get all of the unjoined edges
		List<PointBD> unjoined = getUnjoined();
		int count = 0;

		for (PointBD p : unjoined) {
			if (p.getX().equals(ZERO) || p.getX().equals(canvas.getWidth()) || p.getY().equals(ZERO) || p.getY().equals(canvas.getHeight())) count++;
		}

		return count == unjoined.size();
	}

	/**
	 * Returns the number of edges which should be on each side of the canvas
	 */
	private int[] borderEdges() {
		// Each int represents how many edges are on an edge (left, top, right,
		// bottom)
		int[] numEdges = { 0, 0, 0, 0 };

		// If the polygon should be on a corner, increment the edges
		// accordingly. This is because there should be a line from the corner
		// to a point on the edge, and without this the code would break.
		if (canvas.getTopLeft().equals(this)) {
			numEdges[0]++;
			numEdges[1]++;
		}
		if (canvas.getTopRight().equals(this)) {
			numEdges[1]++;
			numEdges[2]++;
		}
		if (canvas.getBottomLeft().equals(this)) {
			numEdges[0]++;
			numEdges[3]++;
		}
		if (canvas.getBottomRight().equals(this)) {
			numEdges[2]++;
			numEdges[3]++;
		}

		// Then, go through the edges, looking for edges which touch a canvas
		// border
		for (LineBD e : edges) {
			if (e.getX1().equals(ZERO) || e.getX2().equals(ZERO)) numEdges[0]++;
			if (e.getX1().equals(canvas.getWidth()) || e.getX2().equals(canvas.getWidth())) numEdges[2]++;
			if (e.getY1().equals(ZERO) || e.getY2().equals(ZERO)) numEdges[1]++;
			if (e.getY1().equals(canvas.getHeight()) || e.getY2().equals(canvas.getHeight())) numEdges[3]++;
		}

		return numEdges;
	}

	/**
	 * Get all of the lines which are unjoined (they have at least one end
	 * without another polygon connecting to it)
	 */
	public List<PointBD> getUnjoined() {
		List<PointBD> unjoined = new ArrayList<>();

		for (LineBD e : edges) {
			boolean p1Joins = joinsWithOtherEdge(e, e.getP1());
			boolean p2Joins = joinsWithOtherEdge(e, e.getP2());

			if (!p1Joins) unjoined.add(e.getP1());
			if (!p2Joins) unjoined.add(e.getP2());
		}

		return unjoined;
	}

	/**
	 * Get all of the edges in the list that are on the border of the canvas
	 */
	public List<PointBD> getOnBorder() {
		List<PointBD> onBorder = new ArrayList<>();

		for (LineBD e : edges) {

			if (e.getX1().equals(ZERO) || e.getX1().equals(canvas.getWidth())) {
				if (!onBorder.contains(e.getP1())) onBorder.add(e.getP1());
			}
			if (e.getY1().equals(ZERO) || e.getY1().equals(canvas.getHeight())) {
				if (!onBorder.contains(e.getP1())) onBorder.add(e.getP1());
			}

			if (e.getX2().equals(ZERO) || e.getX2().equals(canvas.getWidth())) {
				if (!onBorder.contains(e.getP2())) onBorder.add(e.getP2());
			}
			if (e.getY2().equals(ZERO) || e.getY2().equals(canvas.getHeight())) {
				if (!onBorder.contains(e.getP2())) onBorder.add(e.getP2());
			}

		}

		return onBorder;
	}

	/**
	 * Get if a line joins with another edge
	 * 
	 * @param e1
	 *            the line to check for
	 * @param p
	 *            the point from the line we are checking for
	 * @return whether it joins or not
	 */
	private boolean joinsWithOtherEdge(LineBD e1, PointBD p) {
		for (LineBD e2 : edges) {
			if (!e1.equals(e2) && (e2.getP1().equals(p) || e2.getP2().equals(p))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Join a polygon with the borders of the canvas. uses the corners of the
	 * canvas to check if whole border lines need adding each corner is assigned
	 * to a polygon, and if a polygon should contain that corner, then it is
	 * joined with it else is joined with the other point on the same line
	 */
	public void joinWithBorder() {
		this.modified = true;

		// Work out which corners need to be in the polygon, based on whether a
		// corner has this polygon as its closest.
		ArrayList<PointBD> cornersToAdd = new ArrayList<>();

		if (canvas.getTopLeft().equals(this)) cornersToAdd.add(new PointBD(0, 0));
		if (canvas.getTopRight().equals(this)) cornersToAdd.add(new PointBD(canvas.getWidth().intValue(), 0));
		if (canvas.getBottomLeft().equals(this)) cornersToAdd.add(new PointBD(0, canvas.getHeight().intValue()));
		if (canvas.getBottomRight().equals(this)) cornersToAdd.add(new PointBD(canvas.getWidth().intValue(), canvas.getHeight().intValue()));

		ArrayList<LineBD> newEdges = new ArrayList<>();

		// For each of these corners, work out which point to connect it to,
		// then add this line to the polygon
		for (int i = 0; i < cornersToAdd.size(); i++) {
			for (int j = i + 1; j < cornersToAdd.size(); j++) {
				PointBD p1 = cornersToAdd.get(i);
				PointBD p2 = cornersToAdd.get(j);
				if (p1.getX().equals(p2.getX()) || p1.getY().equals(p2.getY())) {
					newEdges.add(new LineBD(p1, p2));
				}
			}
		}

		// Get all of the points which are on the border
		List<PointBD> borderPoints = getOnBorder();
		List<PointBD> bpToRemove = new ArrayList<PointBD>();

		// Compare each of these points to the other border points
		for (int i = 0; i < borderPoints.size(); i++) {
			for (int j = i + 1; j < borderPoints.size(); j++) {

				PointBD p1 = borderPoints.get(i);
				PointBD p2 = borderPoints.get(j);

				// If the two points are on the same X or same Y, create a new
				// edge between them
				if (p1.getX().equals(p2.getX()) || p1.getY().equals(p2.getY())) {
					newEdges.add(new LineBD(p1, p2));
					// Remove them from the list of border points
					bpToRemove.add(p1);
					bpToRemove.add(p2);
				}
			}
		}

		log.debug("Corners to add: {}", cornersToAdd);

		// For all of the corners that need to be added...
		for (PointBD p : cornersToAdd) {
			log.debug("Checking corner : {}", p);

			// Go through the remaining border points. If they are on the same X
			// or Y, create an edge between them
			for (int i = 0; i < borderPoints.size(); i++) {
				PointBD bp = borderPoints.get(i);
				if (p.getX().equals(bp.getX()) || p.getY().equals(bp.getY())) newEdges.add(new LineBD(p, bp));
			}
		}

		edges.addAll(newEdges);
	}

	/**
	 * Get the two points in the polygon which a line intersects with
	 * 
	 * @return A line where p1 and p2 are the two points where the line
	 *         intersects
	 */
	public LineBD getIntersections(LineBD bisection) {

		log.debug("Checking for intersections with: {}", bisection);

		// Get all of the edges that intersect with the bisection
		List<LineBD> intersecting = new ArrayList<>();
		for (LineBD e : edges) {
			if (e.intersectsLine(bisection)) {
				intersecting.add(e);
			}
		}

		log.debug("Found {} lines intersecting with bisection", intersecting.size());

		if (intersecting.size() < 2) {
			// If the bisection intersects with less than 2 lines, it means that
			// the bisection is outside the polygon
			return null;
		}
		if (intersecting.size() == 2) {
			// If the bisection intersects with exactly 2 edges, use these two
			// intersecting points.
			LineBD intersectingLine1 = intersecting.get(0);
			LineBD intersectingLine2 = intersecting.get(1);

			PointBD intersectPoint1 = bisection.getIntersectionPoint(intersectingLine1);
			PointBD intersectPoint2 = bisection.getIntersectionPoint(intersectingLine2);

			if (intersectPoint1 == null || intersectPoint2 == null) return null;
			return new LineBD(intersectPoint1, intersectPoint2);
		} else if (intersecting.size() == 3 || intersecting.size() == 4) {
			// In this case, the bisection is on one or two corners, and so we
			// need to find out which points to use.

			// Create a list of the ends of the edges without duplicates
			ArrayList<PointBD> points = new ArrayList<PointBD>();

			for (LineBD l : intersecting) {
				PointBD p1 = l.getP1();
				PointBD p2 = l.getP2();

				if (!points.contains(p1)) points.add(p1);
				if (!points.contains(p2)) points.add(p2);
			}

			// Find which of these are on the bisection
			ArrayList<PointBD> pointsOnBisection = new ArrayList<PointBD>();
			for (PointBD p : points) {
				MarginedBigDecimal dist = bisection.getDistance(p);
				if (dist.equals(ZERO)) pointsOnBisection.add(p);
			}

			if (pointsOnBisection.size() > 2) {
				throw new ArithmeticException("More than 2 polygon points on the bisection. Something is horribly wrong!");
			}
			// If both are on the bisection, use these two points
			if (pointsOnBisection.size() == 2) {
				return new LineBD(pointsOnBisection.get(0), pointsOnBisection.get(1));
			}

			// Else
			// We know that the point on the bisection must be in the
			// intersection. Ignore the lines containing this point, and find
			// the intersection with the third line

			PointBD p1 = pointsOnBisection.get(0);
			LineBD searchLine = null;
			for (LineBD l : intersecting) {
				if (!l.getP1().equals(p1) && !l.getP2().equals(p1)) {
					searchLine = l;
					break;
				}
			}

			// Get the intersection of this line and the bisection
			PointBD p2 = searchLine.getIntersectionPoint(bisection);
			return new LineBD(p1, p2);

		}

		// Should never happen!
		log.error("{}", intersecting);
		throw new ArithmeticException("Too may intersecting lines!");

	}

	/**
	 * Convert into a java polygon
	 * 
	 * @return the java polygon
	 */
	public Polygon getJavaPolygon() {
		// Return the polygon if nothing has changed since last call
		if (!modified) {
			return javaPoly;
		}

		// Create a queue of points, prioritising by angle to centre point
		Queue<PointBD> allPoints = new PriorityQueue<>(new Comparator<PointBD>() {
			@Override
			public int compare(PointBD p1, PointBD p2) {
				if (Helpers.angleBetweenPoints(centre, p2) > Helpers.angleBetweenPoints(centre, p1)) return 1;
				return -1;
			}
		});

		// Add in all the edges points (but only once)
		for (LineBD e : edges) {
			if (!allPoints.contains(e.getP1())) allPoints.add(e.getP1());
			if (!allPoints.contains(e.getP2())) allPoints.add(e.getP2());
		}

		// Create the new polygon, and add the points to it
		Polygon javaPolygon = new Polygon();
		while (!allPoints.isEmpty()) {
			PointBD p = allPoints.poll();

			// log.debug("Adding this point to the polygon: {}", p);

			javaPolygon.addPoint((int) p.getXVal(), (int) p.getYVal());
		}

		// Set global polygon and if it's changed to false
		this.javaPoly = javaPolygon;
		this.modified = false;

		return javaPolygon;
	}

	public void notifyModified() {
		this.modified = true;
	}

	public PointBD getCentre() {
		return centre;
	}

	/**
	 * Are two VoronoiPolygons equal?
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof VoronoiPolygon)) {
			return false;
		}

		VoronoiPolygon poly = (VoronoiPolygon) obj;

		if (!centre.equals(poly.centre)) return false;

		if (edges.size() != poly.edges.size()) return false;

		for (int i = 0; i < edges.size(); i++) {
			if (!edges.get(i).equals(poly.edges.get(i))) return false;
		}

		return true;
	}

	/**
	 * Convert the VoronoiPolygon to a string Includes the centre, the edges,
	 * and which corners it contains
	 */
	public String toString() {
		String toString = "Polygon\n" + "centre: " + centre + "\nEdges:\n";
		for (LineBD edge : edges) {
			toString += edge;
			toString += ("\n");
		}

		toString += "Corners:";
		if (canvas.getTopLeft().equals(this)) toString += "\tTop Left";
		if (canvas.getTopRight().equals(this)) toString += "\tTop Right";
		if (canvas.getBottomLeft().equals(this)) toString += "\tBottom Left";
		if (canvas.getBottomRight().equals(this)) toString += "\tBottom Right";

		return (toString);
	}

}
