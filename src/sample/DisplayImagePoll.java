package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class DisplayImagePoll extends Application {
    private final String IMAGES_PATH = "D:\\Studia\\GAPED_2\\GAPED\\GAPED\\";

    private final Map<String, Integer> IMAGE_CATEGORY = Map.of(
            "A",100,"H",100,"N",100,"P",
            100,"Sn",100, "Sp",100);
    private final Integer img_num = 600;

    private Duration displayDur1;
    private Duration displayDur2;
    private Duration displayDur3;
    private Duration blackDur1;
    private Duration blackDur2;
    private Duration blackDur3;
    private List<String> images;
    private Iterator<String> imageIterator;
    private AtomicReference<ImagePattern> img_pattern;
    private Scene sc;

    public void loadDisplayDurations(){
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File("resources\\input.cfg")));
            int first_img_display_dur = Integer.valueOf(props.getProperty("FIRST_IMG_DISPLAY_DUR", "1"));
            int first_black_background = first_img_display_dur + Integer.valueOf(props.getProperty("FIRST_BLACK_BACKGROUND", "1"));
            int second_img_display_dur = first_black_background + Integer.valueOf(props.getProperty("SECOND_IMG_DISPLAY_DUR", "1"));
            int second_black_background = second_img_display_dur + Integer.valueOf(props.getProperty("SECOND_BLACK_BACKGROUND", "1"));
            int third_img_display_dur  = second_black_background + Integer.valueOf(props.getProperty("THIRD_IMG_DISPLAY_DUR", "1"));
            int third_black_background = third_img_display_dur + Integer.valueOf(props.getProperty("THIRD_BLACK_BACKGROUND", "1"));
            displayDur1 = Duration.seconds(first_img_display_dur);
            displayDur2 = Duration.seconds(second_img_display_dur);
            displayDur3 = Duration.seconds(third_img_display_dur);
            blackDur1 = Duration.seconds(first_black_background);
            blackDur2 = Duration.seconds(second_black_background);
            blackDur3 = Duration.seconds(third_black_background);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String[] getImagesPaths(){
        String[] images_locks = new String[img_num];
        int i =0;
        for(Map.Entry<String, Integer> entry : IMAGE_CATEGORY.entrySet()){
            int max_nr = entry.getValue();
            String key = entry.getKey();
            for(int j =1; j<= max_nr; j++) {
                String randomNum = String.format("%03d", j);
                images_locks[i] = IMAGES_PATH + key + "\\" + key + randomNum + ".bmp";
                i++;
            }
        }
        return images_locks;
    }


    private String getDate(){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        return ft.format(dNow);
    }


    private void writeToFile(String img_name, String date){
        try {
            Path destFile = Paths.get("resources\\log.txt");

            Files.write(destFile, Collections.singleton(img_name), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            Files.write(destFile, Collections.singleton(date), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchImage(Iterator<String> imageIterator){
            try {
                String img_name = imageIterator.next();
                System.out.printf(img_name);
                //img_name = "D:\\Studia\\GAPED_2\\GAPED\\GAPED\\N\\N005.bmp";
                img_pattern.set(new ImagePattern(new Image(new File(img_name).toURI().toString())));
                sc.setFill(img_pattern.get());
                writeToFile(img_name, getDate());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void start(Stage stage) {
        String[] ff = getImagesPaths();
        images = new ArrayList<>(Arrays.asList(ff));
        img_pattern = new AtomicReference<>(new ImagePattern(new Image(new File(images.get(0)).toURI().toString())));
        BorderPane pane = new BorderPane();
        sc = new Scene(pane);

        loadDisplayDurations();
        sc.setFill(Color.BLACK);
        imageIterator = images.iterator();
        Collections.shuffle(images);

        Timeline timelineShow = new Timeline(
                new KeyFrame(displayDur1, e -> switchImage(imageIterator)),
                new KeyFrame(blackDur1, e -> sc.setFill(Color.BLACK)),
                new KeyFrame(displayDur2, e -> switchImage(imageIterator)),
                new KeyFrame(blackDur2, e -> sc.setFill(Color.BLACK)),
                new KeyFrame(displayDur3, e -> switchImage(imageIterator)),
                new KeyFrame(blackDur3, e -> sc.setFill(Color.BLACK))
        );

        timelineShow.setCycleCount(1);
        timelineShow.setOnFinished(event -> {
            Collections.shuffle(images);
            imageIterator= images.iterator();
            timelineShow.playFromStart();
        });

        timelineShow.play();
        stage.setFullScreen(true);
        stage.setScene(sc);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}


