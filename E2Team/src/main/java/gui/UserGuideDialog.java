package gui;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * User Guide to help the user
 * @author Michael
 * @author Andy
 *
 */
public class UserGuideDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final File instructionText;
	private JScrollPane scroll;
	private JTextArea content;
	private BufferedReader in;

	/**
	 * instantiate user dialog 
	 * @throws IOException
	 */
	public UserGuideDialog() throws IOException {
		this.instructionText = new File("instructions");
		this.in = new BufferedReader(new FileReader(this.instructionText));
		this.content = new JTextArea();
		this.content.setEditable(false);
		this.content.setLineWrap(true);
		this.content.setWrapStyleWord(true);

		this.getText();
		this.scroll = new JScrollPane(content);
		this.add(scroll);

	}

	/**
	 * getting the content from a document and add it in to the content
	 * @throws IOException
	 */
	public void getText() throws IOException {
		String line;
		line = in.readLine();
		while (line != null) {
			content.append(line + "\n");
			line = in.readLine();
		}
	}

	/**
	 * get the content of the dialog
	 * @return conent of the dialog
	 */
	public JTextArea getContentText() {
		return content;
	}
}
