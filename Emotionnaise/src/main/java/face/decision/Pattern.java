package face.decision;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class Pattern {
	private static final Logger Log = Logger.getLogger(Pattern.class.getName());

	private ChangeVector changeVector;

	private EmotionVectors analyzedRelation;

	// Weights assign to particular emotion (vector of change) on the basis of
	// research
	private Map<Emotions, Float> weights;

	public Pattern(ChangeVector changeVector, EmotionVectors analyzedRelation,
			Map<Emotions, Float> weights) {
		this.changeVector = changeVector;
		this.analyzedRelation = analyzedRelation;
		this.weights = weights;
		if (weights == null || changeVector == null || analyzedRelation == null) {
			Log.error("Unable to create pattern. Nulls values appear!");
		}
	}

	public Pattern() {
		weights = new HashMap<Emotions, Float>();
		if (weights == null) {
			Log.error("Unable to define HashMap for emotion weights");
		}
	}

	public void addValue(Emotions emo, float weight) {
		if (this.weights == null) {
			Log.error("Map is not initialized, cannot add value!");
			return;
		}
		if (this.weights.containsKey(emo.name())) {
			Log.warn("Value for key " + emo.name() + "was change from "
					+ this.weights.get(emo) + " to " + weight);
		}
		this.weights.put(emo, weight);
	}

	/**
	 * @return the changeVector
	 */
	public ChangeVector getChangeVector() {
		return changeVector;
	}

	/**
	 * @param changeVector
	 *            the changeVector to set
	 */
	public void setChangeVector(ChangeVector changeVector) {
		this.changeVector = changeVector;
	}

	/**
	 * @return the weights
	 */
	public Map<Emotions, Float> getWeights() {
		return weights;
	}

	/**
	 * @param weights
	 *            the weights to set
	 */
	public void setWeights(Map<Emotions, Float> weights) {
		this.weights = weights;
	}

	/**
	 * @return the analyzedRelation
	 */
	public EmotionVectors getAnalyzedRelation() {
		return analyzedRelation;
	}

	/**
	 * @param analyzedRelation
	 *            the analyzedRelation to set
	 */
	public void setAnalyzedRelation(EmotionVectors analyzedRelation) {
		this.analyzedRelation = analyzedRelation;
	}

}
