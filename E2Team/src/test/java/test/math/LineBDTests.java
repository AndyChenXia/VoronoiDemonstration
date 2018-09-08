package test.math;

import static org.junit.Assert.assertTrue;
import model.math.LineBD;
import model.math.PointBD;

import org.junit.Test;

/**
 * JUnit tests for LineBD
 * @author thh489
 *
 */
public class LineBDTests {

	@Test
	public void testLineIntersection() {
		PointBD p1 = new PointBD(100, 100);
		PointBD p2 = new PointBD(200, 100);
		PointBD p3 = new PointBD(100, 200);
		PointBD p4 = new PointBD(200, 200);

		LineBD line_1_2 = new LineBD(p1, p2);
		LineBD line_2_3 = new LineBD(p2, p3);
		LineBD line_3_4 = new LineBD(p3, p4);
		LineBD line_1_4 = new LineBD(p1, p4);
		LineBD line_1_3 = new LineBD(p1, p3);
		LineBD line_2_4 = new LineBD(p2, p4);

		assertTrue(line_1_2.getIntersectionPoint(line_2_3).equals(p2));
		assertTrue(line_3_4.getIntersectionPoint(line_1_4).equals(p4));
		assertTrue(line_1_3.getIntersectionPoint(line_3_4).equals(p3));
		assertTrue(line_1_2.getIntersectionPoint(line_1_4).equals(p1));
		assertTrue(line_2_4.getIntersectionPoint(line_2_3).equals(p2));
	}
	
}
