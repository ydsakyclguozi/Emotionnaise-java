/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author James
 *
 */
@Component("eyebrows")
public class Eyebrows implements FaceElement {

	private static final Logger Log = Logger
			.getLogger(Eyebrows.class.getName());

	@Autowired
	private Eyes eyes;
	
	//Classified points fro eyebrows corners
	List<Point> classyfiedPoints4Right;
	List<Point> classyfiedPoints4Left;

	public Eyebrows() {
	}

	private void haaris4Eyes() {
		final int rejectThreshold4RightEye = (int) (eyes.getRightEye().height() * 0.1);
		final int rejectThreshold4LeftEye = (int) (eyes.getLeftEye().height() * 0.1);
		if (Log.isInfoEnabled()) {
			Log.info("Start preparing image for Haaris corner detection.");
		}
		// Fix threshold for rejecting corners
		final int rightEyeHeightThreshold = this.eyes.getRightEye().height() / 2;
		final int leftEyeHeightThreshold = this.eyes.getLeftEye().height() / 2;

		// Initial processing
		Mat greyRightEye = new Mat();
		Imgproc.cvtColor(this.eyes.getRightEye().clone(), greyRightEye,
				Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(greyRightEye, greyRightEye, 5);
		Imgproc.Canny(greyRightEye, greyRightEye,80 ,160);
		imwrite("preprocessed4EyebrowsDetectionRightEye.png",greyRightEye);

		Mat greyLeftEye = new Mat();
		Imgproc.cvtColor(this.eyes.getLeftEye().clone(), greyLeftEye,
				Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(greyLeftEye, greyLeftEye, 5);
		Imgproc.Canny(greyLeftEye, greyLeftEye,80 ,160);
		imwrite("preprocessed4EyebrowsDetectionLeftEye.png",greyLeftEye);

		// Proper algorithm
		MatOfPoint features4rightEye = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(greyRightEye, features4rightEye, 40,
				0.00001, 0);
		MatOfPoint features4leftEye = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(greyLeftEye, features4leftEye, 40, 0.00001,
				0);

		// Initial processing- rejecting corners below thresholds
		classyfiedPoints4Right = new ArrayList<Point>();
		for (Point pt : features4rightEye.toArray()) {
			if (pt.y < rightEyeHeightThreshold) {
				classyfiedPoints4Right.add(pt);
			}
		}
		classyfiedPoints4Left = new ArrayList<Point>();
		for (Point pt : features4leftEye.toArray()) {
			if (pt.y < leftEyeHeightThreshold) {
				classyfiedPoints4Left.add(pt);
			}
		}
		if (Log.isInfoEnabled()) {
			Log.info("Rejecting false positives for Haaris corner detector");
		}

		// Further constraints for elimination fo false positives
		// TODO: rejecting false positives for both eyes
		rejectFalses(classyfiedPoints4Right, rejectThreshold4RightEye);
		rejectFalses(classyfiedPoints4Left, rejectThreshold4LeftEye);
		// Drawing points
		for (Point point : classyfiedPoints4Right) {
			FeatureStore.drawCross(this.eyes.getRightEye(), point, new Scalar(255, 255,
					255));
		}
		for (Point point : classyfiedPoints4Left) {
			FeatureStore.drawCross( this.eyes.getLeftEye(), point,
					new Scalar(255, 255, 255));
		}
		imwrite("haarisCorner4Right.png", this.eyes.getRightEye());
		imwrite("haarisCorner4Left.png", this.eyes.getLeftEye());
	}

	private void rejectFalses(List<Point> points, int deviationThreshold) {
		if (points.size() <= 0) {
			Log.error("Empty list of points!");
			return;
		}
		// Order according to Y coordinate
		Collections.sort(points, new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				if (o1.y < o2.y) {
					return -1;
				} else if (o1.y == o2.y) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		int medianY = (int) points.get(points.size() / 2).y;
		if (Log.isInfoEnabled()) {
			Log.info("Median Y at: " + medianY);
		}
		int rejects = 0;
		for (int i = 0; i < points.size(); i++) {
			if (Math.abs(points.get(i).y - medianY) <= deviationThreshold) {
				continue;
			} else {
				points.remove(i);
				//Size shrink by 1 thus index also has to
				i--;
				rejects++;
			}
		}
		if (Log.isInfoEnabled()) {
			Log.info("Rejected points: " + rejects + " during Y thresholding");
		}
		if (points.size() == 2) {
			Log.info("Two eyebrows corners detected!");
			return;
		} else if (points.size() < 2) {
			Log.warn("Unable to localize one of the eybrow corner!");
			return;
		}
		// Order according to X coordinate
		Collections.sort(points, new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				if (o1.x < o2.x) {
					return -1;
				} else if (o1.x == o2.x) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		//We save just 2 extreme points
		Point pt1=points.get(0);
		Point pt2=points.get(points.size()-1);
		points.clear();
		points.add(pt1);
		points.add(pt2);
		//Recalculation of the points relatively to the face image
		if (Log.isInfoEnabled()) {
			Log.info("Eyebrows' corners localized at positions x,y: "+
		pt1.x+","+pt1.y+" and "+pt2.x+","+pt2.y+" in eyeROI image!");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see face.elements.FaceElement#detectElement()
	 */
	public Mat detectElement() {
		if (Log.isInfoEnabled()) {
			Log.info("Starting detecting eyebrows");
		}
		haaris4Eyes();
		
		//Recalculate points relatively to the whole face
			//LEFT EYE
		if(classyfiedPoints4Left.size()!=2){
			Log.error("Inappropriate value of eye corners!");
			return null;
		}
		Point pt1=classyfiedPoints4Left.get(0);
		Point pt2=classyfiedPoints4Left.get(1);
		pt1.x+=this.eyes.getLeftEyeRect().x;
		pt1.y+=this.eyes.getLeftEyeRect().y;
		pt2.x+=this.eyes.getLeftEyeRect().x;
		pt2.y+=this.eyes.getLeftEyeRect().y;
		if (Log.isInfoEnabled()) {
			Log.info("Eyebrows' corners for the left eyebrow localized at positions x,y: "+
		pt1.x+","+pt1.y+" and "+pt2.x+","+pt2.y+" relatively to face image!");
		}
		FeatureStore.setFeaturePoint(FaceFeatures.LeftEyebrowOuterCorner, pt1);
		FeatureStore.setFeaturePoint(FaceFeatures.LeftEyebrowInnerCorner, pt2);
			//Right EYE
		if(classyfiedPoints4Right.size()!=2){
			Log.error("Inappropriate value of eye corners!");
			return null;
		}
		Point pt3=classyfiedPoints4Right.get(0);
		Point pt4=classyfiedPoints4Right.get(1);
		pt3.x+=this.eyes.getRightEyeRect().x;
		pt3.y+=this.eyes.getRightEyeRect().y;
		pt4.x+=this.eyes.getRightEyeRect().x;
		pt4.y+=this.eyes.getRightEyeRect().y;
		if (Log.isInfoEnabled()) {
			Log.info("Eyebrows' corners for the right eyebrow localized at positions x,y: "+
		pt3.x+","+pt3.y+" and "+pt4.x+","+pt4.y+" relatively to face image!");
		}
		FeatureStore.setFeaturePoint(FaceFeatures.RightEyebrowInnerCorner, pt3);
		FeatureStore.setFeaturePoint(FaceFeatures.RightEyebrowOuterCorner, pt4);
		

		return null;
	}

	/**
	 * @return the eyes
	 */
	public Eyes getEyes() {
		return eyes;
	}

	/**
	 * @param eyes
	 *            the eyes to set
	 */
	public void setEyes(Eyes eyes) {
		this.eyes = eyes;
	}

}
