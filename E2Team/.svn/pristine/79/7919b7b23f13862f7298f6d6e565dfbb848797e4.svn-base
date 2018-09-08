package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
/**
 * information dialog for help 
 * @author Michael
 * @author Andy
 */
public class InfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final File instructionText;
	public JTextArea content;
	private JScrollPane scroll;
	private BufferedReader in;

	/**
	 * instantiate information dialog
	 * @param gui	gui to be represented in
	 * @param path	path to the document
	 * @throws IOException
	 */
	public InfoDialog(MainGUI gui, String path) throws IOException {
		super(gui);
		this.instructionText = new File(path);
		in = new BufferedReader(new FileReader(this.instructionText));
		content = new JTextArea();
		content.setEditable(false);
		content.setLineWrap(true);
		content.setWrapStyleWord(true);
		this.createText();
		scroll = new JScrollPane(content);
		this.add(scroll);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}

	/**
	 *  creating the text area by reading the document
	 * @throws IOException
	 */
	private void createText() throws IOException {
		String line;
		line = in.readLine();
		while (line != null) {
			content.append(line + "\n");
			line = in.readLine();
		}
	}
}
