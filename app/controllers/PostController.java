package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.AppConstants;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.persistence.PersistenceException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostController extends Controller {

    //
    // web service for posts
    //

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getPosts(Long product_id, Long category_id) {
        Logger.debug("prdo/cat " + product_id +" "+category_id);

        List<Post> posts = new Post()
                .find
                .where()
                .eq("product_id", product_id)
                .eq("category", category_id)
                .isNull("parent_id")
                .orderBy("id desc")
                .findList();

        return ok(Post.toJson(posts));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getPost(Long id)
    {
        return ok(Post.hmget(id.toString()));
//        Post item = new Post().findById(id);
//        return item == null ? notFound("Post not found [" + id + "]") : ok(item.toJson());
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addPost() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            String body = json.findPath("body").textValue();
            long product_id = json.findPath("product_id").asLong();

            if (body == null) {
                return badRequest("Missing parameter [body]");
            } else {
                Post post = new Post();
                post.body = body;

                Product prod = Product.findbyID(product_id);
                if (prod != null) {
                    post.product = prod;
                }
                User user = Application.getLocalUser(ctx().session());
                if (user != null) {
                    post.user = user;
                }

                // look for the first url and save it as content if found
                Pattern urlpattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");
                Matcher match = urlpattern.matcher(post.body);

                if(match.find()){
                    Content cnt = new Content();
                    cnt.url = match.group();
                    cnt.product = prod;
                    cnt.user = user;
                    cnt.save();
                    post.content = cnt;
                }

                post.save();

                // Add tags if they are there
                Iterator<JsonNode> myIterator = json.findPath("tags").elements();
                while (myIterator.hasNext()) {
                    JsonNode node = myIterator.next();
                    post.addTag(node.findPath("text").textValue());
                }

                return created(Post.hmget(post.id.toString()));
            }
        }
    }
}
