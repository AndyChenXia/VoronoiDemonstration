package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.CanvasController;
import controller.Holder;
import gui.information.ExplanationHandler;
import gui.information.PromptHandler;
import model.Canvas;
import model.Canvas.VoronoiState;

/**
 * Canvas panel displaying the main visualisation and controls
 * 
 * @author Michael
 * @author Andy
 *
 */
public class CanvasPanel extends JPanel implements Observer {

	private static final Logger log = LoggerFactory.getLogger(CanvasPanel.class);

	private static final long serialVersionUID = 1L;

	private static final String ROOT_IMAGE_PATH = "file:src/main/resources/images/";

	private final PromptHandler promptHandler;
	private final ExplanationHandler explanationHandler;
	public static int nodeCount;
	private MainGUI gui;
	private JPanel canvasPanel;
	private Canvas canvas;
	private CanvasController controller;
	private CanvasView canvasDisplay;
	private Thread playVisThread;
	
	private JPanel controls;
	private JPanel jSlider;
	private JPanel buttons;
	private String playMode;
	private JSlider playSpeedSlider;
	private JLabel playSpeedSliderLabel;
	private JPanel dotDistribPanel;
	private JPanel playChoicePanel;
	private String dotSpread;
	private JScrollPane explanationPane;
	private JTextArea explanation;
	private JSlider numNodesSlider;
	private JLabel numNodesLabel;
	private JLabel numNodesPlaying;
	private int numNodes;
	private int playSpeed;
	private InfoDialog info;
	private InfoDialog info2;
	private final int screenWidth;
	private final int screenHeight;
	private JLabel nodeCountLabel;
	private FontController fontCanvas;
	private int language;
	private JRadioButton square;
	private JRadioButton hex;
	private JRadioButton random;
	private GridBagConstraints c;
	private GridBagConstraints c1;
	private CanvasKeyListener keyListener;

	/**
	 * instantiate canvas panel
	 * @param gui  gui to be represented in 
	 * @param languageCode language 
	 * @param index selected index
	 */
	public CanvasPanel(MainGUI gui, String languageCode, int index) {
		this.gui = gui;
		screenWidth = (int) gui.getScreenX();
		screenHeight = (int) gui.getScreenY();

		// Create handers, contraints and objects needed
		this.promptHandler = new PromptHandler(languageCode);
		this.explanationHandler = new ExplanationHandler(languageCode);
		c = new GridBagConstraints();
		c1 = new GridBagConstraints();
		fontCanvas = new FontController(gui, controls);

		// Variable assignment
		switch(languageCode) {
		case "en":
			language = 0;
			break;
		case "es":
			language = 1;
			break;
		case "ch":
			language = 2;
			break;
		}
		nodeCount = 0;
		playSpeed = 4;
		numNodes = 10;
		playMode = "step by step";
		dotSpread = "random";


		setUpGUI(gui, index);
	}

