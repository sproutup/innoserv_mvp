package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
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

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result logEvent(Long activity_id) {
        User user = Application.getLocalUser(ctx().session());

        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        }

        long product_id = json.findPath("product_id").asLong();

        RewardActivity activity = RewardActivity.find.byId(activity_id);
        Product prod = Product.find.byId(product_id);

        if(activity==null || prod == null){
            notFound("need both activity and product");
        }

        RewardEvent event = new RewardEvent();
        event.product = prod;
        event.user = user;
        event.activity = activity;
        event.points = activity.points;
        event.save();

        return ok();
    }
}
