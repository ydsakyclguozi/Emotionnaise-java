/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emotion;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import static org.opencv.core.CvType.CV_8UC1;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.threshold;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author James
 * 
 * Notion as :left, right concern our perception-
 * left eye is where our left hand is
 */
public final class Eye {
    private final EyeRegion reg;
    public static Mat leftEye;
    public static Mat rightEye;
    public static Rect leftRect;
    public static Rect rightRect;
    
    public Eye(Mat _face){
        reg=null;
        CascadeClassifier eyes_cascade;
        eyes_cascade = new CascadeClassifier("E:\\Studia\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");
        
        //Detect faces and write eyeLine to array of rectangles
        MatOfRect eyes = new MatOfRect();
        eyes_cascade.detectMultiScale(_face,eyes);
        
        if(eyes.toArray().length==0) 
        {
            Logger.getLogger("No face found in the image!");
            return;
        }
        for(int i=0;i<eyes.toList().size();++i){
            Rect tempRect=eyes.toArray()[i].clone();
        
        if(tempRect.x<_face.width()/2){
            Eye.leftRect=recalculate(tempRect,_face);
            Eye.leftEye=new Mat(_face,Eye.leftRect);
            imwrite("leftEYe.jpg",Eye.leftEye);
        }
        else{
            Eye.rightRect=recalculate(tempRect,_face);
            Eye.rightEye=new Mat(_face,Eye.rightRect);
            imwrite("rightEye.jpg",Eye.rightEye);
        }
        }
         templatingOuterCorner(Eye.leftEye,false);
         templatingInnerCorner(Eye.leftEye,false);
         templatingOuterCorner(Eye.rightEye,true);
         templatingInnerCorner(Eye.rightEye,true);
    }
//    public Eye(EyeRegion eyeRegion, boolean rightEyeFlag)
//    {
//        reg=eyeRegion;
//        leftEye=new Mat(eyeRegion.eyeRegion, new Rect(
//        0,
//        0,
//        eyeRegion.eyeRegion.width()/2,
//        eyeRegion.eyeRegion.height()));
//        imwrite("leftEye.png", leftEye);
//        
//        rightEye=new Mat(eyeRegion.eyeRegion, new Rect(
//        eyeRegion.eyeRegion.width()/2,
//        0,
//        eyeRegion.eyeRegion.width()/2,
//        eyeRegion.eyeRegion.height()));
//        imwrite("rightEye.png", rightEye);
//        
//
//        
//        //Read template RIGHT OUTER CORNER
//        templatingOuterCorner(
//                rightEyeFlag?rightEye:leftEye,rightEyeFlag);
//        templatingInnerCorner(
//                rightEyeFlag?rightEye:leftEye, rightEyeFlag);
//        
////        examineEyeOpeness(true);
////        examineEyeOpeness(false);
//        
//        EyeRegion.showLegend();
//    }
    private void templatingOuterCorner(Mat eyeRegion,boolean rightEyeFlag)
    {
//        Mat template=imread("E:\\Studia\\II YEAR\\Team Project\\"
//                + "Face database\\eyecorners\\rightOuter.jpg",CV_8UC1);
        Mat template=imread("src\\Templates\\rightOuter.jpg",CV_8UC1);
        Mat temp=new Mat(eyeRegion.width(),eyeRegion.height(),CV_8UC1);
        cvtColor(eyeRegion, temp, Imgproc.COLOR_BGR2GRAY);
        temp=rightEyeFlag?
                new Mat(temp, new Rect((int)(temp.width()*0.5),
                0,(int)(temp.width()*0.5),temp.height())):
                new Mat(temp, new Rect(0,
                0,(int)(temp.width()*0.5),temp.height()));
        Mat result=new Mat(eyeRegion.width(),eyeRegion.height(),eyeRegion.type());

        //(9,9)- coordinates of eye outerCorner in the template
        if(rightEyeFlag)
        {
            imwrite("rightEyeForOuterTemplating.jpg",temp);
            Imgproc.matchTemplate(temp, template, result, Imgproc.TM_CCOEFF_NORMED);
            Core.normalize(result,result,0,100,Core.NORM_MINMAX);
            Core.MinMaxLocResult maxVal=Core.minMaxLoc(result);
            //(9,9)- coordinates of eye outerCorner in the template
            Point outerCorner=new Point(maxVal.maxLoc.x+9,maxVal.maxLoc.y+9);

            //Adjust coordinates according to whole face
            outerCorner.y+=Eye.rightRect.y;
            outerCorner.x+=Eye.rightRect.x;
            outerCorner.x+=temp.width();     //We examine just right half on the right eye
            ////////////////////////////////////////////
            EyeRegion.rightOuterEyeCorner=outerCorner;
        }
        else
        {
            imwrite("leftEyeForOuterTemplating.jpg",temp);
            Core.flip(template, template, 1);
            Imgproc.matchTemplate(temp, template, result, Imgproc.TM_CCOEFF_NORMED);
            Core.normalize(result,result,0,100,Core.NORM_MINMAX);
            Core.MinMaxLocResult maxVal=Core.minMaxLoc(result);
            
            Point outerCorner=new Point(maxVal.maxLoc.x+4,maxVal.maxLoc.y+9);
            //Adjust coordinates according to whole face
            outerCorner.y+=Eye.leftRect.y;
            outerCorner.x+=Eye.leftRect.x;
            ////////////////////////////////////////////
            EyeRegion.leftOuterEyeCorner=outerCorner;
        }
        //Mat tempw=reg._face.clone();
        //Face.drawCross(tempw, outerCorner);
        //imwrite("checkcorner.png",tempw);
        

    }    
    private void templatingInnerCorner(Mat eyeRegion,boolean rightEyeFlag)
    {
//        Mat template=imread("E:\\Studia\\II YEAR\\Team Project\\"
//                + "Face database\\eyecorners\\rightInner.jpg",CV_8UC1);
        Mat template=imread("src\\Templates\\rightInner.jpg",CV_8UC1);
        Mat temp=new Mat(eyeRegion.width(),eyeRegion.height(),CV_8UC1);
        cvtColor(eyeRegion, temp, Imgproc.COLOR_BGR2GRAY);
        temp=rightEyeFlag?
                new Mat(temp, new Rect(0,
                0,(int)(temp.width()*0.5),temp.height())):
                new Mat(temp, new Rect((int)(temp.width()*0.5),
                0,(int)(temp.width()*0.5),temp.height()));
        Mat result=new Mat(eyeRegion.width(),eyeRegion.height(),eyeRegion.type());

        //(4,7)- coordinates of eye innerCorner in the template
        if(rightEyeFlag)
        {
            imwrite("template4righteye.jpg",template);
            imwrite("rightEyeForInnerTemplating.jpg",temp);
            Imgproc.matchTemplate(temp, template, result, Imgproc.TM_CCOEFF_NORMED);
            Core.normalize(result,result,0,100,Core.NORM_MINMAX);
            Core.MinMaxLocResult maxVal=Core.minMaxLoc(result);
            //(4,7)- coordinates of eye innerCorner in the template
            Point innerCorner=new Point(maxVal.maxLoc.x+4,maxVal.maxLoc.y+7);

            StaticFunctions.drawCross(temp, innerCorner, StaticFunctions.Features.EYE_CORNERS);
            imwrite("rightEyeForInnerTemplating.jpg",temp);
            //Adjust coordinates according to whole face
            innerCorner.y+=Eye.rightRect.y;
            innerCorner.x+=Eye.rightRect.x;
             //We examine just left half on the right eye
            ////////////////////////////////////////////
            EyeRegion.rightInnerEyeCorner=innerCorner;
        }
        else
        {
            imwrite("leftEyeForInnerTemplating.jpg",temp);
            Core.flip(template, template, 1);
            Imgproc.matchTemplate(temp, template, result, Imgproc.TM_CCOEFF_NORMED);
            Core.normalize(result,result,0,100,Core.NORM_MINMAX);
            Core.MinMaxLocResult maxVal=Core.minMaxLoc(result);
            
            Point innerCorner=new Point(maxVal.maxLoc.x+8,maxVal.maxLoc.y+7);

            //Adjust coordinates according to whole face
            innerCorner.y+=Eye.leftRect.y;
            innerCorner.x+=Eye.leftRect.x;
            //We examine just right half on the left eye
            innerCorner.x+=temp.width();    
            ////////////////////////////////////////////
            EyeRegion.leftInnerEyeCorner=innerCorner;
        }
    }    
    public void examineEyeOpeness(boolean rightEyeFlag)
    {
       Rect pureEyeRegion;
       //We take just middle half of strict eye region determined
       //by localized eye corners
       if(rightEyeFlag){
           double regionWidth=EyeRegion.rightOuterEyeCorner.x-EyeRegion.rightInnerEyeCorner.x;
           pureEyeRegion=new Rect((int)(EyeRegion.rightInnerEyeCorner.x+regionWidth/2-2),
                   (int)(Eye.rightRect.y),
                   (4),Eye.rightRect.height);
                   imwrite("strictEyeRegRight.jpg",new Mat(EyeRegion._face,pureEyeRegion));
            //Setting x coordinates of eyelids
            EyeRegion.rightLowerEyelid.x=(EyeRegion.rightOuterEyeCorner.x+
                    EyeRegion.rightInnerEyeCorner.x)/2;
            EyeRegion.rightUpperEyelid.x=EyeRegion.rightLowerEyelid.x;
       }
       else{
           double regionWidth;
           regionWidth = EyeRegion.leftInnerEyeCorner.x-EyeRegion.leftOuterEyeCorner.x;
           pureEyeRegion=new Rect((int)(regionWidth/2+EyeRegion.leftOuterEyeCorner.x-2),
                   (int)(Eye.leftRect.y),
                   (4),Eye.leftRect.height);
                   imwrite("leftEyeReg.jpg",new Mat(EyeRegion._face,pureEyeRegion));
            //Setting x coordinates of eyelids
            EyeRegion.leftLowerEyelid.x=(EyeRegion.leftInnerEyeCorner.x+
                    EyeRegion.leftOuterEyeCorner.x)/2;
            EyeRegion.leftUpperEyelid.x=EyeRegion.leftLowerEyelid.x;
       }
       
        
         Mat strictEyeRegion=new Mat(EyeRegion._face,pureEyeRegion);
         Mat result=new Mat();
         
          strictEyeRegion.convertTo(strictEyeRegion, CvType.CV_32F);
        Core.pow(strictEyeRegion, 1.27, strictEyeRegion);
        cvtColor(strictEyeRegion, strictEyeRegion, Imgproc.COLOR_BGR2GRAY);
       imwrite("improved.jpg",strictEyeRegion);
       
    
        threshold(strictEyeRegion, result, 100, 255,Imgproc.THRESH_BINARY_INV);
       
        Mat strEl=Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,
                new Size(3, 1));
        dilate(result,result,strEl,new Point(1,0),3);
        
