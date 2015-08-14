package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import models.Likes;
import models.Post;
import models.Product;
import models.User;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by peter on 2/12/15.
 */
public class LikesController extends Controller {
    //
    // web service for posts
    //

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getLikes(Long id, String type) {

        // get all likes for object
        //List<Likes> likes = Likes.getAllLikes(id, type);
        return ok(Likes.range(id.toString(), type));

        // convert to json and return
        //return ok(Likes.toJson(likes));
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addLikes(Long id, String type, Long userId) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            //long user_id = json.findPath("user_id").longValue();
            User user = User.find.byId(userId);
            if (user == null) {
                return badRequest("User not found [id]:" + userId);
            } else {
                Likes rs = Likes.addLike(userId, id, type);
                return created(Likes.hmget(rs.id.toString()));
                //return created(rs.toJson());
            }
        }
    }
}
