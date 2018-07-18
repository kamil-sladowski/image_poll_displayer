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
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class DisplayImagePoll extends Application {
    private List<String> images;
    private ImageDisplayState imageState;
    private Scene scene;
    public Iterator<String> imageIterator;
    public AtomicReference<ImagePattern> img_pattern;


    public Scene getJavaFxScene() {
        return scene;
    }


    public void setImageState(ImageDisplayState nextState) {
        imageState = nextState;
    }


    private void runImagesAnimation(){
        Duration[] displayDur = DisplayConfiguration.getDisplayImagesDuration();
        Duration[] blackDur = DisplayConfiguration.getDisplayBlackImagesDuration();
        Duration initialDelay = DisplayConfiguration.getInitialDelay();

        Timeline t1 = new Timeline(
                new KeyFrame(initialDelay, e -> imageState.switchImage(this)),
                new KeyFrame(Duration.ZERO));
        t1.play();

        imageState.switchImage(this);
        Timeline imagesTimeline = new Timeline(
            new KeyFrame(displayDur[0], e -> imageState.switchImage(this)),
            new KeyFrame(blackDur[0], e -> imageState.switchImage(this)),
                new KeyFrame(displayDur[1], e -> imageState.switchImage(this)),
            new KeyFrame(blackDur[1], e -> imageState.switchImage(this)),
            new KeyFrame(displayDur[2], e -> imageState.switchImage(this)),
            new KeyFrame(blackDur[2], e -> imageState.switchImage(this))
        );

        imagesTimeline.setCycleCount(1);
        imagesTimeline.setOnFinished(event -> {
            Collections.shuffle(images);
            imageIterator= images.iterator();
            imagesTimeline.playFromStart();
        });
        imagesTimeline.play();
    }


    private void createFileIfNotExists(String logPath){
        File f = new File(logPath);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String[] getImagesPaths(){
        ArrayList<HashMap<String, Integer>> myList = new ArrayList<>();
        int i =0;
        myList.add(DisplayConfiguration.getPositiveImagesCategory());
        myList.add(DisplayConfiguration.getNeutralImagesCategory());
        myList.add(DisplayConfiguration.getNegativeImagesCategory());
        String[] images_locks = new String[DisplayConfiguration.getNumberOfAllImages()];
        System.out.println(DisplayConfiguration.getNumberOfAllImages());

        for (HashMap<String, Integer> category : myList) {
            for (Map.Entry<String, Integer> entry : category.entrySet()) {
                int max_nr = entry.getValue();
                String key = entry.getKey();
                for (int j = 1; j <= max_nr; j++) {
                    String randomNum = String.format("%03d", j);
                    images_locks[i] = DisplayConfiguration.IMAGES_PATH + key + "\\" + key + randomNum + ".bmp";
                    i++;
                }
            }
        }
        return images_locks;
    }


    public String getDate(){
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        return ft.format(new Date());
    }


    public void writeToFile(String img_name, String date){
        try {
            Path destFile = Paths.get(DisplayConfiguration.LOG_PATH);

            Files.write(destFile, Collections.singleton(img_name), Charset.forName("UTF-8"),
                    StandardOpenOption.APPEND);
            Files.write(destFile, Collections.singleton(date), Charset.forName("UTF-8"),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Can not find file to log");
        }
    }


    private void setFXScene(Stage stage){
        BorderPane pane = new BorderPane();
        scene = new Scene(pane);
        scene.setFill(Color.GREEN);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setScene(scene);
    }


    private void setImagesList(){
        String[] imagesPaths = getImagesPaths();
        images = new ArrayList<>(Arrays.asList(imagesPaths));
        img_pattern = new AtomicReference<>(new ImagePattern(
                new Image(new File(images.get(0)).toURI().toString())));
        imageIterator = images.iterator();
        Collections.shuffle(images);
    }


    @Override
    public void start(Stage stage) {
        DisplayConfiguration.loadDisplayConfiguration();
        createFileIfNotExists(DisplayConfiguration.LOG_PATH);
        imageState = BlackImageState.getBlackState();
        setImagesList();
        setFXScene(stage);
        runImagesAnimation();
        stage.show();
    }


    public static void main(String[] args) {
        String cfgPath = null;
        if(args.length>0)
            cfgPath = args[0]; //Pass path to images as args
        DisplayConfiguration.setResourcesPaths(cfgPath);

        launch(args);
    }
}

