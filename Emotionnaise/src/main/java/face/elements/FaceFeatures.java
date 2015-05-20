package face.elements;

public enum FaceFeatures {
	LeftEyeOuterCorner(0),
	LeftEyeInnerCorner(1),
	LeftEyeLowerEyelid(2),
	LeftEyeUpperEyelid(3),
	LeftEyebrowInnerCorner(4),
	LeftEyebrowOuterCorner(5),
	RightEyeOuterCorner(6),
	RightEyeInnerCorner(7),
	EightEyeLoweEyelid(8),
	RightEyeUpperEyelid(9),
	RightEyebrowInnerCorner(10),
	RightEyebrowOuterCorner(11),
	MouthLeftCorner(12),
	MouthRightConrner(13);
	
	
	 private final int id;
	 FaceFeatures(int id) { this.id = id; }
	    public int getValue() { return id; }
}
