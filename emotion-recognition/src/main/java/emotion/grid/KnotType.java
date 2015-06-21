package emotion.grid;

public enum KnotType {
	LeftOuterEyebrow(0),
	LeftInnerEyebrow(1),
	RightInnerEyebrow(2),
	RightOuterEyebrow(3),
	LeftOuterEye(4),
	LeftInnerEye(5),
	RightInnerEye(6),
	RightOuterEye(7),
	MouthLeft(8),
	MouthRight(9),
	MouthUpper(10),
	MouthLower(11),
	LeftPupil(12),
	RightPupil(13);
	
	
	private final int id;

	KnotType(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}
