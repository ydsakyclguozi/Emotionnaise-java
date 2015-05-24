/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import java.util.ArrayList;
import java.util.List;

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

	private void mouthAlanysis() {
		if (this.mouth == null) {
			Log.error("Method invoke in inapropriate order or loading face unsuccessful");
		}
		//Preprocessing of the image
		Mat mouthAnalyzed = this.mouth.clone();
		Imgproc.cvtColor(mouthAnalyzed, mouthAnalyzed, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(mouthAnalyzed, mouthAnalyzed, 5);
		if (Log.isInfoEnabled()) {
			Log.info("Median blur apply for mouth ROI");
		}
		imwrite("mouthROIblurred.png", mouthAnalyzed);
		
		//Proper algorithm
		MatOfPoint features4mouth = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(mouthAnalyzed, features4mouth, 40, 0.00001,
				0);
		//Rejecting false positives
		List<Point> points=rejectFalses(features4mouth.toList());
		//Drawing
		for (Point pt : points) {
			FeatureStore.drawCross(mouthAnalyzed, pt, new Scalar(255,255,255));
		}
		imwrite("mouthROIblurredMarked"
				+ ".png", mouthAnalyzed);
		
	}

	private List<Point> rejectFalses(List<Point> points){
		final int xThreshold=(int) (this.mouth.width()/5f);
		int rejected=0;
		List<Point> filtredList=new ArrayList<Point>();
		//Reject points outside face- from 1/5 of the image from both sides
		for (Point point : points) {
			if(point.x<=xThreshold || point.x>=(this.face.width()-xThreshold)){
				rejected++;
				continue;
			}else{
				filtredList.add(point);
			}
		}
		if (Log.isInfoEnabled()) {
			Log.info("Points rejected during filtrating: "+rejected);
		}
		
		
		return filtredList;
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
