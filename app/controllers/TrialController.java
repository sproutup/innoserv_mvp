package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import models.Post;
import models.Product;
import models.ProductTrial;
import models.User;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Iterator;
import java.util.List;

public class TrialController extends Controller {

    //
    // web service for posts
    //

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getTrials() {
        User user = Application.getLocalUser(ctx().session());

        List<ProductTrial> trials = new ProductTrial()
                .find
                .where()
                .eq("user_id", user.id)
                .orderBy("id desc")
                .findList();
        return ok(ProductTrial.toJson(trials));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getTrial(Long id)
    {
        ProductTrial item = new ProductTrial().findById(id);
        return item == null ? notFound("Trial not found [" + id + "]") : ok(item.toJson());
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addPost() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            long parent_id = json.findPath("parent").longValue();
            String content = json.findPath("content").textValue();
            String title = json.findPath("title").textValue();
            int category = json.findPath("category").asInt();
            long product_id = json.findPath("product_id").asLong();
            if (content == null) {
                return badRequest("Missing parameter [content]");
            } else {
                Post post = new Post();
                post.title = title;
                post.content = content;
                post.category = category;
                Post parent = Post.findById(parent_id);
                if (parent != null) {
                    post.parent = parent;
                }
                Product prod = Product.findbyID(product_id);
                if (prod != null) {
                    post.product = prod;
                }
                User user = Application.getLocalUser(ctx().session());
                if (user != null) {
                    post.user = user;
                }
                post.save();

                // Add tags if they are there
                Iterator<JsonNode> myIterator = json.findPath("tags").elements();
                while (myIterator.hasNext()) {
                    JsonNode node = myIterator.next();
                    post.addTag(node.findPath("text").textValue());
                }

                return created(post.toJson());
            }
        }
    }

//        Post newPost = Json.fromJson(request().body().asJson(), Post.class);
//        try {
//            newPost.parent. = 1;
//            newPost.save();
//            return created(newPost.toJson());
//        }
//        catch(PersistenceException e){
//            return Results.badRequest(e.getMessage());
//        }
//    }

//    public static Result updateProduct(Long id)
//    {
//        Product existing = new Product().findbyID(id);
//        if(existing != null) {
//            Product updated = Json.fromJson(request().body().asJson(), Product.class);
//            if(updated.productName != null) { existing.productName = updated.productName; };
//            if(updated.productEAN != null) { existing.productEAN = updated.productEAN; };
//            if(updated.productDescription != null) { existing.productDescription = updated.productDescription; };
//            if(updated.productLongDescription != null) { existing.productLongDescription = updated.productLongDescription; };
//            if(updated.urlHome != null) { existing.urlHome = updated.urlHome; };
//            if(updated.urlFacebook != null) { existing.urlFacebook = updated.urlFacebook; };
//            if(updated.urlTwitter != null) { existing.urlTwitter = updated.urlTwitter; };
//            existing.save();
//            return ok(Json.toJson(existing));
//        }
//        else{
//            return Results.notFound("Product not found");
//        }
//    }

//    public static Result deleteProduct(Long id)
//    {
//        Product del = new Product().findbyID(id);
//        if(del != null) {
//            del.delete();
//            return ok();
//        }
//        else{
//            return Results.notFound("Product not found");
//        }
//    }

}
