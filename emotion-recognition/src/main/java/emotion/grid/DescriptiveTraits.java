package emotion.grid;

public enum DescriptiveTraits {
	Pattern(0);
	
	private final int id;

	DescriptiveTraits(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}
