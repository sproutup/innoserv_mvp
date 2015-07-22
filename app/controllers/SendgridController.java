package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Play;
import play.libs.Json;
import utils.AWSSimpleQueueService;

/**
 * Created by Apurv on 7/20/15.
 */
public class SendgridController {

    private static AWSSimpleQueueService awsQueue = AWSSimpleQueueService.getInstance();
    private static String queueUrl = awsQueue.getQueueUrl(Play.application().configuration().getString("aws.sqs.sendgridQueue"));

    public static void pushToQueue(String methodName, String param1) {
        pushToQueue(methodName, param1, null, null);
    }

    public static void pushToQueue(String methodName, String param1, String param2) {
        pushToQueue(methodName, param1, param2, null);
    }

    public static void pushToQueue(String methodName, String param1, String param2, String param3) {
        if (param1.equals("defaultList")) {
            param1 = Play.application().configuration().getString("sendgrid.list");
        }
        ObjectNode message = null;
        if (methodName != null) {
            message = Json.newObject();
            message.put("methodName", methodName);
            if (param1 != null) {
                message.put("param1", param1);
                if (param2 != null) {
                    message.put("param2", param2);
                    if (param3 != null) {
                        message.put("param3", param3);
                    }
                }
            }
        }
        if (message != null) {
            awsQueue.sendMessageToQueue(queueUrl, message.toString());
            System.out.println(queueUrl);
            System.out.println(param1);
        }
    }
}
