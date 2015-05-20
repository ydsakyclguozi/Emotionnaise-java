/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.*;

import static org.opencv.imgcodecs.Imgcodecs.*;
import static org.opencv.imgproc.Imgproc.cvtColor;

import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author James
 *
 */
@Component("eyes")
public class Eyes implements FaceElement {
	private static final Logger Log = Logger.getLogger(Eyes.class.getName());

	private static final String xmlCascade = "E:\\Studia\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml";

	// Templates intended for right eye
	// Necessary correct flip
	private static final String innerCornerTemplate = "src\\main\\resources\\templates\\rightInner.jpg";
	private static final String outerCornerTemplate = "src\\main\\resources\\templates\\rightOuter.jpg";

	// Necessary translation due to corner localization on the template
	private static final int xRightOuter = 6;
	private static final int yRightOuter = 6;
	private static final int xRightInner = 4;
	private static final int yRightInner = 7;
	private static final int xLeftOuter = 6;
	private static final int yLeftOuter = 6;
	private static final int xLeftInner = 8;
	private static final int yLeftInner = 7;

	@Autowired
	private Face faceObj;
	private Mat face;
	private Mat leftEye;
	private Mat rightEye;
	private Rect leftEyeRect;
	private Rect rightEyeRect;

	public Eyes() {
	}

	private void haarCascade4Eyes(String xmlcascade) {
		if (Log.isInfoEnabled()) {
			Log.info("Starting Haar cascade for eyes");
		}
		CascadeClassifier eyesCascade = null;
		try {
			eyesCascade = new CascadeClassifier(xmlcascade);
		} catch (Exception e) {
			Log.error("Error during loading cascade for eyes", e);
		}
		MatOfRect eyes = new MatOfRect();
		if (eyesCascade != null && this.face != null) {
			eyesCascade.detectMultiScale(this.face, eyes);
		} else {
			Log.error("Nulls during eyes localizating using Haar cascade");
			return;
		}
		if (eyes.toArray().length <= 0) {
			Log.error("Unable to detect eyes in the image");
			return;
		}
		for (int i = 0; i < eyes.toArray().length; i++) {
			Rect checkingTest = eyes.toArray()[i];
			if (checkingTest.x < this.face.width() / 2) {
				this.leftEyeRect = recalculate(checkingTest, face);
			} else {
				this.rightEyeRect = recalculate(checkingTest, face);
			}
		}
		if (eyes.toArray().length < 2) {
			Log.error("Unable to localize both eyes");
		} else if (this.leftEyeRect != null && this.rightEyeRect != null) {
			this.leftEye = new Mat(this.face, this.leftEyeRect);
			this.rightEye = new Mat(this.face, this.rightEyeRect);
			imwrite("detectedLeftEye.png", this.leftEye);
			imwrite("detectedRightEye.png", this.rightEye);
		} else {
			Log.error("Rectangles for eyes localized improperly!");
		}
	}

