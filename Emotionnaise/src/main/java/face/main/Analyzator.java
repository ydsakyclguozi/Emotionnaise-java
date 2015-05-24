/**
 * 
 */
package face.main;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import face.decision.DecisionSystem;
import face.elements.Face;
import face.elements.FaceElement;
import face.elements.FeatureStore;

/**
 * @author James
 *
 */

public class Analyzator {
	private static final Logger Log = Logger.getLogger(Analyzator.class
			.getName());

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ConfigurableApplicationContext ctx = null;
		try {
			ctx = new ClassPathXmlApplicationContext(
					"classpath*:/META-INF/*context.xml");
		} catch (BeansException e) {
			Log.error("Error during loading application context", e);
		}
		if (ctx == null) {
			return;
		}
		//Operational block
			//Load beans
		FaceElement face =	(FaceElement) ctx.getBean("face");
		FaceElement eyes=(FaceElement) ctx.getBean("eyes");
		FaceElement eyebrows=(FaceElement) ctx.getBean("eyebrows");
		FaceElement mouth=(FaceElement) ctx.getBean("mouth");
			//Execute detection algorithms
		face.detectElement();
		eyes.detectElement();
		eyebrows.detectElement();
		mouth.detectElement();
			//Mark features
		FeatureStore.markFeatures((Face) face);
			//Decion making system
		DecisionSystem ds=new DecisionSystem();
		ds.makeDecision();
		//Operational block END
		
		if (ctx != null) {
			if(Log.isInfoEnabled()){
				Log.info("Closing application context");
			}
			ctx.close();
		}
	}

}
