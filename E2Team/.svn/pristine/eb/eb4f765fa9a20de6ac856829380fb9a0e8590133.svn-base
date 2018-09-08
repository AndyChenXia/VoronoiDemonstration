package controller;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import gui.CanvasView;
import model.math.LineBD;
import model.math.PointBD;
import model.Canvas;
import model.VoronoiPolygon;

/**
 * Controller class for the canvas. Handles hold/next for the Holder class.
 * Transfers the state-related objects between the Canvas and the display.
 * Handles updating and repainting the display
 * @author mxw449
 */
public class CanvasController implements Observer {

	/**
	 * The canvas that performs Voronoi calculations
	 */
	private final Canvas canvas;

	/**
	 * The holder to stop canvas from progressing
	 */
	private final Holder holder;

	/**
	 * The display of the canvas
	 */
	private CanvasView display;

	public CanvasController(Canvas _canvas, Holder _holder) {
		this.canvas = _canvas;
		this.holder = _holder;
	}

	/**
	 * Progress to the next step in Voronoi generation
	 */
	public void nextVoronoiStep() {
		holder.next();
	}

	public List<VoronoiPolygon> getPolygons() {
		return canvas.getPolygons();
	}

	public int getWidth() {
		return canvas.getWidth().intValue();
	}

	public int getHeight() {
		return canvas.getHeight().intValue();
	}

	public Canvas.VoronoiState getState() {
		return canvas.getState();
	}

	public boolean getIsManual() {
		return canvas.getIsManual();
	}

	public void setIsManual(boolean n) {
		canvas.setIsManual(n);
	}

	public LineBD getStateBisectionLine() {
		return canvas.getStateBisectionLine();
	}

	public VoronoiPolygon getStateTrimmingPolygon() {
		return canvas.getStateTrimmingPolygon();
	}

	public PointBD getStateAddingPoint() {
		return canvas.getStateAddingPoint();
	}

	public VoronoiPolygon getStateAddingPolygon() {
		return canvas.getStateAddingPolygon();
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setDisplay(CanvasView _display) {
		this.display = _display;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (display != null) {
			display.repaint();
		}
	}
}
