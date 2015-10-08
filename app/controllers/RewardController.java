package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import models.*;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by peter on 3/19/15.
 */
public class RewardController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getall() {
        User user = Application.getLocalUser(ctx().session());

        List<RewardEvent> events = RewardEvent.findByUser(user);

        return ok(RewardEvent.toJson(events));
    }
}
