package test;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.math.LineBD;
import model.math.PointBD;
import model.Canvas;
import model.VoronoiPolygon;

/**
 * Some helper functions for use in tests
 * 
 * @author thh489
 */
public class TestHelpers {

	/**
	 * Convert a java.awt.geom.Polygon object to a string
	 */
	public static String polyToString(Polygon poly) {
		String ret = "Polygon: {";

		for (int i = 0; i < poly.npoints; i++) {
			int x = poly.xpoints[i];
			int y = poly.ypoints[i];
			ret += "(" + x + ", " + y + ") ";
		}

		return ret + "}";
	}

	/**
	 * Check equality for two Polygons
	 */
	public static boolean polyEqual(Polygon poly1, Polygon poly2) {

		// Do they have the same number of points?
		if (poly1.npoints != poly2.npoints) return false;

		ArrayList<Point2D> points1 = new ArrayList<Point2D>();
		ArrayList<Point2D> points2 = new ArrayList<Point2D>();

		// Add all of the points into an ArrayList for each polygon
		for (int i = 0; i < poly1.npoints; i++) {
			int x1 = poly1.xpoints[i];
			int y1 = poly1.ypoints[i];
			points1.add(new Point2D.Double(x1, y1));

			int x2 = poly2.xpoints[i];
			int y2 = poly2.ypoints[i];
			points2.add(new Point2D.Double(x2, y2));
		}

		// Remove all of the points in poly1 from poly2
		for (int i = 0; i < points1.size(); i++) {
			// If too many have been removed, they aren't the same
			if (points2.size() == 0) return false;
			
			points2.remove(points1.get(i));
		}

		return points2.size() == 0;
	}

	/**
	 * Get a square VoronoiPolygon. The polygon is 100x100, going from (100,100) -> (200,200)
	 */
	public static VoronoiPolygon getSquarePolygon(Canvas canvas) {
		PointBD p1 = new PointBD(100, 100);
		PointBD p2 = new PointBD(200, 100);
		PointBD p3 = new PointBD(200, 200);
		PointBD p4 = new PointBD(100, 200);

		VoronoiPolygon polygon = new VoronoiPolygon(new PointBD(150, 150), canvas);
		polygon.addEdge(new LineBD(p1, p2));
		polygon.addEdge(new LineBD(p2, p3));
		polygon.addEdge(new LineBD(p3, p4));
		polygon.addEdge(new LineBD(p4, p1));

		return polygon;
	}

	/**
	 * Get a voronoi polygon that fills a whole canvas
	 */
	public static VoronoiPolygon getFillerPolygon(Canvas canvas) {
		int width = canvas.getWidth().intValue();
		int height = canvas.getHeight().intValue();
		VoronoiPolygon poly = new VoronoiPolygon(new PointBD(width / 2, height / 2), canvas);

		PointBD p1 = new PointBD(0, 0);
		PointBD p2 = new PointBD(width, 0);
		PointBD p3 = new PointBD(width, height);
		PointBD p4 = new PointBD(0, height);

		poly.addEdge(new LineBD(p1, p2));
		poly.addEdge(new LineBD(p2, p3));
		poly.addEdge(new LineBD(p3, p4));
		poly.addEdge(new LineBD(p4, p1));

		return poly;

	}

}
