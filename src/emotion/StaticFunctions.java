/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emotion;

import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author James
 */
public class StaticFunctions {
    static public enum Features{
        EYE_CORNERS,
        EYEBROWS_ENDS,
        EYELIDS,
        WHITE_MARK
    }
    static void drawCross(Mat img, Point pt, Features feat)
    {
        Scalar col;
        switch(feat){
            case EYEBROWS_ENDS:
                col=new Scalar(0,255,0);
                break;
            case EYELIDS:
                col=new Scalar(100,210,255);
                break;
            case EYE_CORNERS:
                col=new Scalar(220,180,30);
                break;
            case WHITE_MARK:
                col=new Scalar(255,255,255);
            default:
                col=new Scalar(255,255,255);
                    
        }
        Imgproc.line(img,new Point(
                pt.x,
                pt.y-5),
                new Point(
                pt.x,
                pt.y+5),
                col,
                1);
        
        Imgproc.line(img,new Point(
                pt.x-5,
                pt.y),
                new Point(
                pt.x+5,
                pt.y),
                col,
                1);
    }
    static public Mat convolution(int [] mask, Mat image)
    {
        if(mask.length!=9)  return null;
        
        //output image
       Mat destination = new Mat(image.rows(),image.cols(),image.type());
       
       //Convolution kernel
       Mat kernel = new Mat(3,3, CvType.CV_32F){
            {
                for(int i=0,it=0;i<3;++i)
                {
                    for(int j=0;j<3;++j,++it)
                    {
                        put(i,j,mask[it]);                        
                    }
                }
               
            }
         };
        
        Imgproc.filter2D(image,destination,-1,kernel);
              
        imwrite("convol.jpg",destination);
        return destination;
    }
    public static Mat gabor(Mat image)
    {
        Mat img=image.clone();

        double ksize=15;
        double sigme=4;
        double gamma=1;
        double psi=50;
        int lambd[]=new int[]{5,6,7,10/*,15,13,2*/};
        double theta[]=new double[]{180,200};
        ArrayList<Mat> kernels=new ArrayList<>();
        for (int i = 0; i < theta.length; i++) {
            for (int j = 0; j < lambd.length; j++) {
            kernels.add(Imgproc.getGaborKernel(new Size(ksize,ksize),
                        sigme, theta[i], lambd[j], gamma,psi, CvType.CV_32F));
            }
        }

        Mat result=new Mat(img.height(),img.width(),img.type(),new Scalar(0,0,0));
        for (Mat kernel : kernels) {
            Mat temp=new Mat(img.height(),img.width(),img.type(),new Scalar(0,0,0));
            Imgproc.filter2D(img, temp, -1, kernel);
            Core.add(result, temp, result);
        }
       
        //imwrite("gaborResult.jpg",result);
        return result;
    }
}
