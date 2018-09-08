package gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
/**
 * label for the user guide
 * @author Michael
 * @author Andy
 *
 */
public class UserGuide extends JLabel implements MouseListener {

	private static final long serialVersionUID = 1L;
	public UserGuideDialog dialog;
	private int screenWidth;
	private int screenHeight;

	/**
	 *  instantiate user guide
	 * @param guideImage image for the user guide 
	 * @param gui	gui to be represented in
	 * @throws IOException
	 */
	public UserGuide(ImageIcon guideImage, MainGUI gui) throws IOException {
		super(guideImage);
		screenWidth = (int) gui.getScreenX();
		screenHeight = (int) gui.getScreenY();
		dialog = new UserGuideDialog();
		dialog.setVisible(false);
		dialog.setSize(new Dimension(screenWidth / 2, screenHeight / 2));
		this.settingLabel();
		this.addMouseListener(this);
	}

	/**
	 * creating button 
	 */
	public void settingLabel() {
		this.setToolTipText("Introduction for this software");
		this.setHorizontalAlignment(UserGuide.CENTER);
		this.setVerticalAlignment(UserGuide.BOTTOM);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void setImage(ImageIcon image) {
		this.setIcon(image);
	}

}
