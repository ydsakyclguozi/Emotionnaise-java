/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author James
 *
 */
public class FeatureStore {
	private static final Logger Log = Logger.getLogger(FeatureStore.class
			.getName());
	// Points are stored relatively to array Mat in Face class
	private static Point[] features;
	private static Point[] neutralFeatures;

	// Colours definition
	private static final Scalar green;

	static {
		green = new Scalar(100, 240, 60);
		if (Log.isInfoEnabled()) {
			Log.info("Creating arrays for stroing face features points!");
		}
		features = new Point[14];
		neutralFeatures = new Point[14];
	}

	private FeatureStore() {

	}

	public static void setFeaturePoint(FaceFeatures feature, Point pt) {
		features[feature.getValue()] = pt;
	}

	public static Point getFeaturePoint(FaceFeatures feature) {
		return features[feature.getValue()];
	}


	/**
	 * @return the neutralFeatures
	 */
	public static Point[] getNeutralFeatures() {
		return neutralFeatures;
	}

	/**
	 * @param neutralFeatures the neutralFeatures to set
	 */
	public static void setNeutralFeatures(FaceFeatures feature, Point pt) {
		neutralFeatures[feature.getValue()] = pt;
	}

	public static void drawCross(Mat img, Point pt, Scalar color) {
		Imgproc.line(img, new Point(pt.x, pt.y - 5), new Point(pt.x, pt.y + 5),
				color, 1);

		Imgproc.line(img, new Point(pt.x - 5, pt.y), new Point(pt.x + 5, pt.y),
				color, 1);
	}
	
	public static void markFeatures(Face face) {
		for (Point point : features) {
			if (point == null) {
				Log.warn("Unable to mark feature!");
				continue;
			}
			drawCross(face.getFace(), point, green);
		}
		imwrite("markedFeatures.png", face.getFace());

	}
	
}
