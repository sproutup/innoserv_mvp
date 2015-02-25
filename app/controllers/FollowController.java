package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;

import constants.AppConstants;
import models.Follow;
import models.User;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


/**
 * Manage Follow actions
 */
public class FollowController extends Controller {
    //
    // web service for follow
    //

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAllFollowers(Long id, String type) {

        // get all followers for object
        List<Follow> followers = Follow.getAllFollowers(id, type);

        // convert to json and return
        return ok(Follow.toJson(followers));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result isFollowing(Long refId, String refType) {

        User user = Application.getLocalUser(ctx().session());
        if (user != null) {
            if(Follow.isFollowing(user.id, refId, refType)) {
                // convert to json and return
                return ok("true");
            }
        }
        return notFound("false");
    }

    @SubjectPresent
    @BodyParser.Of(BodyParser.Json.class)
    public static Result follow(Long id, String type) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            User user = Application.getLocalUser(ctx().session());
            if (user == null) {
                return badRequest("User not found");
            } else {
                Follow rs = Follow.addFollow(user.id, id, type);
                return created(rs.toJson());
            }
        }
    }

    @SubjectPresent
    @BodyParser.Of(BodyParser.Json.class)
    public static Result unfollow(Long id, String type) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            User user = Application.getLocalUser(ctx().session());
            if (user == null) {
                return badRequest("User not found");
            } else {
                Follow.removeFollow(user.id, id, type);
                return ok();
            }
        }
    }
}
