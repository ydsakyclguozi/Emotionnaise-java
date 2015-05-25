package face.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DecisionSystem {
	private static final Logger Log = Logger.getLogger(DecisionSystem.class
			.getName());

	// If real vector match completely to pattern vector, sum=2
	private static final float fullMatchingValue = 2f;

	private static List<Pattern> emotionPatterns;

	// Initialisation of the map according to research results
	static {
		emotionPatterns = new ArrayList<Pattern>();
		if (Log.isInfoEnabled()) {
			Log.info("Decision system created.");
		}
		emotionPatterns.add(new Pattern());
	}

	// ChangeVectors determined based on comparison of a neutral face with
	// "expressing" features
	private Map<EmotionVectors, ChangeVector> changeVectors;

	public DecisionSystem() {
	}

	public void setChangeVector4Emotion(EmotionVectors emo, ChangeVector vec) {
		changeVectors.put(emo, vec);
		if (this.changeVectors.containsKey(emo)) {
			Log.warn("Value for " + emo.name() + " changed to value "
					+ vec.toString());
		}
	}

	public void makeDecision() {
		if (emotionPatterns == null || changeVectors == null) {
			Log.error("Unable to make a decision either patterns were not fixed or"
					+ "image analysis was not performed");
			return;
		}
		matchPatterns();
		recalculateValues();
	}

	private void matchPatterns() {
		// Analysis performed for each emotion pattern for a map
		for (int i = 0; i < emotionPatterns.size(); i++) {
			Pattern analyzed = emotionPatterns.get(i);
			float patternX = analyzed.getChangeVector().getX();
			float realX = changeVectors.get(analyzed.getAnalyzedRelation())
					.getX();
			float patternY = analyzed.getChangeVector().getY();
			float realY = changeVectors.get(analyzed.getAnalyzedRelation())
					.getY();
			float patternLength=Math.sqrt(patternX*patternX+patternY*patternY);
			float realLength=Math.sqrt(realX*realX+realY*realY);
			/*Similarity between vectors is determined on the basis of cosine similarity
			*likelihood factor is angle*/
			

		
		}
	}

	private void recalculateValues() {
		Map<Emotions,Float> finalValues=new HashMap<Emotions, Float>();
		
	}

	/**
	 * @return the emotionPatterns
	 */
	public static List<Pattern> getEmotionPatterns() {
		return emotionPatterns;
	}

	/**
	 * @param emotionPatterns
	 *            the emotionPatterns to set
	 */
	public static void setEmotionPatterns(List<Pattern> emotionPatterns) {
		DecisionSystem.emotionPatterns = emotionPatterns;
	}

	/**
	 * @return the changeVectors
	 */
	public Map<EmotionVectors, ChangeVector> getChangeVectors() {
		return changeVectors;
	}

	/**
	 * @param changeVectors
	 *            the changeVectors to set
	 */
	public void setChangeVectors(Map<EmotionVectors, ChangeVector> changeVectors) {
		this.changeVectors = changeVectors;
	}
}
