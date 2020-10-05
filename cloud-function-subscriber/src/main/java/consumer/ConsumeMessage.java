package consumer;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import dto.DistanceRequest;
import dto.DistanceResult;
import eventpojos.PubSubMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumeMessage implements BackgroundFunction<PubSubMessage> {
    private static final String PROJECT_ID = System.getenv("GCP_PROJECT");

    private static final Logger logger = Logger.getLogger(ConsumeMessage.class.getName());

    @Override
    public void accept(PubSubMessage message, Context context) {
        if (message.getData() == null) {
            logger.info("No message provided");
            return;
        }

        String messageString = new String(
                Base64.getDecoder().decode(message.getData().getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        logger.info(messageString);

        try {
            Gson gson = new GsonBuilder().create();
            DistanceRequest request = gson.fromJson(messageString, DistanceRequest.class);

            DistanceResult result = new DistanceResult();
            result.setDestination(request.getDestination());
            result.setSource(request.getSource());
            result.setDistance(123456.23);
            publishAck(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error publishing Pub/Sub message: " + e.getMessage(), e);
        }
    }

    private void publishAck(DistanceResult result) throws IOException {

        Gson gson = new GsonBuilder().create();
        String response = gson.toJson(result);
        ByteString byteStr = ByteString.copyFrom(response, StandardCharsets.UTF_8);
        PubsubMessage pubsubApiMessage = PubsubMessage.newBuilder().setData(byteStr).build();

        logger.log(Level.INFO, "The project ID is " + PROJECT_ID);
        Publisher publisher = Publisher.newBuilder(
                ProjectTopicName .of(PROJECT_ID, "distance-responses")).build();

        // Attempt to publish the message
        try {
            publisher.publish(pubsubApiMessage).get();
            publisher.shutdown();
            publisher.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Error publishing Pub/Sub message: " + e.getMessage(), e);
        }
    }
}