	/**
	 * setting the gui
	 * @param gui gui to be set
	 * @param index index selected
	 */
	private void setUpGUI(MainGUI gui, int index) {
		// creating info dialogs
		try {
			info = new InfoDialog(gui, promptHandler.getText("info1"));
			info2 = new InfoDialog(gui, promptHandler.getText("info2"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// creting explanation text area
		explanation = new JTextArea();
		explanation.setLineWrap(true);
		explanation.setEditable(false);

		// Set up main canvas panel
		setUpCanvasPanel();

		gui.setResizable(true);
		gui.setSize(new Dimension(this.screenWidth / 2, this.screenHeight - (this.screenHeight / 5)));
		gui.setMinimumSize(
				new Dimension(this.screenWidth / 2 - (150), this.screenHeight - (this.screenHeight / 5) - 150));

		gui.setLocationRelativeTo(null);

		// adding components to font controller
		fontCanvas.registerComponent(info.content);
		fontCanvas.registerComponent(info2.content);
		fontCanvas.registerComponent(explanation);
		fontCanvas.setSelectedIndex(index);
		
}

	/**
	 * setting up the panel for canvas
	 */
	public void setUpCanvasPanel() {
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setLayout(new BorderLayout(0, 0));

		canvasPanel = new JPanel();
		// Main visualisation canvas JPanel
		canvasPanel = new JPanel();
		canvasPanel.setPreferredSize(new Dimension(this.screenWidth / 2, this.screenHeight - 200));
		canvasPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		// Overall panel for all controls
		controls = new JPanel();
		Border controlBorder = BorderFactory.createTitledBorder(promptHandler.getText("controls"));
		controls.setBorder(controlBorder);

		controls.setPreferredSize(new Dimension(800, 300));
		//controls.setLayout(new GridLayout(2, 0));
		controls.setLayout(new GridLayout(2,0));
	
		
		// JPanel for slider
		jSlider = new JPanel();
		this.setUpSliders(jSlider);
		
		// Add sliders to control panel
		
		controls.add(jSlider);

		// Panel for all the buttons on the GUI
		buttons = new JPanel();
		buttons.setLayout(new GridLayout(3, 1, 0, 0));

		// Set up the radio buttons on the control panel
		this.setUpRadioButtons(controls, buttons);

		// Set up the normal buttons on the control panel
		this.setUpJButtons();

		// Add the controls to the GUI canvas panel
		this.add(controls, BorderLayout.SOUTH);

	}

	/**
	 * Set up the radio buttons on the GUI
	 * 
	 * @param controls
	 *            The overall JPanel add the buttons panel to
	 * @param buttons
	 *            The JPanel to add the radio buttons to
	 */
	private void setUpRadioButtons(JPanel controls, JPanel buttons) {
		// Button group for play mode of visualisation
		ButtonGroup playChoice = new ButtonGroup();
		// Continuous play mode
		JRadioButton continuous = new JRadioButton(promptHandler.getText("continuous"));
		continuous.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				playMode = "continuous";
				playSpeedSliderLabel.setVisible(true);
				playSpeedSlider.setVisible(true);
			}
		});
		// Step by step play mode
		JRadioButton stepByStep = new JRadioButton(promptHandler.getText("step by step"));
		stepByStep.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				playMode = "step by step";
				playSpeedSliderLabel.setVisible(false);
				playSpeedSlider.setVisible(false);
			}
		});
		stepByStep.setSelected(true);

		playChoice.add(continuous);
		playChoice.add(stepByStep);

		// Panel to store these radio buttons
		playChoicePanel = new JPanel();
		// adding information image and action listener
		final ImageIcon informationImage = createImageIcon("info20_2.png", "info icon");
		JLabel informationLabel1 = new JLabel(informationImage);
		informationLabel1.setSize(40, 40);
		informationLabel1.setToolTipText(promptHandler.getText("moreinfo"));
		informationLabel1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				info.setSize(screenWidth / 2 - (screenWidth / 4 / 5), screenHeight / 3);
				info.setVisible(true);
			}

		});
		// setting the label for the choose label
		JLabel chooseLabel = new JLabel(promptHandler.getText("playmode"));
		
		// adding compoenent to the choice panel
		playChoicePanel.add(informationLabel1);
		playChoicePanel.add(chooseLabel);
		playChoice.add(continuous);
		playChoice.add(stepByStep);
		playChoicePanel.add(continuous);
		playChoicePanel.add(stepByStep);
		buttons.add(playChoicePanel);

		// Panel for distribution of dots on the visualisation canvas
		dotDistribPanel = new JPanel();
		ButtonGroup dotDistrib = new ButtonGroup();
		// Random distribution
		random = new JRadioButton(promptHandler.getText("random"));
		random.setSelected(true);
		random.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dotSpread = "random";
			}
		});
		// Hex distribution
		hex = new JRadioButton(promptHandler.getText("hex"));
		hex.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dotSpread = "hex";
			}
		});
		// Square distribution
		square = new JRadioButton(promptHandler.getText("square"));
		square.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dotSpread = "square";
			}
		});
		// Add to panel
		dotDistrib.add(random);
		dotDistrib.add(hex);
		dotDistrib.add(square);

		// Create label and add to GUI
		// adding information button to choose dot distribution
		JLabel informationLabel2 = new JLabel(informationImage);
		informationLabel2.setToolTipText(promptHandler.getText("moreinfo"));
		informationLabel2.setSize(40, 40);
		informationLabel2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				info2.setSize(screenWidth / 2 - (screenWidth / 4 / 5), screenHeight / 3);
				info2.setVisible(true);
			}

		});
		
		// creating label for dot distribution
		JLabel dotdistribLabel = new JLabel(promptHandler.getText("choosedistrib"));
		dotDistribPanel.add(informationLabel2);
		dotDistribPanel.add(dotdistribLabel);

		// adding radio buttons to the group
		dotDistrib.add(random);
		dotDistrib.add(hex);
		dotDistrib.add(square);

		// adding radio buttons to the panel
		dotDistribPanel.add(random);
		dotDistribPanel.add(hex);
		dotDistribPanel.add(square);
		this.fontCanvas.registerAllComponents(new Component[] { chooseLabel, continuous, stepByStep, controls,
				dotdistribLabel, random, hex, square });
		this.fontCanvas.setControls(controls);
		buttons.add(dotDistribPanel);
	
		controls.add(buttons);
	}

	/**
	 * setting the buttons
	 */
	private void setUpJButtons() {
		// Buttons and panels to store them on
		JPanel buttonsRowOne = new JPanel();
		buttonsRowOne.setLayout(new FlowLayout());

		final JPanel buttonsRowOneA = new JPanel();
		buttonsRowOneA.setLayout(new FlowLayout());
		// creating picture for the button play
		final ImageIcon play = createImageIcon("play.png", "play icon");
		final JButton playButton = new JButton(play);
		
		// setting the play button
		playButton.setToolTipText(promptHandler.getText("playtips"));
		playButton.setPreferredSize(new Dimension(40, 40));
		playButton.setOpaque(false);
		playButton.setContentAreaFilled(false);
		playButton.setBorderPainted(false);
		playButton.setFocusable(false);

		// creating picture for the button next step
		final ImageIcon next = createImageIcon("next.png", "next icon");
		final JButton nextStep = new JButton(next);
		
		// setting the next button
		nextStep.setToolTipText(promptHandler.getText("steptips"));
		nextStep.setPreferredSize(new Dimension(40, 40));
		nextStep.setOpaque(false);
		nextStep.setContentAreaFilled(false);
		nextStep.setBorderPainted(false);

		nextStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
		});

		// Creating image for the pause button
		final ImageIcon pause = createImageIcon("pause.png", "pause icon");
		final JButton pauseButton = new JButton(pause);
		
		//setting the pause button
		pauseButton.setToolTipText(promptHandler.getText("pausetips"));
		pauseButton.setOpaque(false);
		pauseButton.setContentAreaFilled(false);
		pauseButton.setBorderPainted(false);
		
		// adding mouse listener
		pauseButton.addMouseListener(new MouseAdapter() {
			String pauseDesc = ((ImageIcon) pauseButton.getIcon()).getDescription();

			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				if (pauseDesc == "pause icon") {
					playVisThread.suspend(); // Ideal method for the job despite deprecation
					pauseButton.setIcon(play);
					pauseButton.setToolTipText(promptHandler.getText("playtips"));
					pauseDesc = ((ImageIcon) pauseButton.getIcon()).getDescription();
				} else {
					playVisThread.resume();
					pauseButton.setIcon(pause);
					pauseButton.setToolTipText(promptHandler.getText("pausetips"));
					pauseDesc = ((ImageIcon) pauseButton.getIcon()).getDescription();
				}
			}
		});
		pauseButton.setPreferredSize(new Dimension(40, 40));

		// creating image for the home button
		final ImageIcon home = createImageIcon("home.png", "home icon");
		final JButton homeButton = new JButton(home);
		homeButton.setToolTipText(promptHandler.getText("returntips"));
		homeButton.setOpaque(false);
		homeButton.setContentAreaFilled(false);
		homeButton.setBorderPainted(false);
		homeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (playVisThread != null) {
					playVisThread.interrupt();
				}
				if(explanationPane != null) {
					explanationPane.removeKeyListener(keyListener);
				}
				
				gui.setVisible(false);
				gui = new MainGUI(true, fontCanvas.getSelectedIndex(), language);
			}
		});

		// creating image for the exit button
		final ImageIcon exit = createImageIcon("door-exit2.png", "exit icon");
		final JButton exitButton = new JButton(exit);
		exitButton.setToolTipText(promptHandler.getText("exittips"));
		exitButton.setOpaque(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setBorderPainted(false);
		exitButton.setPreferredSize(new Dimension(40, 40));
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		
		// set label for continuous mode for the user
		final JLabel contPlay = new JLabel("", JLabel.CENTER);
		
		// creating the reset button image
		final ImageIcon reset = createImageIcon("reset.png", "reset icon");
		nodeCountLabel = new JLabel(promptHandler.getText("waitingstart"));
		nodeCountLabel.setHorizontalAlignment(JLabel.CENTER);
		
		// creating label for number of nodes displayed
		numNodesPlaying = new JLabel(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
		numNodesPlaying.setHorizontalAlignment(JLabel.CENTER);

		// getting canvas panel
		final CanvasPanel canvasPanel = this;
		// creating label of dot distribution
		final JLabel dotDistribTemp = new JLabel(
				promptHandler.getText("dotdistrib") + promptHandler.getText(dotSpread));
		dotDistribTemp.setToolTipText(promptHandler.getText("dotdistrib") + promptHandler.getText(dotSpread));
		// creating label of play mode
		final JLabel playModesTemp = new JLabel(
				promptHandler.getText("playModeTemp") + promptHandler.getText(playMode));
		playModesTemp.setToolTipText(promptHandler.getText("playModeTemp") + promptHandler.getText(playMode));
		playButton.addMouseListener(new PlayListener(playButton, homeButton, nextStep, dotDistribTemp, playModesTemp,
				pauseButton, canvasPanel, reset, buttonsRowOneA, exitButton, contPlay, play, pause));
		fontCanvas.registerAllComponents(new Component[] { playButton, exitButton, nextStep, pauseButton,
				dotDistribTemp, playModesTemp, contPlay, numNodesPlaying });

		// adding buttons to the  panel
		buttonsRowOneA.add(playButton);
		buttonsRowOneA.add(homeButton);
		buttonsRowOneA.add(exitButton);
		
		// adding buttons panel to another panel
		buttonsRowOne.add(buttonsRowOneA);
		buttons.add(buttonsRowOneA);

	}

	/**
	 * seeting up the silerds
	 * @param jSlider panel for sliders
	 */
	private void setUpSliders(JPanel jSlider) {
		jSlider.setLayout(new GridBagLayout());
		// Create slider to set number of nodes
		numNodesSlider = new JSlider(5, 50, 10);
		numNodesSlider.setMajorTickSpacing(5);
		numNodesSlider.setMinorTickSpacing(1);
		numNodesSlider.setPaintTicks(true);
		numNodesSlider.setPaintLabels(true);
		numNodesLabel = new JLabel(promptHandler.getText("numnodes") + numNodesSlider.getValue(), JLabel.CENTER);

		// setting the layout
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		jSlider.add(numNodesLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		jSlider.add(numNodesSlider, c);

		// Add slider listener
		numNodesSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					numNodes = source.getValue();
					numNodesLabel.setText(promptHandler.getText("numnodes") + numNodesSlider.getValue());
				}
			}
		});

		int startSpeed = 4;
		playSpeed = startSpeed;
		playSpeedSlider = new JSlider(1, 8, startSpeed);
		// Set labels and ticks on slider
		playSpeedSlider.setPaintLabels(true);
		playSpeedSlider.setMajorTickSpacing(1);
		playSpeedSlider.setPaintTicks(true);
		playSpeedSliderLabel = new JLabel(promptHandler.getText("stepspersec") + startSpeed, JLabel.CENTER);

		// Add slider listener
		playSpeedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					playSpeed = source.getValue();
					playSpeedSliderLabel.setText(promptHandler.getText("stepspersec") + playSpeed);

				}
			}
		});
		
		// setting visibility
		playSpeedSlider.setVisible(false);
		playSpeedSliderLabel.setVisible(false);
		
		// Add slider and label to panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		jSlider.add(playSpeedSliderLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		jSlider.add(playSpeedSlider, c);
		fontCanvas.registerAllComponents(
				new Component[] { playSpeedSliderLabel, playSpeedSlider, numNodesLabel, numNodesSlider });
	}

	/**
	 * creting canvas view
	 * @param jSlider2 
	 * @param buttonsRowOneA 
	 * @return canvas view to be seen
	 */
	public CanvasView createCanvasDisplay(JPanel controls1, JPanel jSlider2,int additional) {
		int width = this.getWidth() - 25;
		int height;
		if (width <= 0) { // Here for GUI testing purposes
			width = 800;
			height = 600;
		} else {
			height = this.getHeight() - controls1.getHeight() - jSlider2.getHeight() -25 - additional;
		}
		final Holder holder = new Holder();
		canvas = new Canvas(width, height, holder);
		this.controller = new CanvasController(canvas, holder);
		final CanvasView display = new CanvasView(controller);
		canvas.addObserver(display);
		canvas.addObserver(this);
		display.setBounds(0, 0, width, height);

		Thread canvasThread = new Thread(new Runnable() {
			@Override
			public void run() {
				canvas.fillCanvasWithPoints(getNumNodes(), dotSpread);
			}
		});

		canvasThread.start();

		return display;
	}
	
	public CanvasView createCanvasDisplay() {
		int width = this.getWidth() - 25;
		int height;
		if (width <= 0) { // Here for GUI testing purposes
			width = 800;
			height = 600;
		} else {
			height = this.getHeight() - controls.getHeight() -25;
		}
		final Holder holder = new Holder();
		canvas = new Canvas(width, height, holder);
		this.controller = new CanvasController(canvas, holder);
		final CanvasView display = new CanvasView(controller);
		canvas.addObserver(display);
		canvas.addObserver(this);
		display.setBounds(0, 0, width, height);

		Thread canvasThread = new Thread(new Runnable() {
			@Override
			public void run() {
				canvas.fillCanvasWithPoints(getNumNodes(), dotSpread);
			}
		});

		canvasThread.start();

		return display;
	}

	/**
	 * Return the number of nodes for the diagram
	 * 
	 * @return The number of nodes
	 */
	public int getNumNodes() {
		return numNodes;
	}

	/**
	 * Gets the play mode for the visualisation (Continuous or step-by-step
	 * 
	 * @return The play mode for the visualisation
	 */
	public String getPlayMode() {
		return playMode;
	}

	@Override
	public void update(Observable o, Object arg) {
		log.info("Repainting MainGUI");

		// Update the amount of points
		int amountOfPoints = this.controller.getPolygons().size();
		this.explanationHandler.setAmountOfPoints(amountOfPoints);

		// Get the instruction
		VoronoiState state = this.controller.getState();
		String instruction = this.explanationHandler.getText(state.toString());

		explanation.setText(instruction);
	}

	/**
	 * Gets the play speed for the visualisation on continuous play mode
	 * 
	 * @return The double value indicating the number of steps per second
	 */
	public double getPlaySpeed() {
		return playSpeed;
	}

	/**
	 * Gets the canvas display object from the panel
	 * 
	 * @return The canvas display object from the panel
	 */
	public CanvasView getCanvasDisplay() {
		return canvasDisplay;
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
		path = ROOT_IMAGE_PATH + path;

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
	 * setting dot distribution
	 * @param distrib chosen dot distribution
	 */
	public void setDotDistrib(String distrib) {
		switch (distrib) {
		case "hex":
			hex.setSelected(true);
			canvas.fillCanvasWithPoints(numNodes, distrib);
			break;
		case "square":
			square.setSelected(true);
			canvas.fillCanvasWithPoints(numNodes, distrib);
			break;
		case "random":
			random.setSelected(true);
			canvas.fillCanvasWithPoints(numNodes, distrib);
			break;
		}
	}
	
	/**
	 * get canvas controller
	 * @return contoller
	 */
	public CanvasController getController() {
		return controller;
	}
	
	/**
	 *  get controls panel
	 * @return controls
	 */
	public JPanel getControlPanel() {
		return controls;
	}

	/**
	 * Inline class which is the mouse action listener for the play button on the canvas panel of the GUI
	 * @author Michael
	 *
	 */
	
	private class PlayListener extends MouseAdapter {
		private final JButton playButton;
		private final JButton homeButton;
		private final JButton nextStep;
		private final JLabel dotDistribTemp;
		private final JLabel playModesTemp;
		private final JButton pauseButton;
		private final CanvasPanel canvasPanel;
		private final ImageIcon reset;
		private final JPanel buttonsRowOneA;
		private final JButton exitButton;
		private final JLabel contPlay;
		private final ImageIcon play;
		private final ImageIcon pause;
		
		/**
		 * instantiate private class
		 */
		private PlayListener(JButton playButton, JButton homeButton, JButton nextStep, JLabel dotDistribTemp,
				JLabel playModesTemp, JButton pauseButton, CanvasPanel canvasPanel, ImageIcon reset,
				JPanel buttonsRowOneA, JButton exitButton, JLabel contPlay, ImageIcon play, ImageIcon pause) {
			this.playButton = playButton;
			this.homeButton = homeButton;
			this.nextStep = nextStep;
			this.dotDistribTemp = dotDistribTemp;
			this.playModesTemp = playModesTemp;
			this.pauseButton = pauseButton;
			this.canvasPanel = canvasPanel;
			this.reset = reset;
			this.buttonsRowOneA = buttonsRowOneA;
			this.exitButton = exitButton;
			this.contPlay = contPlay;
			this.play = play;
			this.pause = pause;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			String playDesc = ((ImageIcon) playButton.getIcon()).getDescription();
			JPanel controls1 = new JPanel();
			if (playDesc == "play icon") {
				// fixing the size of the frame when displaying
				remove (controls);

				controls1.setLayout(new BorderLayout());
				
				controls1.add(jSlider,BorderLayout.NORTH);
				
				canvasPanel.add(controls1,BorderLayout.SOUTH);
				gui.setResizable(false);
				//canvasDisplay = createCanvasDisplay();
				// Add in canvas here to canvas panel
				//canvasPanel.add(canvasDisplay, BorderLayout.CENTER);

				// Remove the sliders for now as we don't need them
				jSlider.remove(numNodesSlider);
				jSlider.remove(numNodesLabel);
				jSlider.remove(playSpeedSlider);
				jSlider.remove(playSpeedSliderLabel);

				// Display dot distribution on control panel after start
				dotDistribTemp.setText(promptHandler.getText("dotdistrib") + promptHandler.getText(dotSpread));
				dotDistribTemp.setHorizontalAlignment(JLabel.CENTER);
				// Hide the panels
				dotDistribPanel.setVisible(false);
				playChoicePanel.setVisible(false);

				if (playMode == "continuous") {
					jSlider.setLayout(new BorderLayout(3, 3));
					
					// creating panel
					JPanel tempLabels = new JPanel();
					tempLabels.setLayout(new GridLayout(1, 4));
					tempLabels.add(numNodesPlaying);

					// seeting label
					playModesTemp.setText(promptHandler.getText("playModeTemp") + promptHandler.getText(playMode));
					tempLabels.add(playModesTemp);
					playModesTemp.setHorizontalAlignment(JLabel.CENTER);

					// adding labels to the panel
					tempLabels.add(dotDistribTemp);
					JLabel speedLabel = playSpeedSliderLabel;
					speedLabel.setToolTipText(promptHandler.getText("stepspersec") + playSpeed);
					tempLabels.add(speedLabel);

					playSpeedSliderLabel.setHorizontalAlignment(JLabel.CENTER);

					numNodesLabel.setHorizontalAlignment(JLabel.CENTER);

					// Add labels and explanation box
					jSlider.add(tempLabels, BorderLayout.NORTH);
					contPlay.setText(promptHandler.getText("continuousplaymode"));
					contPlay.setOpaque(false);
					jSlider.add(contPlay, BorderLayout.CENTER);

					// setting play button
					playButton.setIcon(reset);
					playButton.setToolTipText(promptHandler.getText("resettips"));
					playDesc = ((ImageIcon) playButton.getIcon()).getDescription();
					
					// removing buttons from the panel
					buttonsRowOneA.remove(exitButton);
					buttonsRowOneA.remove(playButton);
					buttonsRowOneA.remove(homeButton);

					// adding buttons to the panel
					buttonsRowOneA.add(pauseButton);
					buttonsRowOneA.add(playButton);
					buttonsRowOneA.add(homeButton);
					buttonsRowOneA.add(exitButton);

					// setting size of control and canvas panel
					setControlsSize(controls1,buttonsRowOneA);
					
					// Add in canvas here to canvas panel
					canvasPanel.add(canvasDisplay, BorderLayout.CENTER);
					
					controls1.add(buttonsRowOneA,BorderLayout.SOUTH);
					Border controlBorder = BorderFactory.createTitledBorder(promptHandler.getText("controls"));
					controls1.setBorder(controlBorder);

					// Play visualisation
					double speed = 1.0 / playSpeed;
					final double speedMS = speed * 1000;
					playVisThread = new Thread(new Runnable() {
						@Override
						public void run() {
							boolean firstDone = canvas.getState() == VoronoiState.DONE;
							controller.nextVoronoiStep();
							boolean firstAddPoint = canvas.getState() == VoronoiState.ADD_POINT_START;
							if (firstAddPoint) {
								// setting the text of numbers of nodes played
								numNodesPlaying.setText(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
								numNodesPlaying
										.setToolTipText(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
							}
							try {
								TimeUnit.MILLISECONDS.sleep(100);
							} catch (InterruptedException e) {
							}
							boolean nextDone = canvas.getState() == VoronoiState.DONE;
							while (!(firstDone && nextDone)) {
								// setting first add point depending on add point started
								firstAddPoint = canvas.getState() == VoronoiState.ADD_POINT_START;
								try {
									TimeUnit.MILLISECONDS.sleep((long) speedMS);
								} catch (InterruptedException e) {
									break;
								}
								
								// setting first add point depending on voronoi completed
								firstDone = canvas.getState() == VoronoiState.DONE;
								controller.nextVoronoiStep();
								
								// setting second add point depending on add point started
								boolean secondAddPoint = canvas.getState() == VoronoiState.ADD_POINT_START;
								if (secondAddPoint && firstAddPoint) {
									numNodesPlaying
											.setText(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
									numNodesPlaying.setToolTipText(
											promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
								}
								try {
									TimeUnit.MILLISECONDS.sleep(100);
								} catch (InterruptedException e) {
									break;
								}
								nextDone = canvas.getState() == VoronoiState.DONE;
							}
							// setting text of label
							contPlay.setText("<html><center>" + promptHandler.getText("visualcomplete")
									+ "</center><br/>" + "<center>" + promptHandler.getText("visualcomplete2")
									+ "</center>" + "</html>");

						}
					});

					playVisThread.start();
					// ------------------

					jSlider.revalidate();
					jSlider.repaint();
				} else {

					// setting the text and tool tips of the label
					numNodesPlaying.setText(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);
					numNodesPlaying.setToolTipText(promptHandler.getText("nodenum") + nodeCount + "/" + numNodes);

					// Add in an uneditable text box to the GUI for the
					// explanation of the visualisation
					explanationPane = new JScrollPane(explanation, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
							JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					explanation.setWrapStyleWord(true);
					explanationPane.setPreferredSize(new Dimension(800, 160));
					explanation.setEditable(false);

					jSlider.setLayout(new BorderLayout(3, 3));

					setControlsSize(controls1,buttonsRowOneA);
					Border controlBorder = BorderFactory.createTitledBorder(promptHandler.getText("controls"));
					controls1.setBorder(controlBorder);
					controls1.add(buttonsRowOneA,BorderLayout.SOUTH);
					//canvasPanel.add(canvasDisplay,BorderLayout.CENTER);
					
					// creating new panel 
					JPanel tempLabels = new JPanel();
					tempLabels.setLayout(new GridLayout(1, 2));
					tempLabels.add(numNodesPlaying);

					// setting the label of playmode
					playModesTemp.setText(promptHandler.getText("playModeTemp") + promptHandler.getText(playMode));
					playModesTemp
							.setToolTipText(promptHandler.getText("playModeTemp") + promptHandler.getText(playMode));
					playModesTemp.setHorizontalAlignment(JLabel.CENTER);
					tempLabels.add(playModesTemp);

					numNodesLabel.setHorizontalAlignment(JLabel.CENTER);

					tempLabels.add(dotDistribTemp);

					dotDistribTemp.setHorizontalAlignment(JLabel.CENTER);

					// adding components to the jSliderpanel
					jSlider.add(tempLabels, BorderLayout.NORTH);
					jSlider.add(explanationPane, BorderLayout.CENTER);
					
					// setting visibility of playspeed slider and label
					playSpeedSlider.setVisible(false);
					playSpeedSliderLabel.setVisible(false);
					
					// changing play button's icon
					playButton.setIcon(reset);
					playButton.setToolTipText(promptHandler.getText("resettips"));
					playDesc = ((ImageIcon) playButton.getIcon()).getDescription();

					// removing buttons from the panel
					buttonsRowOneA.remove(exitButton);
					buttonsRowOneA.remove(playButton);
					buttonsRowOneA.remove(homeButton);
					nextStep.setVisible(true);
					
					// adding buttons to the panel
					buttonsRowOneA.add(nextStep);
					buttonsRowOneA.add(playButton);
					buttonsRowOneA.add(homeButton);
					buttonsRowOneA.add(exitButton);
					
					// Add key listener to allow play through
					keyListener = new CanvasKeyListener(controller, canvas, numNodesPlaying, nodeCount, numNodes, nextStep, explanation, promptHandler);
					explanationPane.addKeyListener(keyListener);
					explanationPane.setFocusable(true);
					explanationPane.requestFocus();
					log.info("Key listener added");
					
					jSlider.revalidate();
					jSlider.repaint();
				}
			} else {
				if (playVisThread != null) {
					playVisThread.interrupt();
				}
				
				// making panel visible
				playChoicePanel.setVisible(true);
				dotDistribPanel.setVisible(true);
				
				if(explanationPane != null) {
					explanationPane.removeKeyListener(keyListener);
					explanationPane.setFocusable(false);
				}
				
				explanation.setText("");
				
				//setting node count to 0
				nodeCount = 0;
				
				// removing everything from panel
				jSlider.removeAll();
				jSlider.setLayout(new GridBagLayout());
				
				// setting the layout 
				c.weightx = 1.0;
				c.gridx = 0;
				c.gridy = 0;
				jSlider.add(numNodesLabel, c);
				numNodesLabel.setHorizontalAlignment(JLabel.CENTER);
				c.gridx = 0;
				c.gridy = 1;
				jSlider.add(numNodesSlider, c);
				c.gridx = 0;
				c.gridy = 2;
				jSlider.add(playSpeedSliderLabel, c);
				c.gridx = 0;
				c.gridy = 3;
				jSlider.add(playSpeedSlider, c);

				jSlider.revalidate();
				jSlider.repaint();
			
				// setting the pay button
				playButton.setIcon(play);
				playButton.setToolTipText(promptHandler.getText("playtips"));
				pauseButton.setIcon(pause);

				playDesc = ((ImageIcon) playButton.getIcon()).getDescription();
				nextStep.setVisible(false);

				// remove buttons from panel
				buttonsRowOneA.remove(nextStep);
				buttonsRowOneA.remove(pauseButton);
				
				// removing everything from the panel
				removeAll();
				controls.removeAll();
				
				// controls adding components to control
				controls.add(jSlider);
				controls.add(buttons);
				buttons.add(buttonsRowOneA);
				add(controls,BorderLayout.SOUTH);
				gui.setResizable(true);

				canvasPanel.revalidate();
				canvasPanel.repaint();
			}
		}
	}
	
	private void setControlsSize(JPanel controls1,JPanel buttonsRowOneA)
	{
		switch (this.fontCanvas.getSelectedIndex())
		{
		case 0:
			controls1.setPreferredSize(new Dimension(buttonsRowOneA.getWidth(),buttonsRowOneA.getHeight()+jSlider.getHeight()+ 20));
			canvasDisplay = createCanvasDisplay(buttonsRowOneA,jSlider,20);
			break;
		case 1:
			controls1.setPreferredSize(new Dimension(buttonsRowOneA.getWidth(),buttonsRowOneA.getHeight()+jSlider.getHeight()+ 50));
			canvasDisplay = createCanvasDisplay(buttonsRowOneA,jSlider,50);
			break;
		case 2:
			controls1.setPreferredSize(new Dimension(buttonsRowOneA.getWidth(),buttonsRowOneA.getHeight()+jSlider.getHeight()+ 60));
			canvasDisplay = createCanvasDisplay(buttonsRowOneA,jSlider,60);
			break;
		}
	}

}
