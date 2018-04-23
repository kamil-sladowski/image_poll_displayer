package sample;

import io.grpc.examples.imageviewer.ImageDataSample;
import io.grpc.examples.imageviewer.ImagePollClient;
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
    private final Map<String, Integer> IMAGE_CATEGORY = Map.of(
            "A",100,"H",100,"N",100,"P",
            100,"Sn",100, "Sp",100);
    private final Integer img_num = 600;
    private String IMAGES_PATH;
    private Duration[] displayDur = new Duration[3];
    private Duration[] blackDur = new Duration[3];
    private List<String> images;
    private Iterator<String> imageIterator;
    private AtomicReference<ImagePattern> img_pattern;
    private String BLACK_SCREEN_NAME = "Black";

    public void loadConfigurationData(){
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File("resources\\input.cfg")));
            IMAGES_PATH = props.getProperty("IMAGES_PATH", "");
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

            Files.write(destFile, Collections.singleton(img_name), Charset.forName("UTF-8"),
                    StandardOpenOption.APPEND);
            Files.write(destFile, Collections.singleton(date), Charset.forName("UTF-8"),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void switchImage(Iterator<String> imageIterator, ImagePollClient client, Scene scene){
        try {
            String img_name = imageIterator.next();
            System.out.println(img_name);
            img_pattern.set(new ImagePattern(new Image(new File(img_name).toURI().toString())));
            scene.setFill(img_pattern.get());
            writeToFile(img_name, getDate());
            sendImageDataViaProtobuffers( client, img_name, getDate());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private ImagePollClient runGRPCClient(){
        return new ImagePollClient("localhost", 50051);
    }


    private void sendImageDataViaProtobuffers(ImagePollClient client, String img_name, String date){
        boolean negative = false, neutral = false, positive = false, black = false;
        int prefixLen = IMAGES_PATH.length();

        if(BLACK_SCREEN_NAME.equals(img_name)) {
            black = true;
        }
        else {
            String img_short_name = img_name.substring(prefixLen, img_name.length());
            if (img_short_name.contains("N"))
                neutral = true;
            else if (img_short_name.contains("P"))
                positive = true;
            else if (img_short_name.contains("Sn") ||
                    img_short_name.contains("Sp") ||
                    img_short_name.contains("H") ||
                    img_short_name.contains("A"))
                negative = true;
        }
        client.packImageData(new ImageDataSample(img_name, negative, neutral, positive, black));
    }


    private void runImageAnimation(ImagePollClient grpcClient, Scene scene){
        Timeline timelineShow = new Timeline(
                new KeyFrame(displayDur[0], e -> switchImage(imageIterator, grpcClient, scene)),
                new KeyFrame(blackDur[0], e -> {
                    scene.setFill(Color.BLACK);
                    sendImageDataViaProtobuffers(grpcClient, BLACK_SCREEN_NAME, getDate());
                }),
                new KeyFrame(displayDur[1], e -> switchImage(imageIterator, grpcClient, scene)),
                new KeyFrame(blackDur[1], e -> {
                    scene.setFill(Color.BLACK);
                    sendImageDataViaProtobuffers(grpcClient, BLACK_SCREEN_NAME, getDate());
                }),
                new KeyFrame(displayDur[2], e -> switchImage(imageIterator, grpcClient, scene)),
                new KeyFrame(blackDur[2], e -> {
                    scene.setFill(Color.BLACK);
                    sendImageDataViaProtobuffers(grpcClient, BLACK_SCREEN_NAME, getDate());
                })
        );
        timelineShow.setCycleCount(1);
        timelineShow.setOnFinished(event -> {
            Collections.shuffle(images);
            imageIterator= images.iterator();
            timelineShow.playFromStart();
        });
        timelineShow.play();
    }


    @Override
    public void start(Stage stage) {
        loadConfigurationData();
        String[] imagesPaths = getImagesPaths();
        images = new ArrayList<>(Arrays.asList(imagesPaths));
        img_pattern = new AtomicReference<>(new ImagePattern(new Image(new File(images.get(0)).toURI().toString())));

        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane);
        scene.setFill(Color.BLACK);
        imageIterator = images.iterator();
        Collections.shuffle(images);
        ImagePollClient grpcClient = runGRPCClient();

        runImageAnimation(grpcClient, scene);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}


