package emotion.grid;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Class contains complete localisation of all
 * <code>GridKnot<code>'s used in the pattern. It
 * provides method managing relocation of the knots to fit the grid the best.
 * This class takes a architecture of the singleton. Grid has to be placed in
 * the correct place- it is determined on the basis of the pupils localisation.
 * 
 * @author James
 *
 */
public class Grid {

	private static final Logger Log = Logger.getLogger(Grid.class.getName());

	/**
	 * Field contains all knots
	 */
	private Map<KnotType, GridKnot> knots;

	private static Grid grid;

	protected Grid() {
		if (Log.isDebugEnabled()) {
			Log.debug("Singleton constructor used");
		}
		knots = new EnumMap<KnotType, GridKnot>(KnotType.class);
		for (KnotType type : KnotType.values()) {
			knots.put(type, new GridKnot(this, type));
		}
	}

	public static Grid getInstance() {
		if (Grid.grid == null) {
			Grid.grid = new Grid();
		}
		if (Log.isDebugEnabled()) {
			Log.debug("Instance of the singleton taken");
		}
		return grid;
	}

	/**
	 * @return the knots
	 */
	public Map<KnotType, GridKnot> getKnots() {
		return knots;
	}

	/**
	 * Method fit size and initial position of the pattern grid in terms of
	 * examined image.
	 * 
	 * @param leftPupil
	 *            position of the left pupil
	 * @param rightPupil
	 *            position of the right pupil
	 */
	public void placeGrid(Point leftPupil, Point rightPupil) {
		GridKnot leftPupilKnot = this.knots.get(KnotType.LeftPupil);
		GridKnot rightPupilKnot = this.knots.get(KnotType.RightPupil);

		double patternDistanceX = leftPupilKnot.getX() - rightPupilKnot.getX();
		double currentDistanceX = leftPupil.x - rightPupil.x;
		if (patternDistanceX == 0 || currentDistanceX == 0) {
			Log.error("Inappropriate distance between pupils");
			return;
		}
		final double factor = currentDistanceX / patternDistanceX;

		if (Log.isDebugEnabled()) {
			Log.debug("Recalculating grid relations to processed image");
		}
		recalculateDistances(factor);
		double xDistance = (leftPupil.x - leftPupilKnot.getPatternX());
		double yDistance = (leftPupil.y - leftPupilKnot.getPatternY());
		leftPupilKnot.setPatternX(leftPupilKnot.getPatternX() + xDistance);
		leftPupilKnot.setPatternY(leftPupilKnot.getPatternY() + yDistance);

		rightPupilKnot.setPatternX(rightPupilKnot.getPatternX() + xDistance);
		rightPupilKnot.setPatternY(rightPupilKnot.getPatternY() + yDistance);

		KnotType types[] = KnotType.values();
		// Map has the same size as a number of entries in enum KnotType
		for (int i = 0; i < types.length; i++) {
			if (types[i] == KnotType.LeftPupil
					|| types[i] == KnotType.RightPupil) {
				continue;
			}
			GridKnot knot = this.knots.get(types[i]);
			knot.setPatternX(knot.getPatternX() + xDistance);
			knot.setPatternY(knot.getPatternY() + yDistance);

			// First position of knots is the same as pattern one
			knot.setX(knot.getPatternX());
			knot.setY(knot.getPatternY());
		}
		// Set coordinates for LeftPupil and RightPupil that were skipped during
		// iteration
		GridKnot knot = this.knots.get(KnotType.LeftPupil);
		knot.setX(knot.getPatternX());
		knot.setY(knot.getPatternY());
		knot = this.knots.get(KnotType.RightPupil);
		knot.setX(knot.getPatternX());
		knot.setY(knot.getPatternY());

	}

	/**
	 * Method recalculates distances among knots relatively to rate of pattern
	 * distance between pupils and current distance between them
	 * 
	 * @param factor
	 *            how many times increase/decrease distances among knots in the
	 *            grid
	 */
	private void recalculateDistances(double factor) {
		if (Log.isInfoEnabled()) {
			Log.info("Recalculating of knots relations with rate: " + factor);
		}
		KnotType types[] = KnotType.values();
		// Map has the same size as a number of entries in enum KnotType
		GridKnot knot1 = this.knots.get(KnotType.LeftOuterEyebrow);
		for (int j = 0; j < types.length; j++) {
			if (types[j] == KnotType.LeftOuterEyebrow) {
				continue;
			}
			GridKnot knot2 = knots.get(types[j]);
			// Recalculation of X
			double distance = knot1.getX() - knot2.getX();
			distance = ((distance * factor) - distance);
			knot2.setX(knot2.getX() - distance);
			knot2.setPatternX(knot2.getPatternX() - distance);
			// Recalculation of Y
			distance = knot1.getY() - knot2.getY();
			distance = ((distance * factor) - distance);
			knot2.setY(knot2.getY() - distance);
			knot2.setPatternY(knot2.getPatternY() - distance);
		}
	}

	public Grid setKnot(GridKnot knot) {
		this.knots.put(knot.getType(), knot);
		if (Log.isDebugEnabled()) {
			Log.debug("Knot of type: " + knot.getType().name()
					+ " was added to the grid");
		}
		return this;
	}

	/**
	 * Method marks whole grid on the clone of the image
	 */
	public void markGrid(Mat _image) {
		Mat image = _image.clone();
		for (KnotType type : KnotType.values()) {
			GridKnot knot = this.knots.get(type);
			if (knot == null) {
				Log.warn("Unable to get " + type.name() + " from a map");
				continue;
			}
			double x = knot.getX();
			double y = knot.getY();
			double halfSide = knot.getArea() / 2;
			Imgproc.rectangle(image, new Point(x - halfSide, y - halfSide),
					new Point(x + halfSide, y + halfSide), new Scalar(0, 0, 0));
		}
		final String name = "markedGrid.jpg";
		Imgcodecs.imwrite(name, image);
		if (Log.isInfoEnabled()) {
			Log.info("Marked grid has been saved under the name: " + name);
		}
	}

	/**
	 * Method marks whole grid of pattern coordinates on the clone of the image
	 */
	public void markPatternGrid(Mat _image) {
		Mat image = _image.clone();
		for (KnotType type : KnotType.values()) {
			GridKnot knot = this.knots.get(type);
			if (knot == null) {
				Log.warn("Unable to get " + type.name() + " from a map");
				continue;
			}
			double x = knot.getPatternX();
			double y = knot.getPatternY();
			double halfSide = knot.getArea() / 2;
			Imgproc.rectangle(image, new Point(x - halfSide, y - halfSide),
					new Point(x + halfSide, y + halfSide), new Scalar(0, 0, 0));
		}
		final String name = "markedPatternGrid.jpg";
		Imgcodecs.imwrite(name, image);
		if (Log.isInfoEnabled()) {
			Log.info("Marked grid has been saved under the name: " + name);
		}
	}

}
