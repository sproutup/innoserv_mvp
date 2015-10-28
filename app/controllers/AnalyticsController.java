package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.AnalyticsAccount;
import models.User;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by peter on 3/27/15.
 */
public class AnalyticsController extends Controller {
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    private static String url = Play.application().configuration().getString("analytics.api.url");

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getAll() {
        User user = Application.getLocalUser(ctx().session());
        return ok(AnalyticsAccount.toJson(user.analyticsAccounts));
    }

    public static F.Promise<Result> createNetwork(String provider, String userId) {
        WSRequestHolder holder = WS.url(url + "/user/" + userId + "/network/" + provider);

        JsonNode body = request().body().asJson();
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

    public static F.Promise<Result> saveCallback(String token) {
        WSRequestHolder holder = WS.url(url + "/network/callback/" + token);

        JsonNode body = request().body().asJson();
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

    public static F.Promise<Result> listNetwork(String userId) {
        WSRequestHolder holder = WS.url(url + "/user/" + userId + "/network");

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> readNetwork(String provider, String userId) {
        WSRequestHolder holder = WS.url(url + "/user/" + userId + "/network/" + provider);

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> deleteNetwork(String provider, String userId) {
        WSRequestHolder holder = WS.url(url + "/user/" + userId + "/network/" + provider);

        final F.Promise<Result> resultPromise = holder.delete().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }
}
