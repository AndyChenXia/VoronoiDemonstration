package gui.information;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class JsonHandler {

	private static final Logger log = LoggerFactory.getLogger(JsonHandler.class);

	/**
	 * The root JSON object
	 */
	protected final JSONObject jsonRoot;

	public JsonHandler(String jsonPath) {
		// Setup the JSON object
		JSONObject jsonRoot;

		try {
			String rawJson = new String(Files.readAllBytes(Paths.get(jsonPath)));
			jsonRoot = new JSONObject(rawJson);
		} catch (IOException e) {
			log.error("Can't find languages JSON file", e);
			jsonRoot = null;
		}

		this.jsonRoot = jsonRoot;
	}

	/**
	 * @param title title of the text
	 * @return the text for the title
 	 */
	public String getText(String title) {
		try {
			return this.getTextFromJSON(title);
		} catch(JSONException | NullPointerException e) {
			log.error("Couldn't parse JSON for title {}", title);
			e.printStackTrace();
			return "N/A";
		}
	}

	/**
	 * Get the text from the JSON.
	 * CAlled by getText(), so the error handling is done there.
	 * @param title title of the text
	 * @return the text for the title
	 */
	protected abstract String getTextFromJSON(String title);
}
