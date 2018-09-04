package sample;

import javafx.scene.paint.Color;

class BlackImageState implements DisplayState{
    private static final BlackImageState BLACK_STATE = new BlackImageState(GrpcClient.getInstance());
    private GrpcClient grpcClient;


    private BlackImageState(GrpcClient grpcClient){
        this.grpcClient = grpcClient;
    }


    public static BlackImageState getBlackState(){
        return BLACK_STATE;
    }


    @Override
    public void switchImage(DisplayImagePoll imagePollApp) {
        imagePollApp.getJavaFxScene().setFill(Color.BLACK);
        grpcClient.sendImageDataViaProtobuffers(DisplayConfiguration.BLACK_SCREEN_NAME);
        imagePollApp.setImageState(RandomImageDisplayState.getImageState());
    }

    @Override
    public String getState() {
        return "Black";
    }
}
