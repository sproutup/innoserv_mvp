package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.fasterxml.jackson.databind.JsonNode;

import models.*;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * for product suggestion
 * @author nitin 12/12/15
 *
 */
public class SuggestController extends Controller {

    /*
     * Product suggested by a creator on the creator page
     * does not require login. simple suggestion - blackbox way
     */
	@BodyParser.Of(BodyParser.Json.class)
    public static Result addSuggestionByCreator() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            ProductSuggestion prds = new ProductSuggestion();
            prds.email = json.path("email").asText();
            prds.productName = json.path("productName").asText();
            prds.productUrl = json.path("productUrl").asText();
            prds.save();

            return created();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getSuggestion(Long id)
    {
    	return ok(ProductSuggestion.hmget(id.toString()));
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAll()
    {
        return getRange(0);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getRange(int start)
    {
        return ok(ProductSuggestion.range(start, start + 9));
    }

 
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getUserRange(String nickname, int start)
    {
        return ok(ProductSuggestion.userRange(nickname, start, start+9 ));
    }

    

    private static boolean check(JsonNode root, String path){
        JsonNode node = root.path(path);
        return !node.isMissingNode() && !node.isNull();
    }

    /*
     * add product suggested by community
     * @require login
     */
    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addSuggest() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            String body = json.findPath("body").textValue();
            String name = json.findPath("name").textValue();
            
            if (body == null || name==null) {
                return badRequest("Missing parameters [body or name]");
            } else {
                ProductSuggestion suggestion = new ProductSuggestion();
                suggestion.body = body;
                suggestion.productName = name;
                suggestion.activeFlag = true;
                User user = Application.getLocalUser(ctx().session());
                //User user = User.findByEmail("jack@you.com");
                if (user != null) {
                	suggestion.user = user;
                }

                // look for the first url and save it as suggestion if found
                Pattern urlpattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");
                Matcher match = urlpattern.matcher(suggestion.body);

                if(match.find()){
                	suggestion.productUrl = match.group();
                }
                suggestion.save();
                

                if(suggestion.productUrl != null && (suggestion.isWebPage() || suggestion.isYoutubeVideo()) ) {
                    // Add reward points for suggesting product
                    RewardActivity suggestProduct = RewardActivity.find.byId(6003L);

                    RewardEvent event = new RewardEvent();
                    event.user = user;
                    event.activity = suggestProduct;
                    event.points = suggestProduct.points;
                    event.save();
                }

                // Add tags if they are there
                Iterator<JsonNode> myIterator = json.findPath("tags").elements();
                while (myIterator.hasNext()) {
                    JsonNode node = myIterator.next();
                    suggestion.addTag(node.findPath("text").textValue());
                }

                return created(ProductSuggestion.hmget(suggestion.id.toString()));
            }
        }
    }

    @SubjectPresent
    public static Result update(Long id)
    {
        User user = Application.getLocalUser(ctx().session());
        ProductSuggestion item = ProductSuggestion.find.byId(id);
        // check that we found the ProductSuggestion and that user owns it
        if(item != null && item.user.id != null && item.user.id.longValue() == user.id.longValue()) {
            JsonNode root = request().body().asJson();
            if (check(root, "url")) item.productUrl = root.path("url").asText();
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
        ProductSuggestion item = ProductSuggestion.find.byId(id);
        if(item != null && item.user.id != null && item.user.id.longValue() == user.id.longValue()) {
            item.delete();
            return ok();
        }
        else{
            return play.mvc.Results.notFound("Not found");
        }
    }



}
