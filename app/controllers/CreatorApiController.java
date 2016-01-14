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
}
