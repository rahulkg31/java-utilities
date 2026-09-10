package com.example.taskmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Hello, Log4j2!");
        
        String dummyLog = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam aliquam felis ligula, ac porttitor nunc sagittis sit amet. Ut vulputate tortor ac lectus laoreet, et pellentesque nunc lacinia. Proin lacinia pulvinar fringilla. Quisque et sapien nec enim lobortis consequat. Morbi ultrices odio eu lacus suscipit hendrerit. Curabitur non tellus sit amet lectus feugiat lobortis. Fusce non urna vitae ligula aliquet varius. Nunc in finibus nulla. Aliquam dapibus ex sed mauris pellentesque feugiat. Sed tincidunt ante vitae turpis tincidunt, eget bibendum dolor tincidunt. Pellentesque facilisis fringilla lectus, vel auctor est. Nullam euismod dapibus libero, vel efficitur mauris tincidunt vel.";

        while(true){	       
	        logger.warn("This is a warning message - " + dummyLog);
	        logger.info("This is a debug message - " + dummyLog);
	        logger.debug("This is a debug message - " + dummyLog);
	        try {
	            Thread.sleep(10000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
        }
    }
}
