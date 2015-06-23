package emotion.grid;

import static org.junit.Assert.assertNotNull;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

public class main {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Grid grid=Grid.getInstance();
		Mat image=Imgcodecs.imread("src\\test\\resources\\testFaces\\1.jpg");
		Imgcodecs.imwrite("testFace.jpg", image);
		assertNotNull(image);
		grid.placeGrid(new Point(69,125), new Point(136,126));
		GridKnot knot=grid.getKnots().get(KnotType.LeftInnerEye);
		knot.findBestPlace(image);
	}

}
