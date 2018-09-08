package test.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static test.TestHelpers.getFillerPolygon;
import static test.TestHelpers.getSquarePolygon;
import static test.TestHelpers.polyEqual;

import java.awt.Polygon;
import java.util.List;

import model.Canvas;
import model.VoronoiPolygon;
import model.math.LineBD;
import model.math.PointBD;

import org.junit.Before;
import org.junit.Test;

import controller.Holder;

/**
 * JUnit testing for VoronoiPolygon
 * @author mxw449, std487, thh489
 */
public class VoronoiPolygonTests {

	private Canvas canvas_800x600, canvas_200x200;

	/**
	 * Setup the two canvas objects. Gets called before every test, so will
	 * always have a fresh canvas
	 */
	@Before
	public void resetCanvases() {
		canvas_800x600 = new Canvas(800, 600, new Holder());
		canvas_200x200 = new Canvas(200, 200, new Holder());
	}

	@Test
	public void testRemoveDuplicateEdges() {
		VoronoiPolygon poly = getSquarePolygon(canvas_200x200);
		poly.addEdge(new LineBD(new PointBD(100, 100), new PointBD(100, 200)));

		assertTrue(poly.getEdges().size() == 5);

		poly.removeDuplicateEdges();
		assertTrue(poly.getEdges().size() == 4);

	}

	@Test
	public void testGetBisection() {
		resetCanvases();
		PointBD p1 = new PointBD(100, 100);
		PointBD p2 = new PointBD(200, 100);
		PointBD p3 = new PointBD(200, 200);
		PointBD p4 = new PointBD(100, 200);

		VoronoiPolygon vpoly = new VoronoiPolygon(new PointBD(150, 150), canvas_800x600);
		vpoly.addEdge(new LineBD(p1, p2));
		vpoly.addEdge(new LineBD(p2, p3));
		vpoly.addEdge(new LineBD(p3, p4));
		vpoly.addEdge(new LineBD(p4, p1));

		// --- Test vertically ---
		PointBD point = new PointBD(150, 70);
		LineBD bisector = new LineBD(0, 110, 800, 110);
		LineBD retBisector = vpoly.getBisection(point);
		assertTrue(bisector.equals(retBisector));

		// --- Test horizontally ---
		point = new PointBD(70, 150);
		bisector = new LineBD(110, 0, 110, 600);
		retBisector = vpoly.getBisection(point);
		assertTrue(bisector.equals(retBisector));

		// --- Test diagonally ---

		vpoly = new VoronoiPolygon(new PointBD(150, 150), canvas_200x200);
		vpoly.addEdge(new LineBD(p1, p2));
		vpoly.addEdge(new LineBD(p2, p3));
		vpoly.addEdge(new LineBD(p3, p4));
		vpoly.addEdge(new LineBD(p4, p1));

		point = new PointBD(50, 50);
		bisector = new LineBD(200, 0, 0, 200);
		retBisector = vpoly.getBisection(point);
		assertTrue(bisector.equals(retBisector));

	}

	@Test
	public void testTrim() {
		VoronoiPolygon polygon = getSquarePolygon(canvas_200x200);
		LineBD trimLine = new LineBD(150, 100, 180, 150);
		LineBD trimLine2 = new LineBD(180, 100, 160, 200);
		polygon.trim(trimLine);
		polygon.trim(trimLine2);

		boolean trimmedLineFound = false;
		for (LineBD e : polygon.getEdges()) {
			if ((e.getP1().equals(trimLine.getP1()) && e.getP2().equals(trimLine.getP2())) || (e.getP2().equals(trimLine.getP1()) && e.getP2().equals(trimLine.getP1()))) {
				trimmedLineFound = true;
				break;
			}
		}

		assertTrue(trimmedLineFound);
	}

