package sample;

import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DisplayConfiguration {

    public static final String BLACK_SCREEN_NAME = "Black";
    public static String IMAGES_PATH;
    private static String cfgPath;
    public static String LOG_PATH;
    private static Duration[] displayDur = new Duration[3];
    private static Duration[] blackDur = new Duration[3];

    static void setResourcesPaths(String cfgPath) {
        DisplayConfiguration.cfgPath = cfgPath;
    }

    public static void loadDisplayConfiguration(){
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(cfgPath)));

            IMAGES_PATH = props.getProperty("IMAGES_PATH", "") + "\\";
            LOG_PATH = props.getProperty("LOG_PATH", "") + "\\log.txt";

            int first_img_display_dur = Integer.valueOf(props.getProperty("FIRST_IMG_DISPLAY_DUR", "1"));
            int first_black_background = first_img_display_dur
                    + Integer.valueOf(props.getProperty("FIRST_BLACK_BACKGROUND", "1"));
            int second_img_display_dur = first_black_background
                    + Integer.valueOf(props.getProperty("SECOND_IMG_DISPLAY_DUR", "1"));
            int second_black_background = second_img_display_dur
                    + Integer.valueOf(props.getProperty("SECOND_BLACK_BACKGROUND", "1"));
            int third_img_display_dur  = second_black_background
                    + Integer.valueOf(props.getProperty("THIRD_IMG_DISPLAY_DUR", "1"));
            int third_black_background = third_img_display_dur
                    + Integer.valueOf(props.getProperty("THIRD_BLACK_BACKGROUND", "1"));
            displayDur[0] = Duration.seconds(first_img_display_dur);
            displayDur[1] = Duration.seconds(second_img_display_dur);
            displayDur[2] = Duration.seconds(third_img_display_dur);
            blackDur[0] = Duration.seconds(first_black_background);
            blackDur[1] = Duration.seconds(second_black_background);
            blackDur[2] = Duration.seconds(third_black_background);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Duration[] getDisplayImagesDuration(){
        return displayDur;
    }
    public static Duration[] getDisplayBlackImagesDuration(){
        return blackDur;
    }
}
