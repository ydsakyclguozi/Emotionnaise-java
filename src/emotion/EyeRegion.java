/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emotion;

import static java.lang.Math.abs;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 *
 * @author James
 */
public class EyeRegion {

    //Eye region parameters
//    static int eyeRegionStartY;
//    static int eyeRegionStartX;
//    static int eyeRegionWidth;
//    static int eyeRegionHeight;

    static long wrinklesThreshold;
    static long wrinklesFactor;
    
//    final Mat eyeRegion;
    static boolean unavailable=false;
//    Eye rightEye;
//    Eye leftEye;

    static Mat _face;
    //Points coordinates relatively to face image
    public static Point leftOuterEyeCorner;
    public static Point leftInnerEyeCorner;
    public static Point rightOuterEyeCorner;
    public static Point rightInnerEyeCorner;
    public static Point rightUpperEyelid;
    public static Point rightLowerEyelid;
    public static Point leftUpperEyelid;
    public static Point leftLowerEyelid;

    public static Point rightOuterEyebrowsCorner;
    public static Point rightInnerEyebrowsCorner;
    public static Point leftOuterEyebrowsCorner;
    public static Point leftInnerEyebrowsCorner;
    
    public static double rightEyeOpeness;
    public static double leftEyeOpeness;

    public EyeRegion(Mat face) {
        _face = face;
        leftOuterEyeCorner = new Point();
        leftInnerEyeCorner = new Point();
        rightOuterEyeCorner = new Point();
        rightInnerEyeCorner = new Point();
        rightUpperEyelid = new Point();
        rightLowerEyelid = new Point();
        leftUpperEyelid = new Point();
        leftLowerEyelid = new Point();

        rightOuterEyebrowsCorner = new Point();
        rightInnerEyebrowsCorner = new Point();
        leftOuterEyebrowsCorner = new Point();
        leftInnerEyebrowsCorner = new Point();
    }
//        face.convertTo(face, 0, 0.7, 0);
//
//        //imwrite("blurred.png",face);
//        //Create empty image of same size as input face
//        Mat image = new Mat(new Size(face.width(), face.height()),
//                face.type(), new Scalar(0, 0, 0));
//
//        //Subtracting BLUE channel from RED channel
//        Vector<Mat> channels = new Vector<>();
//        split(face, channels);
//        for (int i = 0; i < face.width(); ++i) {
//            for (int j = 0; j < face.height(); ++j) {
//                int value;
//                value = (int) (channels.get(2).get(i, j)[0] - channels.get(1).get(i, j)[0]);
//                image.put(i, j, new double[]{value, value, value});
//            }
//        }
//        imwrite("RED-BLUEimage.png", image);
//
//        image.convertTo(image, 0, 5, 0);
//        //imwrite("contrasted.png",image);
//        threshold(image, image, 5, 255, Imgproc.THRESH_BINARY);
//
//        //Morphological closing
//        Mat mask = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(5, 5));
//        erode(image, image, mask);
//        dilate(image, image, mask);
//
//        //Clearing bottom half on an image (eyes are in top half
//        for (int i = image.height() / 2; i < image.height(); i++) {
//            for (int j = 0; j < face.width(); j++) {
//                image.put(i, j, new double[]{255, 255, 255});
//            }
//        }
//        imwrite("bottomCleaned.png", image);
//
//        //Mirror reflection
//        Mat flippedImage = new Mat();
//        Core.flip(image, flippedImage, 1);
//
//        //Bit xor operator- more visible eyes
//        Core.bitwise_xor(image, flippedImage, image);
//
//        //We are intrested just in places of changing intensity- gradient operator
//        int[] gradientMask = new int[9];
//        gradientMask[0] = -1;
//        gradientMask[1] = -5;
//        gradientMask[2] = -1;
//        gradientMask[3] = 0;
//        gradientMask[4] = 0;
//        gradientMask[5] = 0;
//        gradientMask[6] = 1;
//        gradientMask[7] = 5;
//        gradientMask[8] = 1;
//
//        image = StaticFunctions.convolution(gradientMask, image);
//
//        //Vertical projection to find proper extrema
//        long hist[] = new long[image.width()];
//        for (int i = 0; i < image.width(); i++) {
//            for (int j = 0; j < image.height(); j++) {
//                hist[i] += image.get(j, i)[0];
//            }
//
//        }
//
//        //Vertical projection
//        //We process just middle 3/5 of the face
//        boolean leftOut = false, leftInn = false,
//                rightInn = false, rightOut = false;
//        for (int i = (int) (image.width() * 0.2); i < (int) (image.width() * 0.8); i++) {
//            if (i < image.width() / 2) {
//                if (hist[i] > 0 && hist[i - 1] < hist[i] && leftOut == false) {
//                    EyeRegion.leftOuterEyeCorner.x = i;
//                    leftOut = true;
//                } else if (hist[i] > 0 && hist[i + 1] < hist[i] && leftInn == false) {
//                    EyeRegion.leftInnerEyeCorner.x = i + 10;
//                    leftInn = true;
//                }
//            } else {
//                if (hist[i] > 0 && hist[i - 1] == 0 && rightInn == false) {
//                    EyeRegion.rightInnerEyeCorner.x = i;
//                    rightInn = true;
//                } else if (hist[i] > 0 && hist[i + 1] == 0 && rightOut == false) {
//                    EyeRegion.rightOuterEyeCorner.x = i;
//                    rightOut = true;
//                }
//            }
//
//        }
//        /*
//         * We look for y coordinate determnining the highest sum
//         * of the intenisties of detected x points
//         */
//        int max = 0, y = 0;
//        for (int i = 0; i < image.height(); ++i) {
//            int temp = 0;
//            temp = (int) (image.get(i,
//                    (int) EyeRegion.leftOuterEyeCorner.x)[0]);
//            temp += (int) (image.get(i,
//                    (int) EyeRegion.leftInnerEyeCorner.x)[0]);
//            temp += (int) (image.get(i,
//                    (int) EyeRegion.rightInnerEyeCorner.x)[0]);
//            temp += (int) (image.get(i,
//                    (int) EyeRegion.rightOuterEyeCorner.x)[0]);
//            if (temp > max) {
//                max = temp;
//                y = i;
//            }
//        }
//        EyeRegion.leftOuterEyeCorner.y = y;
//        EyeRegion.leftInnerEyeCorner.y = y;
//        EyeRegion.rightInnerEyeCorner.y = y;
//        EyeRegion.rightOuterEyeCorner.y = y;
//
//       
//
//        //Face geometry factors
//        //Region of intrest- 2/5 of face height
//        float faceFactorWidth = image.width() / 8;
//        float faceFactorHeight = image.height() / 5;
//
//        int averageY = (int) (EyeRegion.rightInnerEyeCorner.y + EyeRegion.leftInnerEyeCorner.y);
//        averageY /= 2;
//        
//        EyeRegion.eyeRegionStartY = (int) (averageY - faceFactorHeight);
//        EyeRegion.eyeRegionStartX = (int) (faceFactorWidth);
//        EyeRegion.eyeRegionWidth = (int) (face.width() - 2 * faceFactorWidth);
//        EyeRegion.eyeRegionHeight = (int) (faceFactorHeight * 1.5);
//        if(averageY<=0){
//            Logger.getLogger("Eye ragion impossible to detect!", null);
//        }
//        if( EyeRegion.eyeRegionStartY<=0 || EyeRegion.eyeRegionStartX<=0 ||
//                EyeRegion.eyeRegionWidth<=0 || EyeRegion.eyeRegionHeight<=0){
//            EyeRegion.eyeRegionStartY = 0;
//        EyeRegion.eyeRegionStartX = 0;
//        EyeRegion.eyeRegionWidth = 0;
//        EyeRegion.eyeRegionHeight = 0;
//        EyeRegion.unavailable=true;
//        }
//        Mat eyeArea;
//        eyeArea = new Mat(face, new Rect(
//                EyeRegion.eyeRegionStartX,
//                EyeRegion.eyeRegionStartY,
//                EyeRegion.eyeRegionWidth,
//                EyeRegion.eyeRegionHeight));
//        imwrite("eyeRegion.png", eyeArea);
//        this.eyeRegion = eyeArea;
//
//    }
//}
    public static void areEyebrowsWrinkles() {
        //setting parameters
        int height=(int) ( abs(rightInnerEyebrowsCorner.y-rightInnerEyeCorner.y)*1.2);
        int width=(int) (rightInnerEyeCorner.x-leftInnerEyeCorner.x);
        int y= (int) (rightInnerEyebrowsCorner.y-height/2);
        int x=(int) leftInnerEyebrowsCorner.x;
        
        Rect wrinklesRect=new Rect(x,y,width,height);
        Mat wrinklesArea = new Mat(_face, wrinklesRect).clone();
        
        wrinklesThreshold=(int) (wrinklesArea.width()*wrinklesArea.height()*0.085);
        //Wrinkles between eyebrows are vertical
        int[] gradientMask = new int[9];
        gradientMask[0] = -1;
        gradientMask[1] = 0;
        gradientMask[2] = 1;
        gradientMask[3] = -5;
        gradientMask[4] = 0;
        gradientMask[5] = 5;
        gradientMask[6] = -1;
        gradientMask[7] = 0;
        gradientMask[8] = 1;
        
        wrinklesArea.convertTo(wrinklesArea, CvType.CV_32F);
        Imgproc.cvtColor(wrinklesArea,wrinklesArea,Imgproc.COLOR_BGR2GRAY);
        Core.pow(wrinklesArea, 1.09, wrinklesArea);
        imwrite("wrinklesArea.jpg", wrinklesArea);

        wrinklesArea = StaticFunctions.convolution(gradientMask, wrinklesArea);
        threshold(wrinklesArea, wrinklesArea, 110, 255, Imgproc.THRESH_BINARY);
        imwrite("wrinklesAreaGradiented.jpg", wrinklesArea);
        
        long wrinklesPoints=0;
        for (int i = 0; i < wrinklesArea.width(); i++) {
            for (int j = 0; j < wrinklesArea.height(); j++) {
                if(wrinklesArea.get(j, i)[0]==255){
                    wrinklesPoints++;
                }
            }
        }
        EyeRegion.wrinklesFactor=wrinklesPoints;
//        System.out.println("Wrinkles factor: "+wrinklesPoints);
        if(wrinklesPoints>=wrinklesThreshold){
//            System.out.println("Expression wrinkles detected! Threshold exceeded");
            Imgproc.rectangle(EyeRegion._face, wrinklesRect.br(),
                    wrinklesRect.tl(), new Scalar(0,50,205));
        }
    }
    public static void showLegend(){
        StaticFunctions.drawCross(EyeRegion._face, 
                new Point(5,5), 
                StaticFunctions.Features.EYEBROWS_ENDS);
        Imgproc.putText(EyeRegion._face,"Eyebrows ends",new Point(12,7),
                Core.FONT_HERSHEY_SIMPLEX,0.3,new Scalar(255,255,255));
        
        StaticFunctions.drawCross(EyeRegion._face, 
                new Point(5,15), 
                StaticFunctions.Features.EYE_CORNERS);
        Imgproc.putText(EyeRegion._face,"Eyes' corners",new Point(12,17),
                Core.FONT_HERSHEY_SIMPLEX,0.3,new Scalar(255,255,255));
        
        StaticFunctions.drawCross(EyeRegion._face, 
                new Point(5,25), 
                StaticFunctions.Features.EYELIDS);
        Imgproc.putText(EyeRegion._face,"Eyelids",new Point(12,27),
                Core.FONT_HERSHEY_SIMPLEX,0.3,new Scalar(255,255,255));
    }
    public void showFaceFeatures() {
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.rightInnerEyebrowsCorner, 
                StaticFunctions.Features.EYEBROWS_ENDS);
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.rightOuterEyebrowsCorner,
                StaticFunctions.Features.EYEBROWS_ENDS);
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.rightOuterEyeCorner,
                StaticFunctions.Features.EYE_CORNERS);
        StaticFunctions.drawCross(EyeRegion._face,
                EyeRegion.leftOuterEyeCorner,
                StaticFunctions.Features.EYE_CORNERS);
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.leftInnerEyeCorner,
                StaticFunctions.Features.EYE_CORNERS);
        StaticFunctions.drawCross(EyeRegion._face,
                EyeRegion.rightInnerEyeCorner,
                StaticFunctions.Features.EYE_CORNERS);
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.leftInnerEyebrowsCorner,
                StaticFunctions.Features.EYEBROWS_ENDS);
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.leftOuterEyebrowsCorner,
                StaticFunctions.Features.EYEBROWS_ENDS);
        StaticFunctions.drawCross(EyeRegion._face,
                EyeRegion.rightLowerEyelid,
                StaticFunctions.Features.EYELIDS);
        StaticFunctions.drawCross(EyeRegion._face,
                EyeRegion.rightUpperEyelid,
                StaticFunctions.Features.EYELIDS);
        StaticFunctions.drawCross(EyeRegion._face,
                EyeRegion.leftLowerEyelid,
                StaticFunctions.Features.EYELIDS);
        StaticFunctions.drawCross(EyeRegion._face, 
                EyeRegion.leftUpperEyelid,
                StaticFunctions.Features.EYELIDS);
        imwrite("Result.jpg", _face);
    }
    public void printData(){
        System.out.println();
        System.out.println("Wrinkles (between eyebrows) factor: "+
                EyeRegion.wrinklesFactor);
        if(EyeRegion.wrinklesFactor>=EyeRegion.wrinklesThreshold){
            System.out.println("Expressing wrinkles detected! Threshold exceeded");
        }
        
        System.out.println("LEFT SIDE PARAMETERS");
        System.out.println("=====================");
        System.out.println("Left outer eye corner at "+
                EyeRegion.leftOuterEyeCorner );
        System.out.println("Left inner eye corner at "+
                EyeRegion.leftInnerEyeCorner );
        System.out.println("Left eyebrows outer point at "+
                EyeRegion.leftOuterEyebrowsCorner );
        System.out.println("Left eyebrows inner point at "+
                EyeRegion.leftInnerEyebrowsCorner );
        System.out.println("Left upper eyelid at "+
                EyeRegion.leftUpperEyelid );
        System.out.println("Left lower eyelid at "+
                EyeRegion.leftLowerEyelid );
        
        System.out.println();
        System.out.println("RIGHT SIDE PARAMETERS");
        System.out.println("=====================");
        System.out.println("Right outer eye corner at "+
                EyeRegion.rightOuterEyeCorner );
        System.out.println("Right inner eye corner at "+
                EyeRegion.rightInnerEyeCorner );
        System.out.println("Right eyebrows outer point at "+
                EyeRegion.rightOuterEyebrowsCorner );
        System.out.println("Right eyebrows inner point at "+
                EyeRegion.rightInnerEyebrowsCorner );
        System.out.println("Right upper eyelid at "+
                EyeRegion.rightUpperEyelid );
        System.out.println("Right lower eyelid at "+
                EyeRegion.rightLowerEyelid );
        
        
    }
}
