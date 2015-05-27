package face.decision;

import org.apache.log4j.Logger;
import org.opencv.core.Point;
import org.springframework.stereotype.Component;

import face.elements.FaceFeatures;
import face.elements.FeatureStore;

@Component("decisior")
public class RelationsDeterminant {
	private static final Logger Log = Logger
			.getLogger(RelationsDeterminant.class.getName());

	RelationStore relations;

	public RelationsDeterminant() {
		relations = new RelationStore();
		if (relations == null) {
			Log.error("Unable to allocate memory for RelationStore");
			return;
		}
	}
	
	public void makeDecision(){
		calculateMouthAngle();
	}

	private double calculateMouthAngle() {
		Point mouthLowerMid = FeatureStore
				.getFeaturePoint(FaceFeatures.MouthLowerLip);
		Point mouthLeftCor = FeatureStore
				.getFeaturePoint(FaceFeatures.MouthLeftCorner);
		Point mouthRightCor = FeatureStore
				.getFeaturePoint(FaceFeatures.MouthRightConrner);

		Point vectorLeft = new Point(mouthLowerMid.x - mouthLeftCor.x,
				mouthLowerMid.y - mouthLeftCor.y);
		Point vectorRight = new Point(mouthRightCor.x - mouthLowerMid.x,
				mouthRightCor.y - mouthLowerMid.y);

		double vectorLeftLength = Math.sqrt(Math.pow(vectorLeft.x, 2)
				+ Math.pow(vectorLeft.y, 2));
		double vectorRightLength = Math.sqrt(Math.pow(vectorRight.x, 2)
				+ Math.pow(vectorRight.y, 2));
		double dotProduct = vectorLeft.x * vectorRight.x + vectorLeft.y
				* vectorRight.y;
		double cosAngle = dotProduct / (vectorLeftLength * vectorRightLength);
		double result = Math.acos(cosAngle);
		if (Log.isInfoEnabled()) {
			Log.info("Angle calculated for mouth relations = " + result
					+ " rad");
		}
		relations.setValue(EmotionRelations.AngleMouth, result);
		return result;
	}

	/**
	 * @return the relations
	 */
	public RelationStore getRelations() {
		return relations;
	}

	/**
	 * @param relations
	 *            the relations to set
	 */
	public void setRelations(RelationStore relations) {
		this.relations = relations;
	}

}
