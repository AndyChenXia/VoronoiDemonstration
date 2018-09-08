package gui;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import model.Canvas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.CanvasController;
import controller.Holder;

/**
 * A prototype GUI frame - No longer used/needed
 * @author mxw449, std487
 *
 */
public class PrototypeGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PrototypeGUI.class);

	public PrototypeGUI() {
		super("prototype");

		setSize(800, 800);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void addComponent(Component component) {
		this.getContentPane().add(component);
	}

	public static void main(String[] args) {
		log.debug("Setting up the GUI...");
		Holder holder = new Holder();

		Canvas canvas = new Canvas(500, 500, holder);
		CanvasController controller = new CanvasController(canvas, holder);
		CanvasView display = new CanvasView(controller);

		canvas.addObserver(display);
		display.setBounds(100, 100, canvas.getWidth().intValue() + 1, canvas.getHeight().intValue() + 1);

		PrototypeGUI gui = new PrototypeGUI();
		gui.setLayout(null);
		gui.addComponent(display);
		//gui.addKeyListener(new CanvasKeyListener(controller));
		gui.setVisible(true);

		//canvas.fillSafely();

		log.debug("Set up GUI with canvas");
	}
}
