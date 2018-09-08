package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * font controller for font size
 * @author Michael
 * @author Andy
 */
public class FontController extends JComboBox<String> {

	private static final long serialVersionUID = 1L;

	public static String[] size = { "S", "M", "L" };
	public static String[] sizeCH = { "小", "中", "大" };
	private Component container;
	private JPanel controls;
	private List<Component> componentsToUpdate;

	/**
	 * instantiate fonr controller
	 * @param container container of the controller
	 * @param controls panel
	 */
	public FontController(Component container, JPanel controls) {
		super(size);
		this.container = container;
		this.componentsToUpdate = new ArrayList<>();
		this.setPreferredSize(new Dimension(100, 25));
		this.setToolTipText("Select Font size");
		this.addActionListener(this);
		this.controls = controls;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Font newFont;

		// setting tooltips and font size depending on the chosen index
		switch (this.getSelectedIndex()) {
		case 0:
			setToolTips(11);
			newFont = generateFont(12);
			((javax.swing.border.TitledBorder) controls.getBorder()).
		        setTitleFont(new Font("STSong", Font.PLAIN, 12));
			this.setFont(newFont);
			break;
		case 1:
			setToolTips(14);
			newFont = generateFont(15);
			((javax.swing.border.TitledBorder) controls.getBorder()).
	        setTitleFont(new Font("STSong", Font.PLAIN, 15));
			this.setFont(newFont);
			break;
		case 2:
			setToolTips(18);
			newFont = generateFont(19);
			((javax.swing.border.TitledBorder) controls.getBorder()).
	        setTitleFont(new Font("STSong", Font.PLAIN, 19));
			this.setFont(newFont);
			break;
		default:
			throw new IllegalArgumentException("Can't find font for selector " + this.getSelectedIndex());
		}
		for (Component c : this.componentsToUpdate) {
			if (c == null) continue;
			c.setFont(newFont);
		}

		this.setControlsSize();
	}

	/**
	 * @param c the component that will be set to changed
	 */
	public void registerComponent(Component c) {
		this.componentsToUpdate.add(c);
	}

	/**
	 * @param cs  the components that will be set to changed
	 */
	public void registerAllComponents(Component[] cs) {
		for (Component c : cs) {
			registerComponent(c);
		}
	}

	/**
	 * Generate a font with a size
	 * 
	 * @param textSize the size of the font
	 * @return the generated font
	 */
	private Font generateFont(int textSize) {
		return new Font("STSong", Font.PLAIN, textSize);
	}

	/**
	 * setting the controls size
	 */
	private void setControlsSize() {
		if (this.controls == null) return;

		if (this.getSelectedIndex() == 2) {
			this.controls.setPreferredSize(new Dimension(container.getWidth(), 340));
		} else {
			this.controls.setPreferredSize(new Dimension(container.getWidth(), 300));
		}
	}

	/**
	 * setting conrols
	 * 
	 * @param _controls the control panel
	 */
	public void setControls(JPanel _controls) {
		this.controls = _controls;
	}
	
	/**
	 * setting tooltips size
	 * @param size font size
	 */
	private void setToolTips(int size)
	{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.getLookAndFeelDefaults().put("Panel.background",new Color(255,255,250));
			UIManager.getLookAndFeelDefaults().put("ToolTip.font", new Font("STSong",Font.PLAIN,size));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
