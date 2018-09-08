package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Welcome page when the GUI is first opened
 * 
 * @author Michael
 * @author Andy
 *
 */
public class WelcomePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainGUI gui;
	private Language language;
	private JPanel canvasPanel;
	private FontController sizeControl;
	private ManualFrame manual;
	private int fontSize;
	private String languageCode;
	private GridBagConstraints c;

	/**
	 * instantiate the welcome panel
	 * @param gui gui to be represented in
	 * @param size size of font
	 * @param lang language
	 */
	WelcomePanel(MainGUI gui, int size, int lang) {
		super();
		JPanel temp = new JPanel();
		TitledBorder dummyBorder = new TitledBorder("Dummy");
		temp.setBorder(dummyBorder);
		sizeControl = new FontController(gui, temp);
		sizeControl.setBackground(new Color(255, 255, 230));		
		this.gui = gui;
		canvasPanel = null;	
		this.language = new Language();
		this.language.setBackground(new Color(255, 255, 230));
		this.language.setToolTipText("Select the language of the program.");
		this.setOpaque(false);
		
		try {
			this.setUpWelcomePage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (size) {
		case 0:
			fontSize = 12;
		case 1:
			fontSize = 15;
		case 2:
			fontSize = 17;
		default:
			fontSize = 12;
		}

		sizeControl.setSelectedIndex(size);
		this.language.setSelectedIndex(lang);

	}

	/**
	 * Create the welcome page for the Voronoi visualisation
	 * 
	 * @throws IOException
	 */
	public void setUpWelcomePage() throws IOException {
		this.setFont(new Font("STSong", Font.PLAIN, fontSize));

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());

		// creating panel holders
		JPanel panelHolder1 = new JPanel();
		panelHolder1.setOpaque(false);
		JPanel panelHolder2 = new JPanel();
		panelHolder2.setOpaque(false);
		
		// setting constrains for panelholder1
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;

		this.add(panelHolder1, c);
		
		// setting constrains for panelholder2
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 1;
		this.add(panelHolder2, c);
		
		// creating and setting backgournd of the welcome panel
		final ImageIcon welcomeImage = createImageIcon("/images/voronoi6.gif", "Voronoi gif for welcome panel");
		JLabel backgoundImage = new JLabel(welcomeImage);
		gui.setContentPane(backgoundImage);

		// image for the userguide
		final ImageIcon guideImage = createImageIcon("/images/guide3.png", "user guide button");

		// creating user guide and listener
		UserGuide guideLabel = new UserGuide(guideImage, gui);
		
		// creating choose size label and combo box
		JLabel chooseSize = new JLabel("Font size: ");
		panelHolder1.setLayout(new BorderLayout());
		panelHolder1.add(guideLabel, BorderLayout.CENTER);
		panelHolder1.setPreferredSize(new Dimension(300, 300));
		
		// Panel for tutorial and exit buttons
		JPanel welcomeButtons = new JPanel();
		JPanel welcomeControls = new JPanel();
		JPanel languagePanel = new JPanel();

		// setting panel as transparent
		languagePanel.setOpaque(false);
		welcomeControls.setOpaque(false);
		welcomeButtons.setOpaque(false);

		// setting layout of the panels
		languagePanel.setLayout(new GridLayout(1, 2));
		welcomeControls.setLayout(new GridLayout(4, 1));
		welcomeButtons.setLayout(new GridLayout(1, 3));

		// creating tutorial button
		JButton tutorial = this.createTutorialButton();

		// creating exit button
		JButton exit = this.createExitButton();

		// creating free button for putting dots manually
		JButton free = this.createFreeButton();

		// creating Language label
		JLabel labelLanguage = new JLabel("Language:");

		welcomeButtons.add(tutorial);
		welcomeButtons.add(exit);
		languagePanel.add(labelLanguage);
		
		// adding components to language controller
		language.setWelcome(tutorial, exit, labelLanguage, chooseSize, guideLabel, sizeControl, free);
		
		//adding components to font controller
		sizeControl.registerComponent(tutorial);
		sizeControl.registerComponent(exit);
		sizeControl.registerComponent(canvasPanel);
		sizeControl.registerComponent(labelLanguage);
		sizeControl.registerComponent(language);
		sizeControl.registerComponent(chooseSize);
		sizeControl.registerComponent(guideLabel.dialog.getContentText());
		sizeControl.registerComponent(free);

		// adding component to panels
		languagePanel.add(language);

		// panel for font size 
		JPanel textSize = new JPanel(new GridLayout(1, 2));
		textSize.add(chooseSize);
		textSize.add(sizeControl);
		textSize.setOpaque(false);

		// adding panels and buttons to the welcomeControls panel
		welcomeControls.add(free);
		welcomeControls.add(welcomeButtons);
		welcomeControls.add(textSize);
		welcomeControls.add(languagePanel);
		
		// adding welcome buttons to the panelHolder2
		panelHolder2.add(welcomeControls);
		
		gui.setLayout(new GridLayout(1, 1));
		gui.getContentPane().add(this);

	}

	/**
	 * get the canvas panel
	 * @return a canvas panel
	 */
	public CanvasPanel getCanvasPanel() {
		canvasPanel = new CanvasPanel(gui, "en", this.sizeControl.getSelectedIndex());
		return (CanvasPanel) canvasPanel;
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
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	/**
	 * creating the tutorial button for welcome panel
	 * @return tutorial button
	 */
	private JButton createTutorialButton()
	{
		JButton tutorial = new JButton("Tutorial");
		tutorial.setBackground(new Color(255, 255, 230));
		tutorial.setToolTipText("Click to enter the software.");
		tutorial.setPreferredSize(new Dimension(100, 35));
		tutorial.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				gui.setSize(800, 900);
				
				// creating panel according to the selected font size and language
				switch (language.getLanguage()) {
				case "english":
					canvasPanel = new CanvasPanel(gui, "en", sizeControl.getSelectedIndex());
					break;
				case "spanish":
					canvasPanel = new CanvasPanel(gui, "es", sizeControl.getSelectedIndex());
					break;
				case "chinese":
					canvasPanel = new CanvasPanel(gui, "ch", sizeControl.getSelectedIndex());
					break;
				}

				gui.setContentPane(canvasPanel);
				//gui.invalidate();
				//gui.validate();
			}
		});
		
		return tutorial;
	}
	
	/**
	 * creating exit button for welcome panel
	 * @return exit button
	 */
	private JButton createExitButton ()
	{
		JButton exit = new JButton("Exit");
		exit.setBackground(new Color(255, 255, 230));
		exit.setPreferredSize(new Dimension(60, 35));
		exit.setToolTipText("Click to leave the software.");
		exit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);

			}
		});
		
		return exit;
	}
	
	/**
	 * creating free mode button for welcome panel
	 * @return free mode button
	 */
	private JButton createFreeButton()
	{
		JButton free = new JButton("Play by yourself!");
		free.setBackground(new Color(255, 255, 230));
		free.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				gui.setVisible(false);
				
				//changing language according to selected language index		
				switch (language.getSelectedIndex()) {
				case 0:
					languageCode = "en";
					break;
				case 1:
					languageCode = "es";
					break;
				case 2:
					languageCode = "ch";
					break;
				default:
					languageCode = "en";
				}
				
				// create manual frame with selected language and font size
				manual = new ManualFrame(gui, languageCode, 800, 600, sizeControl.getSelectedIndex());
				manual.create((int) gui.getScreenX() / 2 - manual.getSize().width / 2, (int) gui.getScreenY() / 2 - manual.getSize().height / 2);
			}

		});
		
		return free;
	}
}
