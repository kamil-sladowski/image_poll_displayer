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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class DisplayImagePoll extends Application {
    private final String IMAGES_PATH = "D:\\Studia\\GAPED_2\\GAPED\\GAPED\\";

    private final Map<String, Integer> IMAGE_CATEGORY = Map.of(
            "A",100,"H",100,"N",100,"P",
            100,"Sn",100, "Sp",100);
    private final Integer img_num = 600;
    private int FIRST_IMG_DISPLAY_DUR = 3;
    private int FIRST_BLACK_BACKGROUN= FIRST_IMG_DISPLAY_DUR + 4;
    private int SECOND_IMG_DISPLAY_DUR = FIRST_BLACK_BACKGROUN+ 5;
    private int SECOND_BLACK_BACKGROUN = SECOND_IMG_DISPLAY_DUR + 6;
    private int THIRD_IMG_DISPLAY_DUR  = SECOND_BLACK_BACKGROUN+ 7;
    private int THIRD_BLACK_BACKGROUN  = THIRD_IMG_DISPLAY_DUR + 10;

    private Duration DISPLAY_DUR_1 = Duration.seconds(FIRST_IMG_DISPLAY_DUR);
    private Duration DISPLAY_DUR_2 = Duration.seconds(SECOND_IMG_DISPLAY_DUR);
    private Duration DISPLAY_DUR_3 = Duration.seconds(THIRD_IMG_DISPLAY_DUR);
    private Duration BLACK_DUR_1 = Duration.seconds(FIRST_BLACK_BACKGROUN);
    private Duration BLACK_DUR_2 = Duration.seconds(SECOND_BLACK_BACKGROUN);
    private Duration BLACK_DUR_3 = Duration.seconds(THIRD_BLACK_BACKGROUN);
    private int IMAGEE_WIDTH = 480;
    private int IMAGEE_HEIGHT = 360;
    private int IMAGE_MARGIN = 25;
    private List<String> images;
    private Iterator<String> imageIterator;
    private AtomicReference<ImagePattern> img_pattern;
    private Scene sc;

    private String[] getImagesPaths(){
        String[] images_locks = new String[img_num];
        int i =0;
        for(Map.Entry<String, Integer> entry : IMAGE_CATEGORY.entrySet()){
            int max_nr = entry.getValue();
            String key = entry.getKey();
            for(int j =1; j<= max_nr; j++) {
                String randomNum = String.format("%03d", j);
                images_locks[i] = IMAGES_PATH + key + "\\" + key + randomNum + ".bmp";
//                System.out.println(images_locks[i]);
                i++;
            }
        }
        return images_locks;
    }

//    private void loadImages(String[] images_locks){
//        System.out.println(images_locks.length);
//        images = Arrays.stream(images_locks)
//                .map(File::new)
//                .map(e-> new Image(e.toURI().toString()))
//                .collect(Collectors.toList());
//
//        Collections.shuffle(images);
//    }

    private String getDate(){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        return ft.format(dNow);
    }


    private void writeToFile(List<String> lines, Path destFile){
        try {
            Files.write(destFile, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchImage(Iterator<String> imageIterator){
//        while(true) {
            try {
                String img_name = imageIterator.next();
                System.out.printf(img_name);
                //img_name = "D:\\Studia\\GAPED_2\\GAPED\\GAPED\\N\\N005.bmp";
                img_pattern.set(new ImagePattern(new Image(new File(img_name).toURI().toString())));
                sc.setFill(img_pattern.get());
                System.out.printf("1");
                return;
            } catch (IllegalArgumentException x) {
                System.out.printf("4");
            } catch (java.lang.NullPointerException e) {
                System.out.printf("5");
            }
//        }
    }

    @Override
    public void start(Stage stage) {
        ImageView imageView = new ImageView();
        Path destFile = Paths.get("log.txt");
        String[] ff = getImagesPaths();
        images = new ArrayList<>(Arrays.asList(ff));
        img_pattern = new AtomicReference<>(new ImagePattern(new Image(new File(images.get(0)).toURI().toString())));
        BorderPane pane = new BorderPane();
        sc = new Scene(pane);

        sc.setFill(Color.BLACK);
        imageIterator = images.iterator();
        Collections.shuffle(images);

        Timeline timelineShow = new Timeline(
                new KeyFrame(DISPLAY_DUR_1, e -> switchImage(imageIterator)),
                new KeyFrame(BLACK_DUR_1, e -> sc.setFill(Color.BLACK)),
//                new KeyFrame(DISPLAY_DUR_2, e -> switchImage(imageIterator)),
//                new KeyFrame(BLACK_DUR_2, e -> sc.setFill(Color.BLACK)),
//                new KeyFrame(DISPLAY_DUR_3, e -> switchImage(imageIterator)),
                new KeyFrame(BLACK_DUR_3, e -> sc.setFill(Color.BLACK))
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


