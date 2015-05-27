package face.decisionD;

public enum Emotions {
	Happiness(0), Sadness(1), Anger(2), Surprise(3);

	private final int id;

	Emotions(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}
