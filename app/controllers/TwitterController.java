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

//    public static Promise<Result> index() {
//        Promise<Integer> promiseOfInt = Promise.promise(
//                new Function0<Integer>() {
//                    public Integer apply() {
//                        return intensiveComputation();
//                    }
//                }
//        );
//        return promiseOfInt.map(
//                new Function<Integer, Result>() {
//                    public Result apply(Integer i) {
//                        return ok("Got result: " + i);
//                    }
//                }
//        );
//    }


//    OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(grailsApplication.config.oauth.providers.twitter.key).apiSecret(grailsApplication.config.oauth.providers.twitter.secret).build()
//    OAuthRequest request = new OAuthRequest(Verb.GET, 'https://api.twitter.com/1.1/search/tweets.json?q=%23' + URLEncoder.encode(tag) + '&count=100&result_type=mixed&lang=en');
//    Token accessToken = new Token(grailsApplication.config.oauth.providers.twitter.accessToken, grailsApplication.config.oauth.providers.twitter.accessSecret)
//    service.signRequest(accessToken, request);
//    Response response = request.send();
//    JSONElement jsonMap = grails.converters.JSON.parse(response.getBody());
    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";


    public static Result getTimeline(String username) {
        String access_token = Play.application().configuration().getString(TWITTER_ACCESS_TOKEN);
        String access_secret = Play.application().configuration().getString(TWITTER_ACCESS_SECRET);

        Logger.debug("twitter api > get timeline");

        // If you choose to use a callback, "oauth_verifier" will be the return value by Twitter (request param)
        OAuthService service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey("62ibZBjJp4ETSwT3T5B7abFVR")
                .apiSecret("YyM3hcsssmwErxovtB52l4LXxFs1uKThbjM2RwIy3jlMLgBEyp")
                .build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== Twitter's OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        Token requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println("Now go and authorize Scribe here:");
        System.out.println(service.getAuthorizationUrl(requestToken));
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier("test");
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
//        Token accessToken = service.getAccessToken(requestToken, verifier);
        Token accessToken = new Token(access_token, access_secret);
        System.out.println("Got the Access Token!");
        System.out.println("(if you're curious, it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("That's it man! Go and build something awesome with Scribe! :)");
        return ok();
    }

//    public static Promise<Result> getAccessToken() {
//        String app_id = Play.application().configuration().getString(FACEBOOK_APP_ID);
//        String app_secret = Play.application().configuration().getString(FACEBOOK_APP_SECRET);
//
//        final Promise<Result> resultPromise = WS.url("https://graph.facebook.com/oauth/access_token")
//                .setQueryParameter("client_id", app_id)
//                .setQueryParameter("client_secret", app_secret)
//                .setQueryParameter("grant_type", "client_credentials")
//                .get()
//                .map(
//                    new Function<WSResponse, Result>() {
//                        public Result apply(WSResponse response) {
//                            return ok(response.asJson());
//                        }
//                    }
//                );
//        return resultPromise;
//    }
}
