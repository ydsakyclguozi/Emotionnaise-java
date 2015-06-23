/**
 * 
 */
package emotion.grid;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author James
 *
 */
public class GridTest {

	Grid grid;
	Point[][] points4Faces;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		grid = new Grid();
		//Index +1 means ID of image in the resources set
		//[.][0] means left pupil, [.][1]- right one
		points4Faces=new Point[21][2];
		points4Faces[0][0]=new Point(69,125);
		points4Faces[0][1]=new Point(136,126);
		points4Faces[1][0]=new Point(69,115);
		points4Faces[1][1]=new Point(123,115);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddingKnots() {
		assertNotNull(grid);
		Mat image=Imgcodecs.imread("src\\test\\resources\\testFaces\\2.jpg");
		Imgcodecs.imwrite("testFace.jpg", image);
		assertNotNull(image);
		grid.placeGrid(points4Faces[1][0],points4Faces[1][1]);
		grid.markPatternGrid(image);
		GridKnot knot=grid.getKnots().get(KnotType.LeftOuterEyebrow);
		grid.markGrid(image);
		grid.markPatternGrid(image);
		knot.findBestPlace(image);
		

	}

}
