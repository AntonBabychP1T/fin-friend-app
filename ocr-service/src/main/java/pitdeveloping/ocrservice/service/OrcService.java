package pitdeveloping.ocrservice.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrcService {
    @KafkaListener(topics = "telegram-bot-topic", groupId = "ocr-service-group")
    public void listen(String filePath) {
        System.out.println("Received message: " + filePath);

        try {
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
            System.out.println("Image bytes read successfully");

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            System.out.println("Sending request to Google Cloud Vision API");
            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                AnnotateImageResponse response = client.batchAnnotateImages(requests).getResponsesList().get(0);
                if (response.hasError()) {
                    System.err.println("Error: " + response.getError().getMessage());
                    return;
                }

                for (EntityAnnotation annotation : response.getTextAnnotationsList()) {
                    System.out.println("OCR Result: " + annotation.getDescription());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
