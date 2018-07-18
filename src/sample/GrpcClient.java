package sample;

import io.grpc.examples.imageviewer.ImageDataSample;
import io.grpc.examples.imageviewer.ImagePollClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
            Set<String> positive_category = DisplayConfiguration.getPositiveImagesCategory().keySet();
            Set<String>  neutral_category= DisplayConfiguration.getNeutralImagesCategory().keySet();
            Set<String>  negative_category = DisplayConfiguration.getNegativeImagesCategory().keySet();
            for (String s: neutral_category )
                if (img_short_name.contains(s))
                    neutral = true;
            for (String s: positive_category )
                if (img_short_name.contains(s))
                    positive = true;
            for (String s: negative_category )
                if (img_short_name.contains(s))
                    negative = true;
        }
        System.out.println(")))");
        System.out.println(negative);
        System.out.println(neutral);
        System.out.println(positive);
        //        getDate()
        GRPC_CLIENT.packImageData(new ImageDataSample(img_name, negative, neutral, positive, black));
    }
}
