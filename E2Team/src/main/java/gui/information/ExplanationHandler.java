package gui.information;

import org.json.JSONException;

public class ExplanationHandler extends LanguageHandler {

	private static final String jsonPath = "src/main/resources/explanation.json";

	private int amountOfPoints;

	public ExplanationHandler(String languageCode) {
		super(languageCode, jsonPath);

		this.amountOfPoints = 0;
	}

	public ExplanationHandler() {
		super(jsonPath);
	}

	public void setAmountOfPoints(int amountOfPoints) {
		if (amountOfPoints < 0) {
			throw new IllegalArgumentException("Can't have a negative amount of points.");
		}

		this.amountOfPoints = amountOfPoints;
	}

	private String getPointIdentifier() {
		switch(this.amountOfPoints) {
			// First point
			case 0:
				return "firstPoint";
			// Second point
			case 1:
				return "secondPoint";
			// All other points
			default:
				return "newPoint";
		}
	}

	@Override
	protected String getTextFromJSON(String title) throws JSONException {
		String identifier = this.getPointIdentifier();

		return this.jsonRoot
				.getJSONObject(identifier)
				.getJSONObject(title)
				.getString(this.languageCode);
	}
}
