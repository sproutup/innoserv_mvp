package controllers;

import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.AnalyticsAccount;
import models.AnalyticsAccountSummary;
import models.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by peter on 3/27/15.
 */
public class GoogleController extends Controller {
    public static final String GOOGLE_API_CLIENT_ID = "google.api.client.id";
    public static final String GOOGLE_API_CLIENT_SECRET = "google.api.client.secret";
    public static final String GOOGLE_API_CLIENT_CALLBACK = "google.api.client.callback";
    private static final String SCOPE = "https://docs.google.com/feeds/";
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

//    Client ID
//    200067319298-cpblm10r8s9o29kjgtahjek2eib7eigk.apps.googleusercontent.com
//    Client secret
//    nQ4NK9cKoPl8fWXDF9V-PsTU

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result ExchangeAuthorizationCodeForToken(String code, String scope) throws IOException {
        AnalyticsAccount acc = null;
        User user=null;

        try {
            user = Application.getLocalUser(ctx().session());

            GoogleTokenResponse response =
            new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new JacksonFactory(),
                Play.application().configuration().getString(GOOGLE_API_CLIENT_ID),
                Play.application().configuration().getString(GOOGLE_API_CLIENT_SECRET),
                code,
                Play.application().configuration().getString(GOOGLE_API_CLIENT_CALLBACK)
            ).execute();

            System.out.println("Access token: " + response.getAccessToken());
            System.out.println("Refresh token: " + response.getRefreshToken());

    		if (user.analyticsAccounts!=null && user.analyticsAccounts.size()>0){
                //todo: make sure that provider = google
                acc = user.analyticsAccounts.get(0);
	    	}
            else{
                acc = new AnalyticsAccount();
            }
            acc.accessToken = response.getAccessToken();
            acc.refreshToken = response.getRefreshToken();
            acc.scope = scope;

            // calculate the expire date
            Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
            calendar.add(Calendar.SECOND, response.getExpiresInSeconds().intValue());
            acc.expiresAt = calendar.getTime();

            acc.provider = "google";
            acc.googleAnalyticsAPI = acc.scope.contains("auth/analytics.readonly");
            acc.youtubeAnalyticsAPI = acc.scope.contains("auth/yt-analytics.readonly") && acc.scope.contains("auth/youtube.readonly");
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
        return ok(AnalyticsAccount.toJson(user.analyticsAccounts));
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result AuthorizeParams(){
        // add params for google analytics and youtube analytics
        ObjectNode node = Json.newObject();
        node.put("ga", AuthorizeAnalyticsParams());
        node.put("yt", AuthorizeYoutubeParams());
        return ok(node);
    }

    public static ObjectNode AuthorizeAnalyticsParams(){

        ObjectNode node = Json.newObject();
        node.put("redirect_uri", Play.application().configuration().getString(GOOGLE_API_CLIENT_CALLBACK));
        node.put("response_type", "code");
        node.put("client_id", Play.application().configuration().getString(GOOGLE_API_CLIENT_ID));
        node.put("scope", "https://www.googleapis.com/auth/analytics.readonly");
        node.put("include_granted_scopes", "true");
        node.put("access_type", "offline");

        return node;
    }

    public static ObjectNode AuthorizeYoutubeParams(){

        ObjectNode node = Json.newObject();
        node.put("redirect_uri", Play.application().configuration().getString(GOOGLE_API_CLIENT_CALLBACK));
        node.put("response_type", "code");
        node.put("client_id", Play.application().configuration().getString(GOOGLE_API_CLIENT_ID));
        node.put("scope", "https://www.googleapis.com/auth/yt-analytics.readonly https://www.googleapis.com/auth/youtube.readonly");
        node.put("include_granted_scopes", "true");
        node.put("access_type", "offline");

        return node;
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static F.Promise<Result> oauth2revoke() {
        final User user;
        user = Application.getLocalUser(ctx().session());

        final F.Promise<Result> resultPromise = WS.url("https://accounts.google.com/o/oauth2/revoke")
            .setQueryParameter("token", user.analyticsAccounts.get(0).accessToken)
            .get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                    for(AnalyticsAccount analytics:  user.analyticsAccounts){
                        System.out.println("delete token: " + analytics.accessToken);

                        for(AnalyticsAccountSummary summary: analytics.summaries){
                            System.out.println("delete summary: " + summary.name);
                            summary.delete();
                        }

                        analytics.accessToken = "";
                        analytics.refreshToken = "";
                        analytics.isValid = -1;
                        analytics.errorMessage = "";
                        analytics.scope = "";
                        analytics.googleAnalyticsAPI = false;
                        analytics.youtubeAnalyticsAPI = false;
                        analytics.save();
                    }

                    return ok(AnalyticsAccount.toJson(user.analyticsAccounts));
                    }
                }
        );
        return resultPromise;
    }

//    protected JsonNode oauth2revoke(final String token) {
//        WSRequestHolder holder = WS.url("https://accounts.google.com/o/oauth2/revoke");
//
//        final F.Promise<WSResponse> promise = holder.setQueryParameter("token", token).get();
//        final WSResponse response = promise.get(getTimeout());
//        return response.asJson();
//    }


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
