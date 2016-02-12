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

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by peter on 2/12/15.
 */
public class LikesController extends Controller {
    //
    // web service for posts
    //
    private static String url = Play.application().configuration().getString("main.api.url");

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getLikes(Long id, String type) {

        // get all likes for object
        //List<Likes> likes = Likes.getAllLikes(id, type);
        return ok(Likes.range(id.toString(), type));

        // convert to json and return
        //return ok(Likes.toJson(likes));
    }


    public static F.Promise<Result> addLikes(String id, String type) {
        WSRequestHolder holder = WS.url(url + "/likes");

        User user = Application.getLocalUser(ctx().session());

        JsonNode body = request().body().asJson();
        if (body == null) body = Json.newObject();
        ((ObjectNode)body).put("userId", user.id.toString());
        ((ObjectNode)body).put("refId", id.toString());
        ((ObjectNode)body).put("refType", type);

        final F.Promise<Result> resultPromise = holder.post(body).map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addLikesOld(Long id, String type, Long userId) {
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

    public static F.Promise<Result> deleteLike(String id) {
      WSRequestHolder holder = WS.url(url + "/likes/" + id);

      User user = Application.getLocalUser(ctx().session());

      final F.Promise<Result> resultPromise = holder.delete().map(
        new F.Function<WSResponse, Result>() {
          public Result apply(WSResponse response) {
            return ok();
          }
        }
      );
      return resultPromise;
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result deleteLikesOld(Long id, String type, Long userId) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            //long user_id = json.findPath("user_id").longValue();
            User user = User.find.byId(userId);
            if (user == null) {
                return badRequest("User not found [id]:" + userId);
            } else {
                Likes.removeLike(userId, id, type);
                return ok();
            }
        }
    }
}
