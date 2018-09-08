package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Overall GUI for the team project E2
 * 
 * @author Michael, Andy
 *
 */
public class MainGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private WelcomePanel welcomePanel;
	private Dimension screenSize;
	private int fontSize;

	/**
	 * initializing GUI
	 * @param visible visibility of the GUI
	 * @param size	font size
	 * @param language language chosen
	 */
	public MainGUI(boolean visible, int size, int language) {

		super("Voronoi Visualisation");
		fontSize = size;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); // Nimbus theme
			//UIManager.getLookAndFeelDefaults().put("Button.background",new Color(255,255,240));
			UIManager.getLookAndFeelDefaults().put("Panel.background",new Color(255,255,250));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Put frame in the center
		this.setLocation((int) screenSize.getWidth() / 2 - this.getSize().width / 2,
				(int) screenSize.getHeight() / 2 - this.getSize().height / 2);
		this.setResizable(false);
		
		welcomePanel = new WelcomePanel(this, fontSize, language);
		setVisible(visible);
	}

	public static void main(String[] args) {
		new MainGUI(true, 0, 0);
	}

	/**
	 * getting the welcome panel
	 * @return welcoma panel
	 */
	public WelcomePanel getWelcomePanel() {
		return welcomePanel;
	}

	/**
	 * getting the screen width
	 * @return screen width
	 */
	public double getScreenX() {
		return screenSize.getWidth();
	}

	/**
	 * getting the screen height
	 * @return screen height
	 */
	public double getScreenY() {
		return screenSize.getHeight();
	}

}
