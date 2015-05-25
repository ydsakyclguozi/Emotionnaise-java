package face.elements;

import org.opencv.core.Mat;

public interface FaceElement {
	public Mat detectElement(boolean neutral);
}
