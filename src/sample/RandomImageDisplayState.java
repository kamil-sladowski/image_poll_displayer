package sample;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import java.io.File;

class RandomImageDisplayState implements DisplayState{
    private static final RandomImageDisplayState IMAGE_STATE = new RandomImageDisplayState(GrpcClient.getInstance());
    private GrpcClient grpcClient;


    private RandomImageDisplayState(GrpcClient grpcClient){
        this.grpcClient = grpcClient;
    }

    public static RandomImageDisplayState getImageState(){
        return IMAGE_STATE;
    }


    @Override
    public void switchImage(DisplayImagePoll imagePollApp) {
        while (true) {
                String img_name = imagePollApp.imageIterator.next();
                File f = new File(img_name);
                if(f.exists()) {
                    imagePollApp.img_pattern.set(new ImagePattern(
                            new Image(f.toURI().toString())));
                    imagePollApp.getJavaFxScene().setFill(imagePollApp.img_pattern.get());
                    imagePollApp.writeToFile(img_name, imagePollApp.getDate());
                    grpcClient.sendImageDataViaProtobuffers(img_name);
                    imagePollApp.setImageState(BlackImageState.getBlackState());
                    break;
                }
        }
    }

    @Override
    public String getState() {
        return "Image";
    }
}
