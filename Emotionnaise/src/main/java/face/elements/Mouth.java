/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author James
 *
 */
@Component("mouth")
public class Mouth implements FaceElement {
	private static final Logger Log = Logger.getLogger(Mouth.class.getName());

	@Autowired
	private Face faceObj;
	private Mat face;
	private Mat mouth;
	private Rect mouthRect;

	private static final String xmlCascade = "E:\\Studia\\OpenCV\\opencv\\sources\\data\\haarcascades\\mouth.xml";

	private double mouthRoiFactor = 0.35;

	public Mouth() {
	}

	private void init() {
		this.face = faceObj.getFace();

		if (this.face == null) {
			Log.error("Face image was not detected. Unable to localize mouth.");
			return;
		}
		this.mouthRect = new Rect();
		if (Log.isInfoEnabled()) {
			Log.info("Choosing proper area of mouth detected by Haar cascade");
		}
		for (Rect rect : this.haar4Mouth(xmlCascade)) {
			if (rect.y > (this.face.height() * (1 - mouthRoiFactor))) {
				mouthRect = rect;
				break;
			}
		}
		if (Log.isInfoEnabled()) {
			Log.info("Choosen rectangle: " + this.mouthRect.toString());
		}
		this.mouthRect = recalculateRectangle(this.mouthRect, this.face);
		this.mouth = new Mat(this.face, mouthRect);
		imwrite("mouthROI.png", this.mouth);
	}

	private void initDeprected() {
		this.face = faceObj.getFace();

		// We extract just part of the face according to mouthRoiFactor
		this.mouth = new Mat(this.face, new Rect(0,
				(int) (this.face.height() * (1 - mouthRoiFactor)),
				this.face.width(), (int) (this.face.height() * mouthRoiFactor)));
		if (Log.isInfoEnabled()) {
			Log.info("Clone of mouth region ROI created");
		}
		if (this.mouth == null) {
			Log.error("Unable to localize mouth ROI");
			return;
		}
		imwrite("mouthROI.png", this.mouth);
	}

	private void mouthAlanysis(boolean neutral) {
		if (this.mouth == null) {
			Log.error("Method invoked in inapropriate order or loading face unsuccessful");
		}
		// Preprocessing of the image
		Mat mouthAnalyzed = this.mouth.clone();
		Imgproc.cvtColor(mouthAnalyzed, mouthAnalyzed, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(mouthAnalyzed, mouthAnalyzed, 3);
		if (Log.isInfoEnabled()) {
			Log.info("Median blur apply for mouth ROI");
		}
		imwrite("mouthROIblurred.png", mouthAnalyzed);

		// Proper algorithm
		MatOfPoint features4mouth = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(mouthAnalyzed, features4mouth, 10, 0.4, 0);
		// Rejecting false positives
		List<Point> points = rejectFlases(features4mouth.toList());
		// Drawing
		for (Point pt : points) {
			FeatureStore
					.drawCross(mouthAnalyzed, pt, new Scalar(255, 255, 255));
		}
		imwrite("mouthROIblurredMarked" + ".png", mouthAnalyzed);

		Point leftCorner = points.get(0);
		Point rightCorner = points.get(points.size() - 1);
		//Recalculating relatively to the whole face
		leftCorner.x+=this.mouthRect.x;
		rightCorner.x+=this.mouthRect.x;
		leftCorner.y+=this.mouthRect.y;
		rightCorner.y+=this.mouthRect.y;
		
		if (neutral) {
			FeatureStore.setNeutralFeatures(FaceFeatures.MouthLeftCorner,
					leftCorner);
			FeatureStore.setNeutralFeatures(FaceFeatures.MouthRightConrner,
					rightCorner);
		} else {
			FeatureStore.setFeaturePoint(FaceFeatures.MouthLeftCorner,
					leftCorner);
			FeatureStore.setFeaturePoint(FaceFeatures.MouthRightConrner,
					rightCorner);
		}
		if (Log.isInfoEnabled()) {
			Log.info("Left mouth corner localized at " + leftCorner.toString()
					+ ", right mouth corner at " + rightCorner.toString());
		}

	}
	
	private List<Point> rejectFlases(List<Point> input){
		Collections.sort(input,new Comparator<Point>() {

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
		return input;
	}

	private List<Rect> haar4Mouth(String haarxml) {
		// We use or neutral head or head expressing emotions

		if (Log.isInfoEnabled()) {
			Log.info("Starting Haar cascade for mouth");
		}
		CascadeClassifier mouthCascade = null;
		try {
			mouthCascade = new CascadeClassifier(haarxml);
		} catch (Exception e) {
			Log.error("Error during loading cascade for mouth", e);
		}
		MatOfRect mouth = new MatOfRect();
		if (mouthCascade != null) {
			mouthCascade.detectMultiScale(this.face, mouth);
		}
		if (Log.isInfoEnabled()) {
			Log.info("We take just first detected face in the image");
		}
		if (mouth.toArray().length <= 0) {
			Log.error("Unable to detect face in the image");
			return null;
		}
		Mat mouthDetected = new Mat(this.face, mouth.toArray()[1]);

		// Algorithms adjust for face 127x127px
		if (Log.isInfoEnabled()) {
			Log.info("Resizing face image to 127x127px");
		}
		imwrite("detectedMouth.png", mouthDetected);
		return mouth.toList();
	}

	private Rect recalculateRectangle(Rect rect, Mat bckg) {
		if (Log.isInfoEnabled()) {
			Log.info("Recalculating rectangle for a mouth");
		}
		Rect output = new Rect();
		int width = (int) (rect.width * 1.35);
		int height = rect.height;
		output.x = rect.x - (width - rect.width) / 2;
		output.y = rect.y - (height) / 4;
		if (output.x < 0) {
			output.x = 0;
		} else if (output.x >= bckg.width()) {
			output.x = bckg.width() - 1;
		}
		if (output.y < 0) {
			output.y = 0;
		} else if (output.y >= bckg.height()) {
			output.y = bckg.height() - 1;
		}
		output.width = width;
		output.height = height;
		if (Log.isInfoEnabled()) {
			Log.info("Rectangle for an mouth recalculated: "
					+ output.toString());
		}
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see face.elements.FaceElement#detectElement()
	 */
	public Mat detectElement(boolean neutral) {
		if (Log.isInfoEnabled()) {
			Log.info("Start mouth detection");
		}
		init();
		mouthAlanysis(neutral);
		return null;
	}

	/**
	 * @return the faceObj
	 */
	public Face getFaceObj() {
		return faceObj;
	}

	/**
	 * @param faceObj
	 *            the faceObj to set
	 */
	public void setFaceObj(Face faceObj) {
		this.faceObj = faceObj;
	}

	/**
	 * @return the face
	 */
	public Mat getFace() {
		return face;
	}

	/**
	 * @param face
	 *            the face to set
	 */
	public void setFace(Mat face) {
		this.face = face;
	}

	/**
	 * @return the mouth
	 */
	public Mat getMouth() {
		return mouth;
	}

	/**
	 * @param mouth
	 *            the mouth to set
	 */
	public void setMouth(Mat mouth) {
		this.mouth = mouth;
	}

	/**
	 * @return the mouthRect
	 */
	public Rect getMouthRect() {
		return mouthRect;
	}

	/**
	 * @param mouthRect
	 *            the mouthRect to set
	 */
	public void setMouthRect(Rect mouthRect) {
		this.mouthRect = mouthRect;
	}

}
