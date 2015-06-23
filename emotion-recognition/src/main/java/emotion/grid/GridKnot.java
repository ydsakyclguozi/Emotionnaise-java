package emotion.grid;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 
 * @author James
 *
 *         Class that is compose a grid. It contains information about
 *         coordinates, type of the point and description of a knot and/or
 *         region nearby. Coordinates of all knots are relative to
 *         <code>LeftOuterEyebrow</code> that is deemed as reference point of
 *         Grid coordinates.
 */
public class GridKnot {

	private static final Logger Log = Logger
			.getLogger(GridKnot.class.getName());

	private static int number = 0;

	/**
	 * Horizontal coordinate of the knot from the pattern
	 */
	private double patternX;

	/**
	 * Vertical coordinate of the knot from the pattern
	 */
	private double patternY;

	/**
	 * Current horizontal coordinate of the knot
	 */
	private double x;

	/**
	 * Current vertical coordinates of the knot
	 */
	private double y;

	/**
	 * Correlation between knot and image according to
	 * <code> DescriptiveTraits</code>
	 */
	private double correlation;

	/**
	 * Distortion cased by bowing the grid
	 */
	private double distortion;

	/**
	 * Type of the knot respective for particular facial feature
	 */
	private KnotType type;

	/**
	 * One side of a square that is taken into account while matching a knot
	 * using description traits. Odd number
	 */
	private int areaSide;

	/**
	 * Grid to which knot belongs
	 */
	private Grid grid;

	/**
	 * Neighbourhood of the point- knots that affect <code>this</code>
	 */
	List<KnotType> neighbours;

	/**
	 * Traits that describes a knot in terms of the pattern. Contain pattern
	 * description
	 */
	Map<DescriptiveTraits, Object> traits;

