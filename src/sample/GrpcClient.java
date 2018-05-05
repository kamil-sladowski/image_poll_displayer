package sample;

import io.grpc.examples.imageviewer.ImageDataSample;
import io.grpc.examples.imageviewer.ImagePollClient;

class GrpcClient extends ImagePollClient{
    private static final GrpcClient GRPC_CLIENT = new GrpcClient("localhost", 50051);;


    private GrpcClient(String IPAddress, int portNr){
        super(IPAddress, portNr);
    }


    public static GrpcClient getInstance(){
        return GRPC_CLIENT;
    }


    public void sendImageDataViaProtobuffers(String img_name){
        boolean negative = false, neutral = false, positive = false, black = false;
        int prefixLen = DisplayConfiguration.IMAGES_PATH.length();

        if(DisplayConfiguration.BLACK_SCREEN_NAME.equals(img_name)) {
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
        //        getDate()
        GRPC_CLIENT.packImageData(new ImageDataSample(img_name, negative, neutral, positive, black));
    }
}
