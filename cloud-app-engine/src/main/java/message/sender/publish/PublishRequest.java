package message.sender.publish;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import message.sender.dto.DistanceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PublishRequest {

    @Value("${requests.topic}")
    private String topicId;

    @Value("${project.id}")
    private String projectId;

    public String send(DistanceRequest distanceRequest)
            throws IOException, InterruptedException {
        TopicName topicName = TopicName.of(projectId, topicId);

        Gson gson = new GsonBuilder().create();
        String message = gson.toJson(distanceRequest);
        ByteString byteStr = ByteString.copyFrom(message, StandardCharsets.UTF_8);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(byteStr).build();
        Publisher publisher = null;

        AtomicReference<String> responseMessage = new AtomicReference<>();
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> future = publisher.publish(pubsubMessage);

            // Add an asynchronous callback to handle success / failure
            ApiFutures.addCallback(
                future,
                    new ApiFutureCallback<>() {

                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof ApiException) {
                                ApiException apiException = ((ApiException) throwable);
                                // details on the API exception
                                System.out.println(apiException.getStatusCode().getCode());
                                System.out.println(apiException.isRetryable());
                                responseMessage.set("Error publishing message : " + message + " Reason:" + apiException.getMessage());
                            }
                        }

                        @Override
                        public void onSuccess(String messageId) {
                            // Once published, returns server-assigned message ids (unique within the topic)
                            responseMessage.set("Published message ID: " + messageId);
                        }
                    },
                MoreExecutors.directExecutor());
            }
            finally {
                if (publisher != null) {
                    // When finished with the publisher, shutdown to free up resources.
                    publisher.shutdown();
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                }
            }
        return responseMessage.get();
    }

}
