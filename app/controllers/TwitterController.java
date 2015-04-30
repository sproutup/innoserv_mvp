package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import models.Product;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Scanner;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by peter on 3/27/15.
 */
public class TwitterController extends Controller {
    public static final String TWITTER_ACCESS_TOKEN = "twitter.access.token";
    public static final String TWITTER_ACCESS_SECRET = "twitter.access.secret";
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private static final String USERS_SHOW_URL = "https://api.twitter.com/1.1/users/show.json";
    private static final String STATUSES_USER_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json";

    public static Result getUserShow(Long product_id) {
        Product prod = Product.findbyID(product_id);
        // if product is not found return
        Logger.debug("twitter api > get user/show");
        if(prod == null || prod.urlTwitter == null || prod.urlTwitter.length() < 1){
            Logger.debug("twitter api > get user/show > not found");
            return notFound();
        }
        Logger.debug("twitter api > get user/show > found > ", prod.urlTwitter);
        String endpoint = prod.urlTwitter.substring(prod.urlTwitter.lastIndexOf(".com/")+5);
        return getApi(USERS_SHOW_URL+"?screen_name="+endpoint);
    }

    public static Result getUserTimeline(Long productId) {
        Product prod = Product.findbyID(productId);
        // if product is not found return
        Logger.debug("twitter api > get statuses/timeline");
        if(prod == null || prod.urlTwitter == null || prod.urlTwitter.length() < 1){
            Logger.debug("twitter api > get statuses/timeline > not found");
            return notFound();
        }
        Logger.debug("twitter api > get statuses/timeline > found > ", prod.urlTwitter);
        String endpoint = prod.urlTwitter.substring(prod.urlTwitter.lastIndexOf(".com/")+5);
        return getApi(STATUSES_USER_TIMELINE_URL + "?count=3&screen_name=" + endpoint);
    }

    public static Result getSearch(Long product_id) {
        Product prod = Product.findbyID(product_id);
        // if product is not found return
        Logger.debug("twitter api > search");
        if(prod == null || prod.urlTwitter == null || prod.urlTwitter.length() < 1){
            Logger.debug("twitter api > not found");
            return notFound();
        }
        Logger.debug("twitter api > found > ", prod.urlTwitter);
        String endpoint = prod.urlTwitter.substring(prod.urlTwitter.lastIndexOf(".com/")+5);
        return getApi("https://api.twitter.com/1.1/search/tweets.json?count=5&q="+endpoint);
    }

    public static Result getApi(String endpoint) {
        String access_token = Play.application().configuration().getString(TWITTER_ACCESS_TOKEN);
        String access_secret = Play.application().configuration().getString(TWITTER_ACCESS_SECRET);

        Logger.debug("twitter api > get > ", endpoint);

        byte[] bytes = (byte[]) Cache.get("twitter:" + endpoint);
        if (bytes != null) {
            Logger.debug("twitter api: cache hit");
            // Create an immutable reader
            final ObjectReader reader = mapper.reader();
            // Use the reader for thread safe access
            final JsonNode newNode;
            try {
                newNode = reader.readTree(new ByteArrayInputStream(bytes));
                return ok(newNode);
            } catch (IOException e) {
                return badRequest();
            }
        } else {
            Logger.debug("twitter api: cache miss");
            // If you choose to use a callback, "oauth_verifier" will be the return value by Twitter (request param)
            OAuthService service = new ServiceBuilder()
                    .provider(TwitterApi.class)
                    .apiKey("62ibZBjJp4ETSwT3T5B7abFVR")
                    .apiSecret("YyM3hcsssmwErxovtB52l4LXxFs1uKThbjM2RwIy3jlMLgBEyp")
                    .build();

            System.out.println("=== Twitter's OAuth Workflow ===");

            System.out.println("Create an Access Token...");
            Token accessToken = new Token(access_token, access_secret);

            // Now let's go and ask for a protected resource!
            System.out.println("Now we're going to access a protected resource...");
            OAuthRequest request = new OAuthRequest(Verb.GET, endpoint);
            service.signRequest(accessToken, request);
            Response response = request.send();
            System.out.println("Got it!");
            try {
                JsonNode actualObj = mapper.readTree(response.getBody());
                // Create an immutable writer (in this case using the default settings)
                final ObjectWriter writer = mapper.writer();
                // Use the writer for thread safe access.
                bytes = writer.writeValueAsBytes(actualObj);
                // Cache for 60 minutes
                Cache.set("twitter:" + endpoint, bytes, 60 * 60);

                return ok(actualObj);
            } catch (IOException e) {
                return badRequest();
            }
        }
    }
}
