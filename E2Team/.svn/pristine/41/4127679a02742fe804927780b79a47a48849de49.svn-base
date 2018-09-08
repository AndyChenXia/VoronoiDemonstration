package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.CanvasController;
import gui.information.PromptHandler;
import model.Canvas;
import model.math.PointBD;
/**
 * 
 * @author Michael
 * @author Andy
 *
 */
public class ManualFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManualFrame.class);
	private final String imagePath = "file:src/main/resources/images/";
	private final PromptHandler promptHandler;
	private final Dimension screenSize;
	private JButton reset;
	private JButton initial;
	private JButton exit;
	private CanvasView display;
	private Canvas canvas;
	private MainGUI gui;
	private InfoDialog infoWindow;
	private String language;
	private JPanel panel;
	private int size;
	private int controlPanelHeight;
	JLabel help;
	/**
	 * Create the frame.
	 */

	/**
	 * instantiate manual frame
	 * @param gui Main GUI
	 * @param language language used
	 * @param width width of the frame
	 * @param height height of the frame
	 * @param size size of the font
	 */
	public ManualFrame(final MainGUI gui, final String language, int width, int height, int size) {
		super("Voronoi Visualisation with custom dots");
		
		this.size = size;
		this.setSize(width, height);
		this.gui = gui;
		this.language = language;
		promptHandler = new PromptHandler(language);
		help = new JLabel(promptHandler.getText("manualstart"));
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		help.setHorizontalAlignment(JLabel.CENTER);
		
		//creating label for notification
		JLabel canvasSize = new JLabel(promptHandler.getText("canvasgoeshere"));
		canvasSize.setForeground(Color.white);

		try {
			infoWindow = new InfoDialog(gui, promptHandler.getText("info3"));
			switch (size) {
			case 0:
				help.setFont(new Font("STSong", Font.PLAIN, 11));
				infoWindow.content.setFont(new Font("STSong", Font.PLAIN, 11));
				canvasSize.setFont(new Font("STSong", Font.PLAIN, 20));
				break;
			case 1:
				help.setFont(new Font("STSong", Font.PLAIN, 15));
				infoWindow.content.setFont(new Font("STSong", Font.PLAIN, 15));
				canvasSize.setFont(new Font("STSong", Font.PLAIN, 30));
				break;
			case 2:
				help.setFont(new Font("STSong", Font.PLAIN, 19));
				infoWindow.content.setFont(new Font("STSong", Font.PLAIN, 19));
				canvasSize.setFont(new Font("STSong", Font.PLAIN, 40));
				break;

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		controlPanelHeight = 100;

		// final Holder holder = new Holder();
		// create panel for getting the size
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.GRAY);
		panel.add(canvasSize);
		this.add(panel);

		setLocation((int) screenSize.getWidth() / 2 - this.getSize().width / 2, (int) screenSize.getHeight() / 2 - this.getSize().height / 2);

	}
	/**
	 * adding display to the manual frame
	 */
	public void addDisplay() {
		this.add(display, BorderLayout.CENTER);
		display.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PointBD p = new PointBD(e.getX(), e.getY());
				canvas.addPoint(p);
			}
		});
		this.repaint();
		this.setResizable(false);
	}

	/**
	 * Creates an image icon from the specified path giving it the specified
	 * description Source:
	 * http://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
	 * 
	 * @param path
	 *            Image path
	 * @param description
	 *            Image description
	 * @return Image icon made from the image
	 */
	private ImageIcon createImageIcon(String path, String description) {
		path = this.imagePath + path;

		try {
			URL imgURL = new URL(path);

			if (!(new File(imgURL.getPath()).exists())) {
				throw new IllegalArgumentException("No such file exists");
			}

			return new ImageIcon(imgURL, description);
		} catch (MalformedURLException | IllegalArgumentException e) {
			log.error("Couldn't find file: " + path, e);
			return null;
		}
	}

	/**
	 * creating the frame 
	 * @param locationX location x of frame
	 * @param locationY location y of frame
	 */
	public void create(int locationX, int locationY) {
		
		// setting properties
		setLocation(locationX, locationY);
		setVisible(true);
		
		// creating controls panel
		JPanel controls = new JPanel(new GridLayout(2, 1, 0, 0));
		controls.setPreferredSize(new Dimension(gui.getWidth(), controlPanelHeight));
		JPanel buttonPanel = new JPanel(new FlowLayout());

		//creating image for the button
		final ImageIcon resetImage = this.createImageIcon("reset.png", "reset");
		final ImageIcon startImage = this.createImageIcon("play.png", "start");
		
		// creating reset button 
		reset = new JButton(startImage);
		reset.setOpaque(false);
		reset.setContentAreaFilled(false);
		reset.setBorderPainted(false);
		reset.setToolTipText(promptHandler.getText("manualstartbutton"));

		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (((ImageIcon) reset.getIcon()).getDescription().equals("start")) {

					panel.setVisible(false);
					canvas = new Canvas(panel.getWidth(), panel.getHeight(), null);
					canvas.setIsManual(true);
					display = new CanvasView(new CanvasController(canvas, null));
					canvas.addObserver(display);

					addDisplay();
					reset.setIcon(resetImage);
					reset.setToolTipText(promptHandler.getText("resettips"));
					help.setText(promptHandler.getText("manualstarted"));
				}

				else if (((ImageIcon) reset.getIcon()).getDescription().equals("reset")) {
					dispose();
					ManualFrame frame = new ManualFrame(gui, language, getWidth(), getHeight(), size);
					frame.create(getLocation().x, getLocation().y);
				}

			}

		});

		// creating exit image and button
		ImageIcon exitImage = this.createImageIcon("door-exit2.png", "exit");
		this.exit = new JButton(exitImage);
		this.exit.setOpaque(false);
		this.exit.setContentAreaFilled(false);
		this.exit.setBorderPainted(false);
		this.exit.setToolTipText(promptHandler.getText("exittips"));

		this.exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}

		});

		// creating inital button and image
		ImageIcon initialImage = this.createImageIcon("home.png", "home");
		this.initial = new JButton(initialImage);
		this.initial.setOpaque(false);
		this.initial.setContentAreaFilled(false);
		this.initial.setBorderPainted(false);
		this.initial.setToolTipText(promptHandler.getText("returntips"));
		this.initial.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				gui.setVisible(true);
				// removeDisplay();
				// addPanel();
				// reset.setIcon(startImage);
				dispose();
			}

		});

		// creating information image and label
		final ImageIcon informationImage = createImageIcon("info20_2.png", "info icon");
		JLabel info = new JLabel(informationImage);
		info.setToolTipText(promptHandler.getText("moreinfo"));
		info.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				infoWindow.setSize(screenSize.width / 2 - (screenSize.width / 4 / 5), screenSize.height / 3);
				infoWindow.setVisible(true);
			}

		});

		gui.setVisible(false);

		buttonPanel.add(info);
		buttonPanel.add(reset);
		buttonPanel.add(initial);
		buttonPanel.add(exit);

		controls.add(help);
		controls.add(buttonPanel);

		this.add(controls, BorderLayout.SOUTH);
	}

}
