package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

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
    
	@Restrict({@Group(AppConstants.CONSUMER),@Group(AppConstants.CREATOR)})
    @BodyParser.Of(BodyParser.Json.class)
    public static Result addFollow(Long id, String type, Long userId) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            //long user_id = json.findPath("user_id").longValue();
            User user = User.find.byId(userId);
            if (user == null) {
                return badRequest("User not found [id]:" + userId);
            } else {
                Follow rs = Follow.addFollow(userId, id, type);
                return created(rs.toJson());
            }
        }
    }
	
	@Restrict({@Group(AppConstants.CONSUMER),@Group(AppConstants.CREATOR)})
    @BodyParser.Of(BodyParser.Json.class)
    public static Result unFollow(Long id, String type, Long userId) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            //long user_id = json.findPath("user_id").longValue();
            User user = User.find.byId(userId);
            if (user == null) {
                return badRequest("User not found [id]:" + userId);
            } else {
                Follow.removeFollow(userId, id, type);
                return ok();
            }
        }
    }
}
