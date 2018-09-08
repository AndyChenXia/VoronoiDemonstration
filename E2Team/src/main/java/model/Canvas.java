package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Holder;
import gui.CanvasKeyListener;
import gui.CanvasPanel;
import model.math.LineBD;
import model.math.MarginedBigDecimal;
import model.math.PointBD;

/**
 * The canvas contains the points, and handles adding points, and finding
 * polygons to be trimmed.
 * 
 * @author mcs448, mxw449, std487, thh489
 *
 */
public class Canvas extends Observable {

	private static final Logger log = LoggerFactory.getLogger(Canvas.class);

	public enum VoronoiState {
		STARTING, ADD_POINT_START, GETTING_BISECTION, TRIMMING_BEFORE, TRIMMING_AFTER, DONE_WITHOUT_BORDER, JOINING_WITH_BORDER, DONE
	}

	/** Current instance of the Holder class */
	private final Holder holder;

	/** Variables use to store details about the current state */
	private VoronoiState currentState;
	private LineBD stateBisectionLine;
	private VoronoiPolygon stateTrimmingPolygon;
	private PointBD stateAddingPoint;
	private VoronoiPolygon stateAddingPolygon;

	/** Are we in manual addition mode? */
	private boolean isManual;

	/** List of all the polygons on the canvas */
	private List<VoronoiPolygon> polygons = new CopyOnWriteArrayList<>();

	private int numNodes;
	private Random gen;
	private String dotDistrib;

	/** Variables used to keep track of the polygon closest to each corner */
	private VoronoiPolygon topLeft, topRight, bottomLeft, bottomRIght;
	/** Location of each of the corners */
	private PointBD[] corners = new PointBD[4];

	private MarginedBigDecimal width, height;

	/**
	 * Is the class being called from GUITest? This allows these tests to run
	 * quicker by not actually adding the points
	 */
	private boolean testing;

	/**
	 * Takes as parameters the size of the canvas, as well as a holder object
	 */
	public Canvas(int width, int height, Holder holder) {
		testing = false;
		this.width = new MarginedBigDecimal(width);
		this.height = new MarginedBigDecimal(height);
		this.holder = holder;

		polygons = new ArrayList<>();

		gen = new Random();
		isManual = false;
		corners = new PointBD[] { new PointBD(0, 0), new PointBD(width, 0), new PointBD(0, height), new PointBD(width, height) };

		currentState = VoronoiState.STARTING;
	}

	/**
	 * Add a new point to the canvas
	 */
	public synchronized void addPoint(PointBD newPoint) {

		// If the point has already been added, don't add it again
		if (hasPolygon(newPoint)) {
			return;
		}

		// Update the node count in the GUI
		CanvasPanel.nodeCount++;
		CanvasKeyListener.nodeCount++;

		// Update the state
		this.stateAddingPoint = newPoint;
		updateState(VoronoiState.ADD_POINT_START);

		// If this is the first point, instead run the addFirstPoint method
		if (polygons.size() == 0) {
			addFirstPoint(newPoint);
			return;
		}

		// The new polygon to be created
		VoronoiPolygon newPolygon = new VoronoiPolygon(newPoint, this);
		this.stateAddingPolygon = newPolygon;

		// Calculate whether each corner now has newPoint as its closest.
		if (newPolygon.getCentre().getDistanceSqrd(corners[0]).compareTo(getTopLeft().getCentre().getDistanceSqrd(corners[0])) < 0) setTopLeft(newPolygon);
		if (newPolygon.getCentre().getDistanceSqrd(corners[1]).compareTo(getTopRight().getCentre().getDistanceSqrd(corners[1])) < 0) setTopRight(newPolygon);
		if (newPolygon.getCentre().getDistanceSqrd(corners[2]).compareTo(getBottomLeft().getCentre().getDistanceSqrd(corners[2])) < 0) setBottomLeft(newPolygon);
		if (newPolygon.getCentre().getDistanceSqrd(corners[3]).compareTo(getBottomRight().getCentre().getDistanceSqrd(corners[3])) < 0) setBottomRight(newPolygon);

		// Create a heap of all the polygons, sorted by closest to current point
		Queue<VoronoiPolygon> toAddQueue = createClosestQueue(newPoint);

		// Add bisectors and trim neighbours until the polygon is complete or
		// has run out of neighbours
		while (!newPolygon.isCompleteWithoutEdges() && !toAddQueue.isEmpty()) {
			VoronoiPolygon currentPolygon = toAddQueue.poll();
			log.debug("\n\nConsidering {} in add polygon", currentPolygon);

			// Get the bisector between newPoint and the current polygon, then
			// get the intersection line
			LineBD bisection = currentPolygon.getBisection(newPoint);
			log.debug("Bisection: {}", bisection);
			LineBD intersectingLine = currentPolygon.getIntersections(bisection);
			log.debug("Intersecting line: {}", intersectingLine);

			// If there isn't an intersecting line, don't attempt to trim
			if (intersectingLine == null) continue;

			this.stateBisectionLine = intersectingLine;
			this.updateState(VoronoiState.GETTING_BISECTION);

			this.stateTrimmingPolygon = currentPolygon;
			this.updateState(VoronoiState.TRIMMING_BEFORE);

			// Trim neighbour
			currentPolygon.trim(intersectingLine);
			currentPolygon.notifyModified();
			this.updateState(VoronoiState.TRIMMING_AFTER);

			// Add the bisector to this polygon
			newPolygon.addEdge(intersectingLine);

			log.debug("At the end of adding that polygon, we have: {}", newPolygon);
		}

		this.updateState(VoronoiState.DONE_WITHOUT_BORDER);

		// Connect the polygon to the border if required
		newPolygon.joinWithBorder();
		this.updateState(VoronoiState.JOINING_WITH_BORDER);

		log.info("Final polygon: {}\n\n\n\n", newPolygon);
		this.polygons.add(newPolygon);

		this.stateAddingPolygon = null;

		this.updateState(VoronoiState.DONE);
	}

