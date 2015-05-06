/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emotion;

import java.util.logging.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import static org.opencv.imgcodecs.Imgcodecs.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author James
 */
public class Face {
    private Mat head;
    private Mat face;
    String path;
    
    private MatOfRect faceDetections;

    public Face(String path)
    {
        try
        {
             this.head=imread(path,CV_LOAD_IMAGE_COLOR);
        }
        catch(Exception ex)
        {
            System.err.println("Reading image not successful");
        }
    }
    public Face(Mat path)
    {
       this.head=path;
    }
    /**
     * Detecting face- area of intrests
     * This area will be used for further searching
     * Advantage: less data to analyze!
     * !!!This function has to be execute first!!!
     */
    public void detectFace()
    {
         //Loading Haars' classyfier
        CascadeClassifier face_cascade;
        face_cascade = new CascadeClassifier("E:\\Studia\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml");
        
        //Detect faces and write eyeLine to array of rectangles
        faceDetections = new MatOfRect();
        face_cascade.detectMultiScale(head,faceDetections);
        
        if(faceDetections.toArray().length==0) 
        {
            Logger.getLogger("No face found in the image!");
            return;
        }
        this.face=new Mat(head,faceDetections.toArray()[0]);
        //All templates for images 150x150px
        Imgproc.resize(this.face,this.face,new Size(150,150));
        
        //detectEyeRegions(this.face);
        EyeRegion eyeRegion=new EyeRegion(face);
        if(EyeRegion.unavailable){
            Logger.getLogger("Unable to localize eye region!");
            return;
        }
        Eye eyes=new Eye(this.face);
//        Eye eyes=new Eye(eyeRegion,false);
        new Eyebrow(eyeRegion,true);
        new Eyebrow(eyeRegion,false);
        eyes.examineEyeOpeness(true);
        eyes.examineEyeOpeness(false);
        EyeRegion.areEyebrowsWrinkles();
        eyeRegion.showFaceFeatures();
        eyeRegion.printData();
        
        //Saving area of intrest
            imwrite("face.jpg",face);
    }
    
   
    
}
