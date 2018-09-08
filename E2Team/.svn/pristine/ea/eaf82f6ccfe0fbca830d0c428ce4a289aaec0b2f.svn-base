package gui.information;


public abstract class LanguageHandler extends JsonHandler {

	protected String languageCode;

	/**
	 * Initialise with a language code
	 * @param languageCode the language code (en, es, ch)
	 */
	public LanguageHandler(String languageCode, String jsonPath) {
		super(jsonPath);
		this.languageCode = languageCode;
	}

	/**
	 * Default language is English
	 */
	public LanguageHandler(String jsonPath) {
		this("en", jsonPath);
	}

	/**
	 * Set the language to use
	 * @param languageCode code of the language
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
}
