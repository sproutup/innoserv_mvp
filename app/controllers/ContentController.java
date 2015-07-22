package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.GoogleURLShortener;

import java.util.List;

public class ContentController extends Controller {

    //
    // web service for trials
    //

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getall() {
        User user = Application.getLocalUser(ctx().session());

        List<Content> trials = new Content()
                .find
                .where()
                .eq("user_id", user.id)
                .orderBy("id asc")
                .findList();
        return ok(Content.toJson(trials));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result get(Long id)
    {
        Content item = Content.find.byId(id);
        return item == null ? notFound("Content not found [" + id + "]") : ok(item.toJson());
    }

    private static boolean check(JsonNode root, String path){
        JsonNode node = root.path(path);
        return !node.isMissingNode() && !node.isNull();
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result create() {
        JsonNode root = request().body().asJson();
        if (root == null) {
            return badRequest("Expecting Json data");
        } else {
            User user = Application.getLocalUser(ctx().session());

            Content item = new Content();

            if (check(root, "url")) item.url = root.path("url").asText();

            item.user = user;

            Trial trial = null;
            if (check(root, "product_trial_id")) {
                Long trial_id = root.path("product_trial_id").asLong();
                trial = Trial.find.byId(trial_id);
                if (trial != null) {
                    item.trial = trial;
                    item.product = trial.product;

                }
                else{
                    return notFound("ProductTrial for content not found");
                }
            }

            item.save();

            return created(item.toJson());
        }
    }

    @SubjectPresent
    public static Result update(Long id)
    {
        User user = Application.getLocalUser(ctx().session());
        Content item = Content.find.byId(id);
        // check that we found the trial and that user owns it
        if(item != null && item.user.id != null && item.user.id.longValue() == user.id.longValue()) {
            JsonNode root = request().body().asJson();
            if (check(root, "url")) item.url = root.path("url").asText();
            item.save();
            return ok(item.toJson());
        }
        else{
            return play.mvc.Results.notFound("Not found");
        }
    }

    @SubjectPresent
    public static Result delete(Long id)
    {
        User user = Application.getLocalUser(ctx().session());
        Content item = Content.find.byId(id);
        if(item != null && item.user.id != null && item.user.id.longValue() == user.id.longValue()) {
            item.delete();
            return ok();
        }
        else{
            return play.mvc.Results.notFound("Not found");
        }
    }

    /*
 * Gets called when user clicks on the generate referral URL
 */
    @SubjectPresent
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getReferralURL(Long trialId) {
        User user = Application.getLocalUser(ctx().session());
        if (user == null) {
            return badRequest("User not found");
        } else {
            Trial item = Trial.find.byId(trialId);
            String referralId = UserReferral.getReferralId(user.id, null, trialId);
            String genURL = "http://sproutup.co/product/" + item.product.slug + "?refId="+ referralId;
            String shortURL= GoogleURLShortener.shortenURL(genURL);

            ObjectNode rs = Json.newObject();
            rs.put("url", shortURL);
            rs.put("referralId", referralId);
            rs.put("userId", user.id);
            rs.put("trialId", trialId);

            return created(rs);
        }
    }


}
