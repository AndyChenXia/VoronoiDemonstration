package test.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gui.CanvasPanel;
import gui.CanvasView;
import gui.MainGUI;
import gui.WelcomePanel;

import java.util.concurrent.TimeUnit;

import model.Canvas;

import org.junit.Before;
import org.junit.Test;

import controller.CanvasController;

/**
 * JUnit testing for the GUI
 * @author mxc412
 */
public class GUITests {
	
	MainGUI gui;
	WelcomePanel welcomePanel;
	CanvasPanel canvasPanel;
	CanvasView canvasDisplay;
	CanvasController controller;
	Canvas canvas;
	
	@Before 
	/**
	 * Set up the GUI and panels ready for testing
	 */
	public void setUp() {
		gui = new MainGUI(false, 0, 0);
		welcomePanel = gui.getWelcomePanel();
		canvasPanel = welcomePanel.getCanvasPanel();
		canvasDisplay = canvasPanel.createCanvasDisplay();
		controller = canvasDisplay.getController();
		canvas = controller.getCanvas();
	}

	@Test
	/**
	 * Checks to make sure the visualisation is actually returning something to display
	 */
	public void checkCanvasDisplay() {
		assertNotNull(canvasDisplay);
	}
	
	@Test
	/**
	 * Checks to see if the number of nodes we want the canvas to display is the number of nodes it will actually display
	 */
	public void checkNumNodesParam() {
		int expectedNumNodes = canvasPanel.getNumNodes();
				
		// Wait for a brief period to allow the thread adding the points to complete
		try {
			TimeUnit.MILLISECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int actualNumNodes = canvas.getNumNodes();
		
		assertEquals(actualNumNodes, expectedNumNodes);
		
	}
	
	@Test
	public void checkRadioButtons() {
		canvasPanel.setDotDistrib("hex");
		assertEquals("hex", canvas.getDistrib());
		
		canvasPanel.setDotDistrib("square");
		assertEquals("square", canvas.getDistrib());
		
		canvasPanel.setDotDistrib("random");
		assertEquals("random", canvas.getDistrib());
		
	}

}
