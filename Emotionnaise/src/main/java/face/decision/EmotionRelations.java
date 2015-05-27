package face.decision;

/**
 * 
 * @author James
 *
 *         AngleMouth-angle between lines joining mouth corners with point in
 *         the middle of lower lip
 * 
 *         AngleEyebrow- angle between line joining extreme points of the
 *         eyebrow and horizontal line. Defined as
 *         <code>arctan((p2.y-p1.y)/(p2.x-p1.x))</code>
 */

public enum EmotionRelations {
	AngleMouth(0), AngleEyebrow(1);

	private final int id;

	EmotionRelations(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}
