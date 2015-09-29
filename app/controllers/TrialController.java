package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.avaje.ebean.FetchConfig;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import models.*;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

public class TrialController extends Controller {

    //
    // web service for trials
    //

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getall() {
        User user = Application.getLocalUser(ctx().session());

        List<Trial> trials = new Trial()
                .find
                .where()
                .eq("user_id", user.id)
                .orderBy("id asc")
                .findList();
        return ok(Trial.toJson(trials));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getByNickname(String nickname) {
        User user = new User()
                .find
                .where()
                .eq("nickname", nickname)
                .orderBy("id asc")
                .findUnique();

        if(user != null){
            return ok(Trial.toJsonShort(user.trials));
        }
        else {
            return notFound();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getTrialsOnProduct(String slug){
        Product prod = new Product()
                .findbySlug(slug);

        if(prod == null) return notFound();

        ArrayNode arrayNode = Trial.toJson(prod.trials);
        for (Trial item : prod.trials){
            arrayNode.add(item.product.toJsonShort());
        }

        return ok(arrayNode);
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getMyTriedProducts(){
        User user = Application.getLocalUser(ctx().session());

        List<Trial> trials = new Trial()
                .find
                .where()
                .eq("user_id", user.id)
                .ge("status", 1)
                .orderBy("updated_at asc")
                .findList();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Trial item : trials){
            arrayNode.add(item.product.toJsonShort());
        }

        return ok(arrayNode);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result get(Long id)
    {
        Trial item = Trial.find.byId(id);
        return item == null ? notFound("Trial not found [" + id + "]") : ok(item.toJson());
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

            Trial item = new Trial();
            item.email = user.email;
            item.name = user.name;
            item.status = 0; // requested

            if (check(root, "address")) item.address = root.path("address").asText();
            if (check(root, "phone")) item.phone = root.path("phone").asText();
            if (check(root, "reason")) item.reason = root.path("reason").asText();

            item.user = user;

            Product prod = null;
            if (check(root, "product_slug")) {
                String product_slug = root.path("product_slug").asText();
                prod = Product.findBySlug(product_slug);
                if (prod != null) {
                    item.product = prod;
                    prod.cacheRemove();
                }
                else{
                    return notFound("Product for trial not found");
                }
            }

            List<Integer> s1 = Arrays.asList(0, 1, 2, 3);//only active requests and not cancelled ones
            List<Integer> s2 = Arrays.asList(0, 1, 2, 3, 4);//only active requests and not cancelled ones

            /*
             * Place trial request only if product trial is not full house
             */
            if (item.product.trialFullHouseFlag){ return status(200, "Product Trial Full House; Not accepting more requests"); }

            /*
             * Place trial request only if user has 3 or less active ones
             */
            List<Trial> t = Trial.find.fetch("user", new FetchConfig().query())
                    .where()
                    .eq("user.id", user.id)
                    .in("status", s1).findList();

            if (t!=null && t.size()>=3) { return status(200, "User already has more than 3 active requests"); }

            /*
            * Place trial request only if it doesn't exist before
            */
             t = Trial.find.fetch("user", new FetchConfig().query()).fetch("product", new FetchConfig().query())
            .where()
            .eq("user.id", user.id)
            .eq("product.id", item.product.id)
            .in("status", s2).findList();


             if (t==null || t.size()==0) {//only create trial if it doesn't exist
                item.save();

	            /*
	             * need to save first to generate item.id before we want to generate RefURL
	             * so that each refferer id gets tied to a trial id
	             */
	            item.generateRefUrl();
	            item.update();


	            return created(item.toFullJson());

            } else {
            	return ok(item.toJson());
            }
        }
    }

    @SubjectPresent
    public static Result update(Long id)
    {
    	User user = Application.getLocalUser(ctx().session());
        Trial item = Trial.find.byId(id);
        // check that we found the trial and that user owns it
        if(item != null && item.user.id != null && item.user.id.longValue() == user.id.longValue()) {
            JsonNode root = request().body().asJson();
            if (check(root, "email")) item.email = root.path("email").asText();
            if (check(root, "name")) item.name = root.path("name").asText();
            if (check(root, "address")) item.address = root.path("address").asText();
            if (check(root, "phone")) item.phone = root.path("phone").asText();
            if (check(root, "reason")) item.reason = root.path("reason").asText();
            item.save();
            return ok(item.toJson());
        }
        else{
            return play.mvc.Results.notFound("Trial not found");
        }
    }

    @SubjectPresent
    public static Result delete(Long id)
    {
        User user = Application.getLocalUser(ctx().session());
        Trial item = Trial.find.byId(id);
        if(item != null && item.user.id != null && item.user.id.longValue() == user.id.longValue()) {
            item.delete();
            return ok();
        }
        else{
            return play.mvc.Results.notFound("Trial not found");
        }
    }

}
