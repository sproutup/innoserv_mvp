package utils;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.File;
import play.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by peter on 3/11/15.
 */
public class AWSSimpleQueueManager implements Runnable{
    private String queueUrl;

//    public static void main(String[] args){
//        AWSSimpleQueueService awssqsUtil =   AWSSimpleQueueService.getInstance();
//        String queueUrl  = awssqsUtil.getQueueUrl(awssqsUtil.getQueueName());
//        System.out.println("queueUrl : " + queueUrl);
//    }

    public AWSSimpleQueueManager(String queueUrl){
        this.queueUrl = queueUrl;
    }

    @Override
    public void run() {
        AWSSimpleQueueService awssqsUtil =   AWSSimpleQueueService.getInstance();

        String url  = awssqsUtil.getQueueUrl(awssqsUtil.getQueueName());
        Logger.debug("queue url: " + url);
        List<Message> sqsmessages =  awssqsUtil.getMessagesFromQueue(url);
        for (Message sqsmessage : sqsmessages) {
            String sqsmessageBody = sqsmessage.getBody();

            ObjectMapper mapper = new ObjectMapper();
            ObjectMapper messageMapper = new ObjectMapper();
            JsonNode root = null;
            JsonNode message = null;
            try {
                root = mapper.readTree(sqsmessageBody);
                message = mapper.readTree(root.path("Message").asText());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Logger.debug("##########################################################");
            Logger.debug("SQS - received message with type: " + root.path("Type").asText());

            if(root.path("Type").asText().equalsIgnoreCase("Notification")) {
                Logger.debug("SQS - state: " + message.path("state").asText());
                if(message.path("state").asText().equalsIgnoreCase("COMPLETED")) {
                    if(File.transcodeCompleted(message.path("input").path("key").asText().split("_")[0])) {
                        /**
                         * finally delete the message
                         */
                        Logger.debug("SQS - delete message from queue");
                        awssqsUtil.deleteMessageFromQueue(url, sqsmessage);
                    }
                }
                else{
                    /**
                     * we ignore this kind of message and delete it
                     */
                    Logger.debug("SQS - delete message from queue");
                    awssqsUtil.deleteMessageFromQueue(url, sqsmessage);
                }
            }
        }
    }
}