	/**
	 * Check whether a point is already on the canvas
	 */
	private boolean hasPolygon(PointBD newPoint) {
		for (int i = 0; i < polygons.size(); i++) {
			if (polygons.get(i).getCentre().equals(newPoint)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If this point is the first to be added, simply create a new polygon that
	 * covers the entire canvas.
	 */
	private void addFirstPoint(PointBD p) {

		VoronoiPolygon poly = new VoronoiPolygon(p, this);
		poly.addEdge(new LineBD(width, MarginedBigDecimal.ZERO, width, height));
		poly.addEdge(new LineBD(MarginedBigDecimal.ZERO, MarginedBigDecimal.ZERO, width, MarginedBigDecimal.ZERO));
		poly.addEdge(new LineBD(MarginedBigDecimal.ZERO, MarginedBigDecimal.ZERO, MarginedBigDecimal.ZERO, height));
		poly.addEdge(new LineBD(MarginedBigDecimal.ZERO, height, width, height));

		polygons.add(poly);

		setTopLeft(poly);
		setTopRight(poly);
		setBottomLeft(poly);
		setBottomRight(poly);

	}

	/**
	 * Create a new queue of polygons based on how close they are to a PointBD,
	 * p. Index 0 should be the closest, Index 1 the second closest etc.
	 */
	private Queue<VoronoiPolygon> createClosestQueue(final PointBD p) {
		Queue<VoronoiPolygon> queue = new PriorityQueue<>(new Comparator<VoronoiPolygon>() {
			@Override
			public int compare(VoronoiPolygon p1, VoronoiPolygon p2) {
				return (int) p.getDistanceSqrd(p1.getCentre()).subtract(p.getDistanceSqrd(p2.getCentre())).value();
			}
		});

		queue.addAll(this.polygons);

		return queue;
	}

	/**
	 * Fill the canvas with random points
	 * 
	 * @param amount
	 *            amount of points
	 * @param distribution
	 *            the distribution of points to be added
	 */
	public void fillCanvasWithPoints(int amount, String distribution) {
		dotDistrib = distribution;
		numNodes = amount;
		switch (distribution) {
		case "random":
			fillWithRandom(amount);
			break;
		case "hex":
			fillWithHexPoints(amount);
			break;
		case "square":
			fillWithSquarePoints(amount);
			break;
		}
	}

	/**
	 * Fill the canvas with a random distribution of points
	 */
	private void fillWithRandom(int amount) {
		if (testing) return;

		for (int i = 0; i < amount; i++) {
			addPoint(new PointBD(gen.nextInt(width.intValue()), gen.nextInt(height.intValue())));
		}
	}

	/**
	 * Fill the canvas with a set of points in a hex distribution
	 */
	private void fillWithHexPoints(int amount) {
		if (testing) return;

		int w = width.intValue();
		int h = height.intValue();
		int y = 0;
		ArrayList<PointBD> hexPoints = new ArrayList<>();
		if (amount < 26) {
			for (int i = 0; i < amount; i++) {
				if (y % 2 == 0) {
					hexPoints.add(new PointBD((w / 20) + (w / 5) * (i % 5), (h / 10) + (y * h / 5)));
				} else {
					hexPoints.add(new PointBD((w / 20) + (w / 10) + (w / 5) * (i % 5), (h / 10) + (y * h / 5)));
				}
				if (i % 5 == 4) y = y + 1;
			}
			for (int i = 0; i < amount; i++) {
				int randomNext = gen.nextInt(hexPoints.size());
				addPoint(hexPoints.get(randomNext));
				hexPoints.remove(randomNext);
			}
		} else {
			for (int i = 0; i < amount; i++) {
				if (y % 2 == 0) {
					hexPoints.add(new PointBD((w / 40) + (w / 10) * (i % 10), (h / 10) + (y * h / 5)));
				} else {
					hexPoints.add(new PointBD((w / 40) + (w / 20) + (w / 10) * (i % 10), (h / 10) + (y * h / 5)));
				}
				if (i % 10 == 9) y = y + 1;
			}
			for (int i = 0; i < amount; i++) {
				int randomNext = gen.nextInt(hexPoints.size());
				addPoint(hexPoints.get(randomNext));
				hexPoints.remove(randomNext);
			}
		}
	}

	/**
	 * Fill the canvas with a set of points in a square distribution
	 */
	private void fillWithSquarePoints(int amount) {
		if (testing) return;

		int w = width.intValue();
		int h = height.intValue();
		int y = 0;

		ArrayList<PointBD> squarePoints = new ArrayList<>();
		if (amount < 26) {
			for (int i = 0; i < amount; i++) {
				squarePoints.add(new PointBD((w / 10) + (w / 5) * (i % 5), (h / 10) + (y * h / 5)));
				if (i % 5 == 4) y = y + 1;
			}
			for (int i = 0; i < amount; i++) {
				int randomNext = gen.nextInt(squarePoints.size());
				addPoint(squarePoints.get(randomNext));
				squarePoints.remove(randomNext);
			}
		} else {
			for (int i = 0; i < amount; i++) {
				squarePoints.add(new PointBD((w / 20) + (w / 10) * (i % 10), (h / 10) + (y * h / 5)));
				if (i % 10 == 9) y = y + 1;
			}
			for (int i = 0; i < amount; i++) {
				int randomNext = gen.nextInt(squarePoints.size());
				addPoint(squarePoints.get(randomNext));
				squarePoints.remove(randomNext);
			}
		}
	}

	/**
	 * Update the state of the canvas - has potential to hold until next called.
	 * 
	 * @param newState
	 *            the state to switch to
	 */
	private void updateState(VoronoiState newState) {
		log.info("Changing state to {}", newState);

		this.currentState = newState;

		setChanged();
		notifyObservers();
		if (testing) return;
		if (!isManual) this.holder.hold();

	}

	// ---- GETTERS AND SETTERS ----

	public List<VoronoiPolygon> getPolygons() {
		return polygons;
	}

	public VoronoiState getState() {
		return currentState;
	}

	public VoronoiPolygon getStateTrimmingPolygon() {
		return stateTrimmingPolygon;
	}

	public LineBD getStateBisectionLine() {
		return stateBisectionLine;
	}

	public MarginedBigDecimal getWidth() {
		return width;
	}

	public MarginedBigDecimal getHeight() {
		return height;
	}

	public PointBD getStateAddingPoint() {
		return stateAddingPoint;
	}

	public VoronoiPolygon getStateAddingPolygon() {
		return stateAddingPolygon;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public VoronoiPolygon getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(VoronoiPolygon p) {
		topLeft = p;
	}

	public VoronoiPolygon getTopRight() {
		return topRight;
	}

	public void setTopRight(VoronoiPolygon p) {
		topRight = p;
	}

	public VoronoiPolygon getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(VoronoiPolygon p) {
		bottomLeft = p;
	}

	public VoronoiPolygon getBottomRight() {
		return bottomRIght;
	}

	public void setBottomRight(VoronoiPolygon p) {
		bottomRIght = p;
	}

	public String getDistrib() {
		return dotDistrib;
	}

	public boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(boolean n) {
		isManual = n;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}
}
