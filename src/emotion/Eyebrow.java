/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emotion;

import java.util.ArrayList;
import org.opencv.core.CvType;
import static org.opencv.core.CvType.CV_8UC1;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 *
 * @author James
 */

public class Eyebrow {
    EyeRegion reg;
    Eye _eye;
    
    public Eyebrow(EyeRegion eyeReg,boolean rightEyeFlag)
    {
        this.reg=eyeReg;
        Mat eye=rightEyeFlag?Eye.rightEye.clone():Eye.leftEye.clone();
        
        Mat eyebrowROI=eye.clone();
        //cvtColor(eyebrowROI, eyebrowROI, Imgproc.COLOR_BGR2GRAY);

        eyebrowROI.convertTo(eyebrowROI, CvType.CV_32F);
//        Vector<Mat> channels=new Vector<>();  
//        split(eyebrowROI,channels);
//        imwrite("eyebrowROI.png", channels.get(0));
       
        Mat result=StaticFunctions.gabor(eyebrowROI);
        //threshold(result, result, 200,255, Imgproc.THRESH_BINARY_INV);
        
        imwrite("intermidiate.png",result);
        Harris(result,rightEyeFlag);
        imwrite("eyeafterGabor.png",result);
    }

    public static void Harris(Mat img,boolean rightEyeFlag)
    {
                  //Harris point extraction
        Mat harrisTestimg;
        harrisTestimg=img.clone();
         cvtColor(harrisTestimg,harrisTestimg, Imgproc.COLOR_BGR2GRAY);
         threshold(harrisTestimg, harrisTestimg, 200,255, Imgproc.THRESH_BINARY_INV);
         Mat struct=Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,
                new Size(3,3));
        erode(harrisTestimg, harrisTestimg, struct);
        dilate(harrisTestimg, harrisTestimg, struct);
         imwrite("intermediateHaaris.jpg",harrisTestimg);
         harrisTestimg.convertTo(harrisTestimg, CV_8UC1);
          ArrayList<MatOfPoint> contours = new ArrayList<>();
          Mat hierarchy = new Mat();
          
          Imgproc.findContours(harrisTestimg, contours, hierarchy, 
                  Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
          

          
          //System.out.println("Average Y for contours:");
          float []averageY=new float[contours.size()];
          for (int i=0;i<contours.size();++i) {
              //We calculate mean of Y coordinates for each contour
              for(int j=0;j<contours.get(i).total();++j)
              {
                  int val=(int)contours.get(i).toArray()[j].y;
                  averageY[i]+=val;                
              }
              averageY[i]/=contours.get(i).total();
              //System.out.println(i+") "+averageY[i]);
              
              if(averageY[i]<=img.height()/2 &&                 //We consider just up half of an image
                     contours.get(i).total()>=img.width() )     //and longer than threshold
                Imgproc.drawContours(harrisTestimg,contours,i, new Scalar(255,255,255));
              else
                  Imgproc.drawContours(harrisTestimg,contours,i, new Scalar(0,0,0));
        }
          
        MatOfPoint features=new MatOfPoint();
        Imgproc.goodFeaturesToTrack(harrisTestimg, features,100,0.00001,0);
        
        //We draw just 2 extreme points- first and last
        Point eyebrowsPoints[]=new Point[2];
        for(int i=0;i<features.toList().size();i++){
        if(i==0)
        {
            eyebrowsPoints[0]=new Point(harrisTestimg.width()/2,0);
            eyebrowsPoints[1]=new Point(harrisTestimg.width()/2,0);
        }
        if(features.toArray()[i].x<eyebrowsPoints[0].x && 
                features.toArray()[i].y<harrisTestimg.height()/2)
        {
            eyebrowsPoints[0]=features.toArray()[i];
        }
        if(features.toArray()[i].x>eyebrowsPoints[1].x && 
                features.toArray()[i].y<harrisTestimg.height()/2)
        {
            eyebrowsPoints[1]=features.toArray()[i];
        }
       }
        StaticFunctions.drawCross(img,eyebrowsPoints[1],
                    StaticFunctions.Features.EYEBROWS_ENDS );
            StaticFunctions.drawCross(img,eyebrowsPoints[0],
                    StaticFunctions.Features.EYEBROWS_ENDS );
            imwrite("testHaris.jpg",img);
        if(rightEyeFlag)
        {
           EyeRegion.rightInnerEyebrowsCorner=eyebrowsPoints[0];
           EyeRegion.rightInnerEyebrowsCorner.x+=Eye.rightRect.x;
           EyeRegion.rightInnerEyebrowsCorner.y+=Eye.rightRect.y;
           
           EyeRegion.rightOuterEyebrowsCorner=eyebrowsPoints[1];
           EyeRegion.rightOuterEyebrowsCorner.x+=Eye.rightRect.x;
           EyeRegion.rightOuterEyebrowsCorner.y+=Eye.rightRect.y;
        }
        else
        {
           EyeRegion.leftInnerEyebrowsCorner=eyebrowsPoints[1];
           EyeRegion.leftInnerEyebrowsCorner.x+=Eye.leftRect.x;
           EyeRegion.leftInnerEyebrowsCorner.y+=Eye.leftRect.y;
           
           EyeRegion.leftOuterEyebrowsCorner=eyebrowsPoints[0];
           EyeRegion.leftOuterEyebrowsCorner.x+=Eye.leftRect.x;
           EyeRegion.leftOuterEyebrowsCorner.y+=Eye.leftRect.y;
        }
    }
}
