package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.CanvasController;
import gui.information.PromptHandler;
import model.Canvas;
import model.Canvas.VoronoiState;

// TODO: Auto-generated Javadoc
/**
 * Created by misha on 15/02/16.
 *
 */
public class CanvasKeyListener implements KeyListener {

	private static final Logger log = LoggerFactory.getLogger(CanvasKeyListener.class);
	private final CanvasController controller;
	private Canvas canvas;
	private JLabel numNodesPlaying;
	public static int nodeCount;
	private int numNodes;
	private JButton nextStep;
	private JTextArea explanation;
	private PromptHandler promptHandler;

	/**
	 * Instantiates a new canvas key listener.
	 *
	 * @param _controller the _controller
	 * @param canvas the canvas
	 * @param numNodesPlaying the num nodes playing
	 * @param nodeCount the node count
	 * @param numNodes the num nodes
	 * @param nextStep the next step
	 * @param explanation the explanation
	 * @param promptHandler the prompt handler
	 */
	public CanvasKeyListener(CanvasController _controller, Canvas canvas, JLabel numNodesPlaying, int nodeCount,
			int numNodes, JButton nextStep, JTextArea explanation, PromptHandler promptHandler) {
		this.controller = _controller;
		this.canvas = canvas;
		this.numNodesPlaying = numNodesPlaying;
		CanvasKeyListener.nodeCount = nodeCount;
		this.numNodes = numNodes;
		this.nextStep = nextStep;
		this.explanation = explanation;
		this.promptHandler = promptHandler;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		log.info("Key pressed");
		this.controller.nextVoronoiStep();
		boolean firstDone = canvas.getState() == VoronoiState.DONE;
		controller.nextVoronoiStep();
		if (canvas.getState() == VoronoiState.ADD_POINT_START) {
			numNodesPlaying.setText(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
		}
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e1) {
		}
		boolean nextDone = canvas.getState() == VoronoiState.DONE;
		if (firstDone && nextDone) {
			nextStep.setVisible(false);
			explanation.setText(promptHandler.getText("visualcomplete"));
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}


	@Override
	public void keyPressed(KeyEvent e) {
	}
}
