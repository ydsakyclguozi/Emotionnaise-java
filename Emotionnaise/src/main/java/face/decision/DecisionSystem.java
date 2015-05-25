package face.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author James
 *
 */
public class DecisionSystem {
	private static final Logger Log = Logger.getLogger(DecisionSystem.class
			.getName());

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

	// Output of final emotion analysis
	private String emotionAnalysis;

	public DecisionSystem() {
		changeVectors = new HashMap<EmotionVectors, ChangeVector>();
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
					+ " image analysis was not performed");
			return;
		}
		this.matchPatterns();
		this.recalculateValues();
		if (Log.isInfoEnabled()) {
			Log.info("Emotionanalysis completed successfully!");
		}
		System.out.println(this.emotionAnalysis);
	}

	/**
	 * Function compares vectors of the particular face features (e.g. vector of
	 * eyebrows) with respective vectors determined during research. Similarity
	 * of the vector is based on cosine similarity. It's equal 1 for complete
	 * matching, 0 for orthogonal, -1 for opposite vectors. There shouldn't
	 * appear values less than 0, because both analysed and pattern vector are
	 * determined from left to right looking at the image.
	 */
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
			float patternLength = (float) Math.sqrt(patternX * patternX
					+ patternY * patternY);
			float realLength = (float) Math.sqrt(realX * realX + realY * realY);
			/*
			 * Similarity between vectors is determined on the basis of cosine
			 * similarity, likelihood factor is angle
			 */
			float similarityDegree = ((patternX * realX) + (patternY * realY));
			if (patternLength != 0 && realLength != 0) {
				similarityDegree /= (patternLength * realLength);
			} else {
				Log.warn("Similarity degree waqs not calculate completely"
						+ " due to division by 0.");
			}
			for (int j = 0; j < analyzed.getWeights().size(); j++) {
				float tempWeight = analyzed.getWeights().get(
						Emotions.values()[j]);
				tempWeight *= similarityDegree;
				analyzed.getWeights().put(Emotions.values()[j], tempWeight);
				if (Log.isInfoEnabled()) {
					Log.info("Weight for emotion: "
							+ Emotions.values()[j].name() + " set to: "
							+ tempWeight);
				}
			}
		}
	}

	private void recalculateValues() {
		float happiness = 0;
		float sadness = 0;
		float anger = 0;
		float surprise = 0;
		float total = 0;
		for (int i = 0; i < emotionPatterns.size(); i++) {
			Pattern analyzed = emotionPatterns.get(i);
			happiness += analyzed.getWeights().get(Emotions.Happiness);
			sadness += analyzed.getWeights().get(Emotions.Sadness);
			anger += analyzed.getWeights().get(Emotions.Anger);
			surprise += analyzed.getWeights().get(Emotions.Surprise);
		}
		// Calculation of contribution of particular emotion in the whole
		// expression
		total = happiness + sadness + anger + surprise;
		if (total == 0) {
			Log.warn("Total expression value equal to 0."
					+ "Unable to calculate emotion contribution");
			return;
		}
		happiness /= total;
		sadness /= total;
		anger /= total;
		surprise /= total;

		StringBuilder sb = new StringBuilder();
		sb.append(
				"\n Comparision emotion analysis shown that proceeded face is: \n");
		if(happiness>0){
			sb.append("happy in: ").append(happiness).append(" % \n");
		}
		if(sadness>0){
			sb.append("sadness in: ").append(sadness).append(" % \n");
		}
		if(anger>0){
			sb.append("anger in: ").append(anger).append(" % \n");
		}
		if(surprise>0){
			sb.append("surprise in: ").append(surprise).append(" % \n");
		}
		this.emotionAnalysis = sb.toString();
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

	/**
	 * @return the emotionAnalysis
	 */
	public String getEmotionAnalysis() {
		return emotionAnalysis;
	}

	/**
	 * @param emotionAnalysis
	 *            the emotionAnalysis to set
	 */
	public void setEmotionAnalysis(String emotionAnalysis) {
		this.emotionAnalysis = emotionAnalysis;
	}

}
