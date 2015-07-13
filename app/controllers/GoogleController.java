package controllers;

import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import models.AnalyticsAccount;
import models.Product;
import models.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.util.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by peter on 3/27/15.
 */
public class GoogleController extends Controller {
    public static final String GOOGLE_API_CLIENT_ID = "google.api.client.id";
    public static final String GOOGLE_API_CLIENT_SECRET = "google.api.client.secret";
    private static final String SCOPE = "https://docs.google.com/feeds/";
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

//    Client ID
//    200067319298-cpblm10r8s9o29kjgtahjek2eib7eigk.apps.googleusercontent.com
//    Client secret
//    nQ4NK9cKoPl8fWXDF9V-PsTU

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result ExchangeAuthorizationCodeForToken(String code) throws IOException {
        try {
            User user;
            user = Application.getLocalUser(ctx().session());

            GoogleTokenResponse response =
                    new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            new JacksonFactory(),
                            Play.application().configuration().getString(GOOGLE_API_CLIENT_ID),
                            Play.application().configuration().getString(GOOGLE_API_CLIENT_SECRET),
                            code,
                            "http://dev.sproutup.co:9000/settings/analytics")
                            .execute();
            System.out.println("Access token: " + response.getAccessToken());
            System.out.println("Refresh token: " + response.getRefreshToken());

            AnalyticsAccount acc = new AnalyticsAccount();
            acc.accessToken = response.getAccessToken();
            acc.refreshToken = response.getRefreshToken();
            acc.provider = "google";
            acc.googleAnalyticsAPI = true;
            acc.youtubeAnalyticsAPI = false;
            acc.user = user;
            acc.save();

        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    System.err.println(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    System.err.println(e.getDetails().getErrorUri());
                }
            } else {
                System.err.println(e.getMessage());
            }
        }
        return ok();
    }

    public static Result ExchangeAuthorizationCodeForTokenxx(String code) {
        OAuthService service = new ServiceBuilder()
                .provider(GoogleApi.class)
                .apiKey(Play.application().configuration().getString(GOOGLE_API_CLIENT_ID))
                .apiSecret(Play.application().configuration().getString(GOOGLE_API_CLIENT_SECRET))
                .scope(SCOPE)
                .build();


        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        //Token requestToken = service.getRequestToken();
        Token requestToken = new Token(code, Play.application().configuration().getString(GOOGLE_API_CLIENT_SECRET));
        System.out.println("Got the Request Token!");
        System.out.println("(if your curious it looks like this: " + requestToken + " )");
        System.out.println();

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        return ok();
    }



//    public static Result getApi(String endpoint) {
//        String access_token = Play.application().configuration().getString(TWITTER_ACCESS_TOKEN);
//        String access_secret = Play.application().configuration().getString(TWITTER_ACCESS_SECRET);
//
//        Logger.debug("twitter api > get > ", endpoint);
//
//        byte[] bytes = (byte[]) Cache.get("twitter:" + endpoint);
//        if (bytes != null) {
//            Logger.debug("twitter api: cache hit");
//            // Create an immutable reader
//            final ObjectReader reader = mapper.reader();
//            // Use the reader for thread safe access
//            final JsonNode newNode;
//            try {
//                newNode = reader.readTree(new ByteArrayInputStream(bytes));
//                return ok(newNode);
//            } catch (IOException e) {
//                return badRequest();
//            }
//        } else {
//            Logger.debug("twitter api: cache miss");
//            // If you choose to use a callback, "oauth_verifier" will be the return value by Twitter (request param)
//            Logger.debug("twitter key: " + Play.application().configuration().getString("twitter.access.token"));
//            OAuthService service = new ServiceBuilder()
//                    .provider(TwitterApi.class)
//                    .apiKey(Play.application().configuration().getString("twitter.consumer.key"))
//                    .apiSecret(Play.application().configuration().getString("twitter.consumer.secret"))
//                    .build();
//
//            System.out.println("=== Twitter's OAuth Workflow ===");
//
//            System.out.println("Create an Access Token...");
//            Token accessToken = new Token(access_token, access_secret);
//
//            // Now let's go and ask for a protected resource!
//            System.out.println("Now we're going to access a protected resource...");
//            OAuthRequest request = new OAuthRequest(Verb.GET, endpoint);
//            service.signRequest(accessToken, request);
//            Response response = request.send();
//            System.out.println("Got it!");
//            try {
//                JsonNode actualObj = mapper.readTree(response.getBody());
//                // Create an immutable writer (in this case using the default settings)
//                final ObjectWriter writer = mapper.writer();
//                // Use the writer for thread safe access.
//                bytes = writer.writeValueAsBytes(actualObj);
//                // Cache for 60 minutes
//                Cache.set("twitter:" + endpoint, bytes, 60 * 60);
//
//                return ok(actualObj);
//            } catch (IOException e) {
//                return badRequest();
//            }
//        }
//    }
}
