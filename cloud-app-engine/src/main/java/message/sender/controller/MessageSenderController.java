package message.sender.controller;

import message.sender.dto.DistanceRequest;
import message.sender.publish.PublishRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageSenderController {

    @Autowired
    private PublishRequest publishRequest;

    @GetMapping("/")
    public String hello() {
        return "Good morning, eager young minds.";
    }

    @GetMapping("/calculate")
    public String greeting(@RequestParam(value = "source", defaultValue = "London") String source,
                           @RequestParam(value = "destination", defaultValue = "Edinburgh") String destination) {
        DistanceRequest request = new DistanceRequest(source, destination);
        try {
            return publishRequest.send(request);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