	public GridKnot(Grid _grid, KnotType type) {
		if (Log.isDebugEnabled()) {
			Log.debug("Knot was created with position (x,y): " + x + "," + y
					+ "; type: " + type.name());
		}
		this.grid = _grid;
		traits = new EnumMap<DescriptiveTraits, Object>(DescriptiveTraits.class);
		neighbours = new ArrayList<KnotType>();
		this.setType(type);
		this.x = this.patternX;
		this.y = this.patternY;
		double side = areaSide / 2;

		GridKnot.number++;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the type
	 */
	public KnotType getType() {
		return type;
	}

	/**
	 * Set type of the <code>GridKnot</code> simultaneously generate neighbours,
	 * area and pattern coordinates for the given type
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(KnotType type) {
		final int EYE_AREA = 15;
		final int EYEBROW_AREA = 15;
		final int MOUTH_AREA = 15;
		final int PUPIL_AREA = 15;
		this.type = type;
		switch (type) {
		case LeftInnerEye:
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.RightInnerEye);
			this.patternX = 40;
			this.patternY = 17;
			this.areaSide = EYE_AREA;
			break;
		case LeftPupil:
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftOuterEyebrow);
			this.neighbours.add(KnotType.LeftInnerEye);
			this.neighbours.add(KnotType.LeftOuterEye);
			this.patternX = 24;
			this.patternY = 14;
			this.areaSide = PUPIL_AREA;
			break;
		case LeftOuterEye:
			this.neighbours.add(KnotType.LeftOuterEyebrow);
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.MouthLeft);
			this.patternX = 8;
			this.patternY = 15;
			this.areaSide = EYE_AREA;
			break;
		case LeftOuterEyebrow:
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.LeftOuterEye);
			this.patternX = 0;
			this.patternY = 0;
			this.areaSide = EYEBROW_AREA;
			break;
		case LeftInnerEyebrow:
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.LeftOuterEyebrow);
			this.neighbours.add(KnotType.LeftInnerEye);
			this.neighbours.add(KnotType.RightInnerEye);
			this.patternX = 42;
			this.patternY = -2;
			this.areaSide = EYEBROW_AREA;
			break;
		case RightInnerEye:
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftInnerEye);
			this.neighbours.add(KnotType.RightInnerEyebrow);
			this.patternX = 76;
			this.patternY = 17;
			this.areaSide = EYE_AREA;
			break;
		case RightInnerEyebrow:
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.RightInnerEye);
			this.neighbours.add(KnotType.RightOuterEyebrow);
			this.patternX = 73;
			this.patternY = 0;
			this.areaSide = EYEBROW_AREA;
			break;
		case RightPupil:
			this.neighbours.add(KnotType.RightInnerEyebrow);
			this.neighbours.add(KnotType.RightOuterEyebrow);
			this.neighbours.add(KnotType.RightInnerEye);
			this.neighbours.add(KnotType.RightOuterEye);
			this.patternX = 89;
			this.patternY = 12;
			this.areaSide = PUPIL_AREA;
			break;
		case RightOuterEyebrow:
			this.neighbours.add(KnotType.RightInnerEyebrow);
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.RightOuterEye);
			this.patternX = 114;
			this.patternY = -3;
			this.areaSide = EYEBROW_AREA;
			break;
		case RightOuterEye:
			this.neighbours.add(KnotType.RightOuterEyebrow);
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.MouthRight);
			this.patternX = 105;
			this.patternY = 14;
			this.areaSide = EYE_AREA;
			break;
		case MouthLeft:
			this.neighbours.add(KnotType.LeftOuterEye);
			this.neighbours.add(KnotType.MouthUpper);
			this.neighbours.add(KnotType.MouthLower);
			this.patternX = 35;
			this.patternY = 79;
			this.areaSide = MOUTH_AREA;
			break;
		case MouthLower:
			this.neighbours.add(KnotType.MouthRight);
			this.neighbours.add(KnotType.MouthUpper);
			this.neighbours.add(KnotType.MouthLeft);
			this.patternX = 63;
			this.patternY = 94;
			this.areaSide = MOUTH_AREA;
			break;
		case MouthUpper:
			this.neighbours.add(KnotType.MouthLeft);
			this.neighbours.add(KnotType.MouthRight);
			this.neighbours.add(KnotType.MouthLower);
			this.patternX = 62;
			this.patternY = 70;
			this.areaSide = MOUTH_AREA;
			break;
		case MouthRight:
			this.neighbours.add(KnotType.RightOuterEye);
			this.neighbours.add(KnotType.MouthUpper);
			this.neighbours.add(KnotType.MouthLower);
			this.patternX = 88;
			this.patternY = 78;
			this.areaSide = MOUTH_AREA;
			break;
		}
	}

	/**
	 * @return the area
	 */
	public int getArea() {
		return areaSide;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(int area) {
		this.areaSide = area;
	}

	/**
	 * @return the number
	 */
	public static int getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public static void setNumber(int number) {
		GridKnot.number = number;
	}

	/**
	 * @return the traits
	 */
	public Map<DescriptiveTraits, Object> getTraits() {
		return traits;
	}

	/**
	 * @param traits
	 *            the traits to set
	 */
	public void setTraits(Map<DescriptiveTraits, Object> traits) {
		this.traits = traits;
	}

	/**
	 * @return the neighbours
	 */
	public List<KnotType> getNeighbours() {
		return neighbours;
	}

	/**
	 * @param neighbours
	 *            the neighbours to set
	 */
	public void setNeighbours(List<KnotType> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * @return the patternX
	 */
	public double getPatternX() {
		return patternX;
	}

	/**
	 * @param patternX
	 *            the patternX to set
	 */
	public void setPatternX(double patternX) {
		this.patternX = patternX;
	}

	/**
	 * @return the patternY
	 */
	public double getPatternY() {
		return patternY;
	}

	/**
	 * @param patternY
	 *            the patternY to set
	 */
	public void setPatternY(double patternY) {
		this.patternY = patternY;
	}

	/**
	 * @return the correlation
	 */
	public double getCorrelation() {
		return correlation;
	}

	/**
	 * @param correlation
	 *            the correlation to set
	 */
	public void setCorrelation(double correlation) {
		this.correlation = correlation;
	}

	/**
	 * @return the distortion
	 */
	public double getDistortion() {
		return distortion;
	}

	/**
	 * @param distortion
	 *            the distortion to set
	 */
	public void setDistortion(double distortion) {
		this.distortion = distortion;
	}

	/**
	 * Add knot that is deemed as a neighbour of <code>this</code> knot
	 * 
	 * @param neighbour
	 *            Knot that can affect <code>this</code>
	 */
	public void addNeighbour(KnotType neighbour) {
		this.neighbours.add(neighbour);
	}

	/**
	 * @return the grid
	 */
	public Grid getGrid() {
		return grid;
	}

	/**
	 * @param grid
	 *            the grid to set
	 */
	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	/**
	 * @return the Rectangle around the current position of the knot
	 */
	public Rect getRect() {
		Rect rect = new Rect((int) this.x, (int) this.y, 1, 1);
		double side = this.areaSide / 2;
		// Flag to check if Rectangle does not goes outside image
		boolean accepted = true;
		do {
			try {
				rect = new Rect((int) (this.x - side), (int) (this.y - side),
						this.areaSide, this.areaSide);
				accepted = false;
			} catch (Exception e) {
				Log.warn("Area might goes outside the image", e);
				// In case of too large Rectangle- side is decreased by 1
				areaSide--;
			}
		} while (accepted);
		return rect;
	}

	/**
	 * Method put area trait for particular knot
	 * 
	 * @param trait
	 *            Trait to put
	 * @param value
	 * 
	 */
	public void addTrait(DescriptiveTraits trait, Object value) {
		traits.put(trait, value);
	}

	public Object getTrait(DescriptiveTraits trait) {
		if (trait == null) {
			Log.warn("Attepmt to get a trait from null traits map.");
			return null;
		}
		return traits.get(trait);
	}

	/**
	 * Method moves a knot around the neighbourhood in order to find the best
	 * location
	 */
	public void findBestPlace(Mat img) {
		sobelAngle(img);
		// TODO: write method
	}

	/**
	 * Method looks for average angle of the gradient within the
	 * <code>areaSide</code>
	 * 
	 * @param image
	 *            RGB image
	 * @return angle of gradient
	 */
	private double sobelAngle(Mat img) {
		Mat image = new Mat(img, this.getRect());
		// Mat image=img.clone();
		Mat grey_x = new Mat();
		Mat result = new Mat(img.height(), img.width(), CvType.CV_32F);
		Imgproc.cvtColor(image, grey_x, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(grey_x, grey_x, 3);
		Imgcodecs.imwrite("sobelOperatorStep1.jpg", grey_x);
		Mat grey_y = grey_x.clone();
		Imgproc.Sobel(grey_y, grey_y, -1, 0, 1); // y
		Imgproc.Sobel(grey_x, grey_x, -1, 1, 0); // x
		grey_x.convertTo(grey_x, CvType.CV_32F);
		grey_y.convertTo(grey_y, CvType.CV_32F);
		Core.phase(grey_y, grey_x, result, true); // result in degrees
		// Core.addWeighted(grey_y, 1, grey_x, 0, 0, result);
		// Core.divide(grey_y, grey_x, result);
		double height = result.height();
		double width = result.width();
		double averageDirection = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				averageDirection += result.get(j, i)[0];
			}
		}
		if (width * height > 0) {
			averageDirection /= (width * height);
		}
		if (Log.isInfoEnabled()) {
			Log.info("Average gradient angle for region equals to: "
					+ averageDirection);
		}
		Imgcodecs.imwrite("sobelOperatorResult.jpg", result);
		return 0;
	}

	/**
	 * Method checks if particular relations between neighbouring knots are
	 * hold. For example if <code>LeftOuterEyebrow</code> is above
	 * <code>LeftOuterEye</code>
	 * 
	 * @return whether knot is in correct region
	 */
	private boolean checkConstraints() {
		boolean isCorrect = true;
		// TODO: finish method
		return isCorrect;
	}

	/**
	 * Method calculates a tension of a <code>GridKnot</code> in new position in
	 * terms of location of neighbours.
	 * 
	 * Tension is calculated as absolute values of changes from a balance point:
	 * (patternX, patternY)
	 * 
	 * The less value is, the less deformation is necessary to fit a grid
	 * 
	 * @return tension being a result of a grid deformation
	 */
	public double calculateGridTension() {
		double tension = Math.abs(this.patternX - this.x);
		tension += Math.abs(this.patternY - this.y);
		if (Log.isDebugEnabled()) {
			Log.debug("Grid tension for a point is equal " + tension);
		}
		return tension;
	}

	/**
	 * Method determines degree in which image representation suits to knot
	 * description. The greater value is, the more precise place was found.
	 * 
	 * @return value of "fitting" an image area to the knot
	 */
	public double calculateCorrelation(Mat img) {
		double correlation = 1;
		if (this.traits.containsKey(DescriptiveTraits.GradientAngle)) {
			double currentAngle = sobelAngle(img);
			correlation -= (Math.abs(currentAngle
					- (Double) (this.traits
							.get(DescriptiveTraits.GradientAngle))));
		}

		if (Log.isDebugEnabled()) {
			Log.debug("Grid correlation for a point is equal " + correlation);
		}

		return correlation;
	}

	/**
	 * @return String representation of the <code>GridKnot</code>
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Knot at: ").append(x).append(",").append(y)
				.append("; of type: ").append(this.type.name());
		return builder.toString();
	}

}
