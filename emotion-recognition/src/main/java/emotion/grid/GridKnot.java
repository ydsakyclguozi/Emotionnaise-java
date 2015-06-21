package emotion.grid;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
	private int area;

	/**
	 * Grid to which knot belongs
	 */
	private Grid grid;

	/**
	 * Neighbourhood of the point- knots that affect <code>this</code>
	 */
	List<KnotType> neighbours;

	/**
	 * Traits that describes a knot in terms of the pattern
	 */
	Map<DescriptiveTraits, Object> traits;

	public GridKnot(Grid _grid,KnotType type) {
		if (Log.isDebugEnabled()) {
			Log.debug("Knot was created with position (x,y): " + x + "," + y
					+ "; type: " + type.name());
		}
		this.grid=_grid;
		traits = new EnumMap<DescriptiveTraits, Object>(DescriptiveTraits.class);
		neighbours = new ArrayList<KnotType>();
		this.setType(type);
		this.x=this.patternX;
		this.y=this.patternY;
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
			this.area = EYE_AREA;
			break;
		case LeftPupil:
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftOuterEyebrow);
			this.neighbours.add(KnotType.LeftInnerEye);
			this.neighbours.add(KnotType.LeftOuterEye);
			this.patternX = 24;
			this.patternY = 14;
			this.area = PUPIL_AREA;
			break;
		case LeftOuterEye:
			this.neighbours.add(KnotType.LeftOuterEyebrow);
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.MouthLeft);
			this.patternX = 8;
			this.patternY = 15;
			this.area = EYE_AREA;
			break;
		case LeftOuterEyebrow:
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.LeftOuterEye);
			this.patternX = 0;
			this.patternY = 0;
			this.area = EYEBROW_AREA;
			break;
		case LeftInnerEyebrow:
			this.neighbours.add(KnotType.LeftPupil);
			this.neighbours.add(KnotType.LeftOuterEyebrow);
			this.neighbours.add(KnotType.LeftInnerEye);
			this.neighbours.add(KnotType.RightInnerEye);
			this.patternX = 42;
			this.patternY = -2;
			this.area = EYEBROW_AREA;
			break;
		case RightInnerEye:
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.LeftInnerEye);
			this.neighbours.add(KnotType.RightInnerEyebrow);
			this.patternX = 76;
			this.patternY = 17;
			this.area = EYE_AREA;
			break;
		case RightInnerEyebrow:
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.LeftInnerEyebrow);
			this.neighbours.add(KnotType.RightInnerEye);
			this.neighbours.add(KnotType.RightOuterEyebrow);
			this.patternX = 73;
			this.patternY = 0;
			this.area = EYEBROW_AREA;
			break;
		case RightPupil:
			this.neighbours.add(KnotType.RightInnerEyebrow);
			this.neighbours.add(KnotType.RightOuterEyebrow);
			this.neighbours.add(KnotType.RightInnerEye);
			this.neighbours.add(KnotType.RightOuterEye);
			this.patternX = 89;
			this.patternY = 12;
			this.area = PUPIL_AREA;
			break;
		case RightOuterEyebrow:
			this.neighbours.add(KnotType.RightInnerEyebrow);
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.RightOuterEye);
			this.patternX = 114;
			this.patternY = -3;
			this.area = EYEBROW_AREA;
			break;
		case RightOuterEye:
			this.neighbours.add(KnotType.RightOuterEyebrow);
			this.neighbours.add(KnotType.RightPupil);
			this.neighbours.add(KnotType.MouthRight);
			this.patternX = 105;
			this.patternY = 14;
			this.area = EYE_AREA;
			break;
		case MouthLeft:
			this.neighbours.add(KnotType.LeftOuterEye);
			this.neighbours.add(KnotType.MouthUpper);
			this.neighbours.add(KnotType.MouthLower);
			this.patternX = 35;
			this.patternY = 79;
			this.area = MOUTH_AREA;
			break;
		case MouthLower:
			this.neighbours.add(KnotType.MouthRight);
			this.neighbours.add(KnotType.MouthUpper);
			this.neighbours.add(KnotType.MouthLeft);
			this.patternX = 63;
			this.patternY = 94;
			this.area = MOUTH_AREA;
			break;
		case MouthUpper:
			this.neighbours.add(KnotType.MouthLeft);
			this.neighbours.add(KnotType.MouthRight);
			this.neighbours.add(KnotType.MouthLower);
			this.patternX = 62;
			this.patternY = 70;
			this.area = MOUTH_AREA;
			break;
		case MouthRight:
			this.neighbours.add(KnotType.RightOuterEye);
			this.neighbours.add(KnotType.MouthUpper);
			this.neighbours.add(KnotType.MouthLower);
			this.patternX = 88;
			this.patternY = 78;
			this.area = MOUTH_AREA;
			break;
		}
	}

	/**
	 * @return the area
	 */
	public int getArea() {
		return area;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(int area) {
		this.area = area;
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
		double diff = patternX - this.patternX;
		for (KnotType type : KnotType.values()) {
			GridKnot knot = this.grid.getKnots().get(type);
			double value=knot.patternX + diff;
			knot.patternX=value;
		}
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
		double diff = patternY - this.patternY;
		for (KnotType type : KnotType.values()) {
			GridKnot knot = this.grid.getKnots().get(type);
			knot.patternY=(knot.patternY + diff);
		}
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
	public void findBestPlace() {
		// TODO: write method
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
