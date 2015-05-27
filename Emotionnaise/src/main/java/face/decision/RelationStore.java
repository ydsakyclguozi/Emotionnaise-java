package face.decision;

import org.apache.log4j.Logger;

public class RelationStore {
	private static final Logger Log = Logger.getLogger(RelationStore.class
			.getName());

	private double relations[];

	public RelationStore() {
		relations = new double[EmotionRelations.values().length];
		if (relations == null) {
			Log.error("Unable to allocate memory for relations array");
			return;
		}
	}

	public void setValue(EmotionRelations emoR, double value) {
		this.relations[emoR.getValue()] = value;
		if (Log.isInfoEnabled()) {
			Log.info("Value for: " + emoR.name() + " set to: " + value);
		}
	}

	public double getValue(EmotionRelations emoR) {
		return this.relations[emoR.getValue()];
	}
}