        for (int i = 0; i < result.width(); i++) {
            for (int j = 0; j < result.height()*0.4; j++) {
                result.put(j, i, new double[]{0,0,0});
            }
        }
            for (int j = result.height()-1; j >= 0; j--) {
                if(result.get(j, 0)[0]==255){
                    if(rightEyeFlag){
                        
                        if(EyeRegion.rightLowerEyelid.y==0){
                            EyeRegion.rightLowerEyelid.y=j+3;
                            EyeRegion.rightLowerEyelid.y+=Eye.rightRect.y;
                        }
                        EyeRegion.rightUpperEyelid.y=j;
                        EyeRegion.rightUpperEyelid.y+=Eye.rightRect.y;
                    }
                    else{
                       if(EyeRegion.leftLowerEyelid.y==0){
                            EyeRegion.leftLowerEyelid.y=j+3;
                            EyeRegion.leftLowerEyelid.y+=Eye.leftRect.y;
                        }
                        EyeRegion.leftUpperEyelid.y=j;
                        EyeRegion.leftUpperEyelid.y+=Eye.leftRect.y;
                    }
                }
            }
        imwrite("openessResult.jpg",result);
    }
    private Rect recalculate(Rect _input,Mat canvas){
        Rect output=new Rect();
        int width=(int) (_input.width*1.5);
        int height=(int) (_input.height*1.5);
        output.x=_input.x-(width-_input.width)/2;
        output.y=_input.y-(height)/4;
        if(output.x<0){
            output.x=0;
        }
        else if(output.x>=canvas.width()){
            output.x=canvas.width()-1;
        }
        if(output.y<0){
            output.y=0;
        }
        else if(output.y>=canvas.height()){
            output.y=canvas.height()-1;
        }
        output.width=width;
        output.height=height;
        return output;
    }
}

