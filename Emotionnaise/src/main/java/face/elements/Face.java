/**
 * 
 */
package face.elements;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author James
 *
 */
@Component("face")
public class Face implements FaceElement {
	private static final Logger Log = Logger.getLogger(Face.class.getName());

	private static final String xmlCascade = "E:\\Studia\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml";

	private String path;
	private String path4expressinFace;
	private Mat head;
	private Mat headExpressing;
	private Mat neutralFace;
	private Mat expressingFace;

	public Face() {
	}

	@Autowired
	public Face(
			@Value("src\\main\\resources\\head\\2.jpg") String neutralFacePath,
			@Value("src\\main\\resources\\head\\2.jpg") String expressingFacePath) {
		this.path = neutralFacePath;
		this.path4expressinFace = expressingFacePath;
		try {
			if (Log.isInfoEnabled()) {
				Log.info("Loading head image");
			}
			head = imread(this.path, CV_LOAD_IMAGE_COLOR);
			headExpressing = imread(this.path4expressinFace,
					CV_LOAD_IMAGE_COLOR);
		} catch (Exception e) {
			Log.error("Error occured during loading head image", e);
		}
	}

	public Mat detectElement(boolean neutral) {
		this.neutralFace = haarCascade4Face(xmlCascade, true);
		this.expressingFace = haarCascade4Face(xmlCascade, false);
		return null;
	}

	private Mat haarCascade4Face(String haarxml, boolean neutral) {

		// We use or neutral head or head expressing emotions
		Mat head = neutral ? this.head : this.headExpressing;

		if (Log.isInfoEnabled()) {
			Log.info("Starting Haar cascade for face");
		}
		CascadeClassifier faceCascade = null;
		try {
			faceCascade = new CascadeClassifier(haarxml);
		} catch (Exception e) {
			Log.error("Error during loading cascade for face", e);
		}
		MatOfRect faces = new MatOfRect();
		if (faceCascade != null) {
			faceCascade.detectMultiScale(head, faces);
		}
		if (Log.isInfoEnabled()) {
			Log.info("We take just first detected face in the image");
		}
		if (faces.toArray().length <= 0) {
			Log.error("Unable to detect face in the image");
			return null;
		}
		Mat face = new Mat(head, faces.toArray()[0]);

		// Algorithms adjust for face 127x127px
		if (Log.isInfoEnabled()) {
			Log.info("Resizing face image to 127x127px");
		}
		Imgproc.resize(face, face, new Size(127, 127));
		imwrite("detected&ResizedFace.png", face);
		return face;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the head
	 */
	public Mat getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(Mat head) {
		this.head = head;
	}

	/**
	 * @return the face
	 */
	public Mat getFace() {
		return neutralFace;
	}

	/**
	 * @param face
	 *            the face to set
	 */
	public void setFace(Mat face) {
		this.neutralFace = face;
	}

	public static int medianY(List<Point> points) {
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
		return (int) points.get(points.size() / 2).y;
	}

}
