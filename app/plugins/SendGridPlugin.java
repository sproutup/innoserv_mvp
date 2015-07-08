package plugins;

import com.amazonaws.services.s3.AmazonS3;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import com.sendgrid.smtpapi.SMTPAPI;
import org.json.JSONException;
import play.Application;
import play.Logger;
import play.Plugin;

/**
 * Created by peter on 7/7/15.
 */
public class SendGridPlugin extends Plugin {

    public static final String SENDGRID_API_KEY = "sendgrid.api.key";
    public static final String SENDGRID_MOCK = "sendgrid.mock";
    private final Application application;

    private static SendGrid _sendgrid;
    private static String sendgrid_api_key;
    private static String sendgrid_mock;

    public SendGridPlugin(Application application) {
        this.application = application;
    }

    public static SendGrid.Response send(SendGrid.Email email) throws SendGridException {
        if(sendgrid_mock == "true"){
            return new SendGrid.Response(200, "mock message");
        }
        else {
            return _sendgrid.send(email);
        }
    }

    @Override
    public void onStart() {
        // read config settings
        sendgrid_api_key = application.configuration().getString(SENDGRID_API_KEY);
        sendgrid_mock = application.configuration().getString(SENDGRID_MOCK);
        // create sendgrid object
        _sendgrid = new SendGrid(sendgrid_api_key);
        Logger.info("Using sendgrid: mock=" + sendgrid_mock);
    }

    @Override
    public boolean enabled() {
        return (application.configuration().keys().contains(SENDGRID_API_KEY));
    }

}
