/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emotion;
import org.opencv.core.*;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 *
 * @author James
 */
public class Emotion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
   System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   
   
   

        
        String path="E:\\Studia\\II YEAR\\Team Project\\Face database\\";
           Face face1=new Face(path+"1.jpg");

//        String path="src\\TestingSet\\emo8.jpg";
//        Face face1=new Face(path);
        
        face1.detectFace();
       
        
    }
    
}