	@Test
	public void testIsCompleteWithoutEdges() {
		PointBD p1 = new PointBD(10, 10);
		PointBD p2 = new PointBD(10, 100);
		PointBD p3 = new PointBD(100, 0);

		VoronoiPolygon polygon = new VoronoiPolygon(p1, canvas_200x200);
		polygon.addEdge(new LineBD(p1, p2));
		polygon.addEdge(new LineBD(p2, p3));
		polygon.addEdge(new LineBD(p3, p1));

		canvas_200x200.setTopLeft(polygon);
		canvas_200x200.setTopRight(polygon);
		canvas_200x200.setBottomLeft(polygon);
		canvas_200x200.setBottomRight(polygon);

		boolean complete = polygon.isCompleteWithoutEdges();
		assertTrue(complete);

		polygon = new VoronoiPolygon(p1, canvas_200x200);
		polygon.addEdge(new LineBD(p1, p2));
		polygon.addEdge(new LineBD(p2, p3));

		canvas_200x200.setTopLeft(polygon);
		canvas_200x200.setTopRight(polygon);
		canvas_200x200.setBottomLeft(polygon);
		canvas_200x200.setBottomRight(polygon);
		complete = polygon.isCompleteWithoutEdges();

		assertTrue(!complete);

	}

	@Test
	public void testGetUnjoined() {
		VoronoiPolygon poly = new VoronoiPolygon(new PointBD(100, 100), canvas_800x600);

		PointBD p1 = new PointBD(200, 0);
		PointBD p2 = new PointBD(100, 0);
		PointBD p3 = new PointBD(0, 100);
		PointBD p4 = new PointBD(0, 200);
		PointBD p5 = new PointBD(200, 100);

		poly.addEdge(new LineBD(p1, p2));
		poly.addEdge(new LineBD(p2, p3));
		poly.addEdge(new LineBD(p3, p4));
		poly.addEdge(new LineBD(p4, p5));

		List<PointBD> unjoined = poly.getUnjoined();
		assertTrue(unjoined.size() == 2);
		assertTrue(unjoined.contains(p1));
		assertTrue(unjoined.contains(p5));

	}

	@Test
	public void testGetOnBorder() {
		VoronoiPolygon poly = new VoronoiPolygon(new PointBD(200, 200), canvas_800x600);
		PointBD p1 = new PointBD(0, 20);
		PointBD p2 = new PointBD(800, 20);
		PointBD p3 = new PointBD(700, 400);
		PointBD p4 = new PointBD(0, 400);

		poly.addEdge(new LineBD(p1, p2));
		poly.addEdge(new LineBD(p2, p3));
		poly.addEdge(new LineBD(p3, p4));
		poly.addEdge(new LineBD(p4, p1));

		List<PointBD> border = poly.getOnBorder();
		assertEquals(border.size(), 3);
		assertTrue(border.contains(p1));
		assertTrue(border.contains(p2));
		assertTrue(border.contains(p4));
		
	}


	@Test
	public void testJoinWithBorder() {
		VoronoiPolygon fillerPolygon = getFillerPolygon(canvas_800x600);

		canvas_800x600.setTopRight(fillerPolygon);
		canvas_800x600.setBottomLeft(fillerPolygon);
		canvas_800x600.setBottomRight(fillerPolygon);

		VoronoiPolygon polygon = new VoronoiPolygon(new PointBD(50, 50), canvas_800x600);
		polygon.addEdge(new LineBD(new PointBD(0, 100), new PointBD(100, 0)));

		canvas_800x600.setTopLeft(polygon);
		polygon.joinWithBorder();

		LineBD l1 = new LineBD(new PointBD(0, 0), new PointBD(0, 100));
		LineBD l2 = new LineBD(new PointBD(0, 0), new PointBD(100, 0));

		assertTrue(polygon.getEdges().contains(l1));
		assertTrue(polygon.getEdges().contains(l2));
	}

