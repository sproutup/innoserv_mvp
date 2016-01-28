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
import play.Logger;


/**
 * Created by peter on 3/27/15.
 */
public class CreatorApiController extends Controller {
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    private static String url = Play.application().configuration().getString("creator.api.url");

    public static F.Promise<Result> campaign_list() {
        WSRequestHolder holder = WS.url(url + "/campaign/");

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> campaign_view(String campaignId) {
        WSRequestHolder holder = WS.url(url + "/campaign/" + campaignId);

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> campaign_by_user(String userId) {
        WSRequestHolder holder = WS.url(url + "/user/" + userId + "/campaign");

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> contributor_create() {
        WSRequestHolder holder = WS.url(url + "/contributor");

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

    public static F.Promise<Result> contributor_view(String userId, String campaignId){
      WSRequestHolder holder = WS.url(url + "/user/" + userId + "/campaign/" + campaignId);

      final F.Promise<Result> resultPromise = holder.get().map(
        new F.Function<WSResponse, Result>() {
          public Result apply(WSResponse response) {
            return ok(response.asJson());
          }
        }
      );
      return resultPromise;
    }

/*
 * Hangout API
 */
    public static F.Promise<Result> hangout_list() {
        WSRequestHolder holder = WS.url(url + "/calendar/event");

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> hangout_view(String hangoutId) {
        WSRequestHolder holder = WS.url(url + "/calendar/event/" + hangoutId);

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }
}
