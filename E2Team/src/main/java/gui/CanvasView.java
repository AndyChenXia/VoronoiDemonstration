package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.CanvasController;
import model.math.LineBD;
import model.math.PointBD;
import model.VoronoiPolygon;

public class CanvasView extends JComponent implements Observer {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CanvasView.class);

	/**
	 * Stores the color of each polygon
	 */
	private Map<VoronoiPolygon, Color> colorMap = new HashMap<>();

	/**
	 * Colours for general aspects
	 */
	private static final Color BACKGROUND_COLOR = Color.WHITE, BISECTION_COLOR = Color.BLACK, TRIMMING_COLOR = new Color(7, 16, 32, 100), ADDING_POINT_COLOR = Color.BLACK, ADDING_POLYGON_COLOR = Color.PINK;

	/**
	 * Radius of the points
	 */
	private static final int POINT_RADIUS = 10;

	/**
	 * Stroke used for drawing lines
	 */
	private static final BasicStroke stroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	/**
	 * Canvas of polygons
	 */
	private final CanvasController controller;

	/**
	 * Initialise with a controller
	 * 
	 * @param _controller
	 *            the controller of the canvas
	 */
	public CanvasView(CanvasController _controller) {
		this.controller = _controller;
		this.controller.setDisplay(this);
	}

	/**
	 * Paint the display
	 */
	public void paint(Graphics g) {
		log.info("Canvas state is {}", controller.getState());

		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, controller.getWidth(), controller.getHeight());

		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawAllPolygons(g2d);
		drawAllPoints(g2d);
		drawAllPolygonOutlines(g2d);

		drawStateDependentObjects(g2d);
	}

	/**
	 * Draw every polygon
	 */
	private void drawAllPolygons(Graphics2D g) {
		List<VoronoiPolygon> polygons = this.controller.getPolygons();
		for (VoronoiPolygon p : polygons) {
			g.setColor(getPolygonColor(p));
			drawPolygon(p, g);
		}
	}

	/**
	 * Draw every polygon outline
	 */
	private void drawAllPolygonOutlines(Graphics2D g) {
		g.setStroke(stroke);

		List<VoronoiPolygon> polygons = this.controller.getPolygons();
		for (VoronoiPolygon p : polygons) {
			g.setColor(getSecondaryPolygonColor(p));
			drawPolygonOutline(p, g);
		}
	}

	/**
	 * Draw all centre points
	 */
	private void drawAllPoints(Graphics2D g) {
		for (VoronoiPolygon p : this.controller.getPolygons()) {
			g.setColor(getSecondaryPolygonColor(p));
			drawPoint(g, p.getCentre());
		}
	}

	/**
	 * Draw a polygon
	 */
	public void drawPolygon(VoronoiPolygon p, Graphics g) {
		Polygon javaPolygon = p.getJavaPolygon();
		g.fillPolygon(javaPolygon);
	}

	/**
	 * Draw a polygon outline
	 */
	public void drawPolygonOutline(VoronoiPolygon p, Graphics g) {
		for (LineBD l : p.getEdges()) {
			g.drawLine(l.getX1().intValue(), l.getY1().intValue(), l.getX2().intValue(), l.getY2().intValue());
		}
	}

	/**
	 * Draw a point
	 */
	private void drawPoint(Graphics g, PointBD p) {
		g.fillOval((int) p.getXVal() - (POINT_RADIUS / 2), (int) p.getYVal() - (POINT_RADIUS / 2), POINT_RADIUS, POINT_RADIUS);
	}

	/**
	 * Draw the objects depending on the state
	 */
	@SuppressWarnings("incomplete-switch")
	private void drawStateDependentObjects(Graphics2D g) {
		switch (controller.getState()) {
		case GETTING_BISECTION:
			g.setStroke(stroke);
			g.setColor(BISECTION_COLOR);
			LineBD line = controller.getStateBisectionLine();
			g.drawLine((int) line.getP1().getXVal(), (int) line.getP1().getYVal(), (int) line.getP2().getXVal(), (int) line.getP2().getYVal());
			break;
		case TRIMMING_BEFORE:
		case TRIMMING_AFTER:
			g.setColor(TRIMMING_COLOR);
			g.fillPolygon(controller.getStateTrimmingPolygon().getJavaPolygon());
			break;
		}

		drawCurrentAddingObjects(g);
	}

	/**
	 * Draw the current objects being added
	 */
	private void drawCurrentAddingObjects(Graphics g) {
		if (controller.getStateAddingPoint() != null) {
			g.setColor(ADDING_POINT_COLOR);
			drawPoint(g, controller.getStateAddingPoint());
		}

		if (controller.getStateAddingPolygon() != null) {
			g.setColor(ADDING_POLYGON_COLOR);
			drawPolygonOutline(controller.getStateAddingPolygon(), g);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		log.info("Updating because {} changed", o);
		repaint();
	}

	/**
	 * @param polygon
	 *            polygon to get a color for
	 * @return the color for the polygon
	 */
	private Color getPolygonColor(VoronoiPolygon polygon) {
		if (colorMap.containsKey(polygon)) {
			return colorMap.get(polygon);
		} else {
			Color newColor = getRandomColor();
			colorMap.put(polygon, newColor);
			return newColor;
		}
	}

	/**
	 * @param polygon
	 *            polygon to get a secondary color for
	 * @return the secondary color for the polygon
	 */
	private Color getSecondaryPolygonColor(VoronoiPolygon polygon) {
		return getPolygonColor(polygon).darker();
	}

	/**
	 * @return a random pastel color
	 */
	private Color getRandomColor() {
		int r = 135 + (int) (Math.random() * 120);
		int g = 135 + (int) (Math.random() * 120);
		int b = 135 + (int) (Math.random() * 120);
		return new Color(r, g, b);
	}

	
	/**
	 * getting canvas controller
	 * @return canvas controller
	 */
	public CanvasController getController() {
		return controller;
	}
}
