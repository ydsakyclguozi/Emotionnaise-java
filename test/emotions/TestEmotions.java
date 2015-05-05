package emotions;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import emotion.Face;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opencv.core.Mat;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 *
 * @author James
 */
public class TestEmotions {
    
    Mat[] testSet;
    public TestEmotions() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testSet=new Mat[8];
        for (int i = 1; i < 9; i++) {
//            testSet[i-1]=imread("src\\TestingSet\\emo"+i+".jpg");
            
        }
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void testEmotions() {
         Face face=new Face(testSet[0]);
         face.detectFace();
     }
}
