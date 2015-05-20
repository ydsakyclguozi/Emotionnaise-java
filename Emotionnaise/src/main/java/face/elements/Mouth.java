/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
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

	private double mouthRoiFactor = 0.3;

	public Mouth() {
	}

	private void init() {
		this.face = faceObj.getFace();
		// We extarct just part of the face according to mouthRoiFactor
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

	private void mouthAlanysis() {
		if (this.mouth == null) {
			Log.error("Method invoke in inapropriate order or loading face unsuccessful");
		}
		//Preprocessing of the image
		Mat mouthAnalyzed = this.mouth.clone();
		Imgproc.cvtColor(mouthAnalyzed, mouthAnalyzed, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(mouthAnalyzed, mouthAnalyzed, new Size(5, 5), 0.9);
		if (Log.isInfoEnabled()) {
			Log.info("Gaussian blur apply for mouth ROI");
		}
		imwrite("mouthROIblurred.png", mouthAnalyzed);
		
		//Proper algorithm
		MatOfPoint features4mouth = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(mouthAnalyzed, features4mouth, 40, 0.00001,
				0);
		for (Point pt : features4mouth.toArray()) {
			FeatureStore.drawCross(mouthAnalyzed, pt, new Scalar(255,255,255));
		}
		imwrite("mouthROIblurredMarked"
				+ ".png", mouthAnalyzed);
		//Rejecting false positives
			//TODO: rejecting false positives
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see face.elements.FaceElement#detectElement()
	 */
	public Mat detectElement() {
		if (Log.isInfoEnabled()) {
			Log.info("Start mouth detection");
		}
		init();
		mouthAlanysis();
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

}
