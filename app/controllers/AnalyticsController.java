package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.AnalyticsAccount;
import models.User;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by peter on 3/27/15.
 */
public class AnalyticsController extends Controller {
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getAll() {
        User user = Application.getLocalUser(ctx().session());
        return ok(AnalyticsAccount.toJson(user.analyticsAccounts));
    }
}