	private Rect recalculate(Rect rect, Mat bckg) {
		if (Log.isInfoEnabled()) {
			Log.info("Recalculating rectangle for an eye");
		}
		Rect output = new Rect();
		int width = (int) (rect.width * 1.3);
		int height = (int) (rect.height * 1.3);
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
			Log.info("Rectangle for an eye recalculated: WIDTH=" + output.width
					+ " HEIGHT=" + output.height + " (x,y)=" + output.x + ","
					+ output.y);
		}
		return output;
	}

	private void templateMatching() {
		if (Log.isInfoEnabled()) {
			Log.info("Loading templates for eye corner matching");
		}
		Mat innerTemplate = imread(innerCornerTemplate,CV_LOAD_IMAGE_GRAYSCALE);
		Mat outerTemplate = imread(outerCornerTemplate,CV_LOAD_IMAGE_GRAYSCALE);
		if (innerTemplate == null || outerTemplate == null) {
			Log.error("Unable to load templates");
			return;
		}
		Mat result = new Mat();
		Mat greyLeftEye = new Mat();
		Mat greyRightEye = new Mat();
		cvtColor(this.leftEye, greyLeftEye, Imgproc.COLOR_BGR2GRAY);
		cvtColor(this.rightEye, greyRightEye, Imgproc.COLOR_BGR2GRAY);
		if (Log.isInfoEnabled()) {
			Log.info("Grey copies of eyes created!");
		}
		Imgproc.medianBlur(greyLeftEye, greyLeftEye, 5);
		Imgproc.medianBlur(greyRightEye, greyRightEye, 5);
		if (Log.isInfoEnabled()) {
			Log.info("Grey copies blurred with median blur: mask 5x5");
		}

		// Creating partially cleared images to increase correlation
		Mat rightEye4Outer = greyRightEye.clone();
		Mat rightEye4Inner = greyRightEye.clone();
		Mat leftEye4Outer = greyLeftEye.clone();
		Mat leftEye4Inner = greyLeftEye.clone();
		// Clearing halves of an image
		for (int i = 0; i < greyRightEye.width() * 0.5; i++) {
			for (int j = 0; j < greyRightEye.height(); j++) {
				rightEye4Outer.put(j, i, new byte[] { 0, 0, 0 });
				rightEye4Inner.put(j, (int) (greyRightEye.width()-3-i),
						new byte[] { 0, 0, 0 });
			}
		}
		for (int i = 0; i < greyLeftEye.width() * 0.5; i++) {
			for (int j = 0; j < greyLeftEye.height(); j++) {
				leftEye4Inner.put(j, i, new byte[] { 0, 0, 0 });
				leftEye4Outer.put(j, (int) (greyLeftEye.width()-3 -i),
						new byte[] { 0, 0, 0 });
			}
		}
		
		//RIGHT EYE
		imwrite("rightEye4OuterMatching.png",rightEye4Outer);
		imwrite("rightEye4InnerMatching.png",rightEye4Inner);
		
		Imgproc.matchTemplate(rightEye4Outer, outerTemplate, result,
				Imgproc.TM_CCOEFF_NORMED);
		Core.normalize(result, result, 0, 100, Core.NORM_MINMAX);
		Core.MinMaxLocResult maxVal = Core.minMaxLoc(result);
		Point outerRightPoint = new Point(maxVal.maxLoc.x, maxVal.maxLoc.y);
		FeatureStore.setFeaturePoint(FaceFeatures.RightEyeOuterCorner,
				recalculateRightOuter(outerRightPoint));
		
		Imgproc.matchTemplate(rightEye4Inner, innerTemplate, result,
				Imgproc.TM_CCOEFF_NORMED);
		Core.normalize(result, result, 0, 100, Core.NORM_MINMAX);
		maxVal = Core.minMaxLoc(result);
		Point innerRightPoint = new Point(maxVal.maxLoc.x, maxVal.maxLoc.y);
		FeatureStore.setFeaturePoint(FaceFeatures.RightEyeInnerCorner,
				recalculateRightInner(innerRightPoint));
		
		Core.flip(innerTemplate, innerTemplate, 1);
		Core.flip(outerTemplate, outerTemplate, 1);
		
		//LEFT EYE
		imwrite("leftEye4OuterMatching.png",leftEye4Outer);
		imwrite("leftEye4InnerMatching.png",leftEye4Inner);
		
		Imgproc.matchTemplate(leftEye4Outer, outerTemplate, result,
				Imgproc.TM_CCOEFF_NORMED);
		Core.normalize(result, result, 0, 100, Core.NORM_MINMAX);
		maxVal = Core.minMaxLoc(result);
		Point outerLeftPoint = new Point(maxVal.maxLoc.x, maxVal.maxLoc.y);
		FeatureStore.setFeaturePoint(FaceFeatures.LeftEyeOuterCorner,
				recalculateLeftOuter(outerLeftPoint));
		
		Imgproc.matchTemplate(leftEye4Inner, innerTemplate, result,
				Imgproc.TM_CCOEFF_NORMED);
		Core.normalize(result, result, 0, 100, Core.NORM_MINMAX);
		maxVal = Core.minMaxLoc(result);
		Point innerLeftPoint = new Point(maxVal.maxLoc.x, maxVal.maxLoc.y);
		FeatureStore.setFeaturePoint(FaceFeatures.LeftEyeInnerCorner,
				recalculateLeftInner(innerLeftPoint));

	}

	private Point recalculateRightOuter(Point pt) {
		pt.x += xRightOuter;
		pt.x += this.rightEyeRect.x;
		pt.y += yRightOuter;
		pt.y+=this.rightEyeRect.y;
		if(Log.isInfoEnabled()){
			Log.info("Point for right outer eye corner set to: "+pt.x+","+pt.y);
		}
		return pt;
	}

	private Point recalculateRightInner(Point pt) {
		pt.x += xRightInner;
		pt.x+=this.rightEyeRect.x;
		pt.y += yRightInner;
		pt.y+=this.rightEyeRect.y;
		if(Log.isInfoEnabled()){
			Log.info("Point for right inner eye corner set to: "+pt.x+","+pt.y);
		}
		return pt;
	}
	
	private Point recalculateLeftOuter(Point pt) {
		pt.x += xLeftOuter;
		pt.x += this.leftEyeRect.x;
		pt.y += yLeftOuter;
		pt.y+=this.leftEyeRect.y;
		if(Log.isInfoEnabled()){
			Log.info("Point for left outer eye corner set to: "+pt.x+","+pt.y);
		}
		return pt;
	}

	private Point recalculateLeftInner(Point pt) {
		pt.x += xLeftInner;
		pt.x+=this.leftEyeRect.x;
		pt.y += yLeftInner;
		pt.y+=this.leftEyeRect.y;
		if(Log.isInfoEnabled()){
			Log.info("Point for left inner eye corner set to: "+pt.x+","+pt.y);
		}
		return pt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see face.elements.FaceElement#detectElement()
	 */
	public Mat detectElement() {
		if (Log.isInfoEnabled()) {
			Log.info("Starting detecting eyes");
		}
		this.face = faceObj.getFace().clone();
		if (this.face == null) {
			Log.error("Cannot clone face image for eyes detecting purposes");
		}

		this.haarCascade4Eyes(xmlCascade);
		this.templateMatching();
		// We don't return, images are stored in the class
		return null;
	}

	/**
	 * @return the leftEye
	 */
	public Mat getLeftEye() {
		return leftEye;
	}

	/**
	 * @param leftEye
	 *            the leftEye to set
	 */
	public void setLeftEye(Mat leftEye) {
		this.leftEye = leftEye;
	}

	/**
	 * @return the rightEye
	 */
	public Mat getRightEye() {
		return rightEye;
	}

	/**
	 * @param rightEye
	 *            the rightEye to set
	 */
	public void setRightEye(Mat rightEye) {
		this.rightEye = rightEye;
	}

	/**
	 * @return the leftEyeRect
	 */
	public Rect getLeftEyeRect() {
		return leftEyeRect;
	}

	/**
	 * @param leftEyeRect
	 *            the leftEyeRect to set
	 */
	public void setLeftEyeRect(Rect leftEyeRect) {
		this.leftEyeRect = leftEyeRect;
	}

	/**
	 * @return the rightEyeRect
	 */
	public Rect getRightEyeRect() {
		return rightEyeRect;
	}

	/**
	 * @param rightEyeRect
	 *            the rightEyeRect to set
	 */
	public void setRightEyeRect(Rect rightEyeRect) {
		this.rightEyeRect = rightEyeRect;
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

}
