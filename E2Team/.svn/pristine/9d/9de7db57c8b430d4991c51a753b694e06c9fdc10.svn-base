package gui.information;

import org.json.JSONException;

public class PromptHandler extends LanguageHandler {

	private static final String jsonPath = "src/main/resources/languages.json";

	public PromptHandler(String languageCode) {
		super(languageCode, jsonPath);
	}

	public PromptHandler() {
		super(jsonPath);
	}

	@Override
	protected String getTextFromJSON(String title) throws JSONException {
		return this.jsonRoot
				.getJSONObject(title)
				.getString(this.languageCode);
	}
}
