package eventpojos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class PubSubMessage {
    // Cloud Functions uses GSON to populate this object.
    // Field types/names are specified by Cloud Functions
    // Changing them may break your code!
    private String data;
    private Map<String, String> attributes;
    private String messageId;
    private String publishTime;
}