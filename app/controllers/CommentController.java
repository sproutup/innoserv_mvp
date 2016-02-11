package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Play;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.Logger;

/**
 * Created by peter on 2/12/15.
 */
public class CommentController extends Controller {
    //
    // web service for posts
    //
    private static String url = Play.application().configuration().getString("main.api.url");

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getComments(Long id, String type) {

        // get all comments for object
        return ok(Comment.range(id.toString(), type));
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static F.Promise<Result> addComment(String id, String type) {
        WSRequestHolder holder = WS.url(url + "/comment/" + type + "/" + id);

        User user = Application.getLocalUser(ctx().session());

        JsonNode body = request().body().asJson();
        ((ObjectNode)body).put("userId", user.id.toString());
        if (body == null) body = Json.newObject();

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
    public static Result addCommentOld(Long id, String type) {
        Logger.debug("adding a comment on type: " + type);
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            User user = Application.getLocalUser(ctx().session());
            if (user == null) {
                return badRequest("User not found");
            } else {
                String body = json.findPath("body").textValue();
                Logger.debug(body);
                Comment rs = Comment.addComment(user.id, body, id, type);

                if((type.compareToIgnoreCase("models.post")==0) || (type.compareToIgnoreCase("models.productsuggestion")==0)) {
                    // Add reward points
                    RewardActivity publishContent = RewardActivity.find.byId(3002L);

                    RewardEvent event = new RewardEvent();
                    //event.product = Post.find.byId(id);
                    event.user = user;
                    //event.content = post.content;
                    event.activity = publishContent;
                    event.points = publishContent.points;
                    event.save();
                }

                return created(Comment.hmget(rs.id.toString()));
            }
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result deleteComment(Long id, String type, Long commentId) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            User user = Application.getLocalUser(ctx().session());
            if (user == null) {
                return badRequest("User not found");
            } else {
                if(Comment.deleteComment(commentId, id, type, user.id)){
                    return ok();
                }
                else{
                    return notFound();
                }
            }
        }
    }
}