	@Test
	public void testGetIntersections() {
		VoronoiPolygon poly = getSquarePolygon(canvas_800x600);
		
		PointBD hP1 = new PointBD(100, 150);
		PointBD hP2 = new PointBD(200, 150);
		LineBD horizLine = new LineBD(hP1, hP2);
		LineBD horizBisection = poly.getIntersections(horizLine);
		assertEquals(horizBisection, horizLine);
		
		PointBD vP1 = new PointBD(150, 100);
		PointBD vP2 = new PointBD(150, 200);
		LineBD vertLine = new LineBD(vP1, vP2);
		LineBD vertBisection = poly.getIntersections(vertLine);
		assertEquals(vertBisection, vertLine);
		
		PointBD dP1 = new PointBD(100, 100);
		PointBD dP2 = new PointBD(200, 200);
		LineBD diagLine = new LineBD(dP1, dP2);
		LineBD diagBisection = poly.getIntersections(diagLine);
		assertEquals(diagBisection, diagLine);		
	}
	
	@Test
	public void testGetIntersectionsEdge() {
		LineBD l1 = new LineBD(new PointBD(100, 100), new PointBD(200, 100));
		LineBD l2 = new LineBD(new PointBD(200, 100), new PointBD(200, 200));
		LineBD l3 = new LineBD(new PointBD(200, 200), new PointBD(100, 200));
		LineBD l4 = new LineBD(new PointBD(100, 200), new PointBD(100, 100));
		
		VoronoiPolygon poly = new VoronoiPolygon(new PointBD(150, 150), canvas_200x200);
		poly.addEdge(l1);
		poly.addEdge(l2);
		poly.addEdge(l3);
		poly.addEdge(l4);
		
		assertEquals(poly.getIntersections(l1), l1);
		assertEquals(poly.getIntersections(l2), l2);
		assertEquals(poly.getIntersections(l3), l3);
		assertEquals(poly.getIntersections(l4), l4);
		
		
		//assertEquals(poly.getIntersections(bisection))
		
	}

	@Test
	public void testToJavaPolygonSquare() {
		PointBD p1 = new PointBD(100, 100);
		PointBD p2 = new PointBD(200, 100);
		PointBD p3 = new PointBD(200, 200);
		PointBD p4 = new PointBD(100, 200);

		VoronoiPolygon vpoly = new VoronoiPolygon(new PointBD(150, 150), canvas_200x200);
		vpoly.addEdge(new LineBD(p1, p2));
		vpoly.addEdge(new LineBD(p2, p3));
		vpoly.addEdge(new LineBD(p3, p4));
		vpoly.addEdge(new LineBD(p4, p1));

		Polygon poly = new Polygon();
		poly.addPoint((int) p1.getXVal(), (int) p1.getYVal());
		poly.addPoint((int) p2.getXVal(), (int) p2.getYVal());
		poly.addPoint((int) p3.getXVal(), (int) p3.getYVal());
		poly.addPoint((int) p4.getXVal(), (int) p4.getYVal());

		Polygon converted = vpoly.getJavaPolygon();

		assertTrue(polyEqual(poly, converted));
	}

	@Test
	public void testToJavaPolygonComplex() {
		PointBD p1 = new PointBD(200, 200);
		PointBD p2 = new PointBD(250, 150);
		PointBD p3 = new PointBD(300, 250);
		PointBD p4 = new PointBD(250, 400);
		PointBD p5 = new PointBD(220, 300);

		VoronoiPolygon vpoly = new VoronoiPolygon(new PointBD(250, 250), canvas_200x200);
		vpoly.addEdge(new LineBD(p1, p2));
		vpoly.addEdge(new LineBD(p2, p3));
		vpoly.addEdge(new LineBD(p3, p4));
		vpoly.addEdge(new LineBD(p4, p5));
		vpoly.addEdge(new LineBD(p5, p1));

		Polygon poly = new Polygon();
		poly.addPoint((int) p1.getXVal(), (int) p1.getYVal());
		poly.addPoint((int) p2.getXVal(), (int) p2.getYVal());
		poly.addPoint((int) p3.getXVal(), (int) p3.getYVal());
		poly.addPoint((int) p4.getXVal(), (int) p4.getYVal());
		poly.addPoint((int) p5.getXVal(), (int) p5.getYVal());

		Polygon converted = vpoly.getJavaPolygon();

		assertTrue(polyEqual(poly, converted));
	}

}
