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
    private final Map<String, Integer> IMAGE_CATEGORY = Map.of(
            "A",100,"H",100,"N",100,"P",
            100,"Sn",100, "Sp",100);
    private final Integer img_num = 600;
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

        imageState.switchImage(this);
        Timeline timelineShow = new Timeline(
            new KeyFrame(displayDur[0], e -> imageState.switchImage(this)),
            new KeyFrame(blackDur[0], e -> imageState.switchImage(this)),
            new KeyFrame(displayDur[1], e -> imageState.switchImage(this)),
            new KeyFrame(blackDur[1], e -> imageState.switchImage(this)),
            new KeyFrame(displayDur[2], e -> imageState.switchImage(this)),
            new KeyFrame(blackDur[2], e -> imageState.switchImage(this))
        );

        timelineShow.setCycleCount(1);
        timelineShow.setOnFinished(event -> {
            Collections.shuffle(images);
            imageIterator= images.iterator();
            timelineShow.playFromStart();
        });
        timelineShow.play();
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
        String[] images_locks = new String[img_num];
        int i =0;
        for(Map.Entry<String, Integer> entry : IMAGE_CATEGORY.entrySet()){
            int max_nr = entry.getValue();
            String key = entry.getKey();
            for(int j =1; j<= max_nr; j++) {
                String randomNum = String.format("%03d", j);
                images_locks[i] = DisplayConfiguration.IMAGES_PATH + key + "\\" + key + randomNum + ".bmp";
                i++;
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
        imageState = RandomImageDisplayState.getImageState();
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

