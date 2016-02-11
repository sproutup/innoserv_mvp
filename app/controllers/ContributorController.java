package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.AppConstants;
import models.*;
import play.Play;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.persistence.PersistenceException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContributorController extends Controller {
    private static String url = Play.application().configuration().getString("main.api.url");

    public static F.Promise<Result> listMyContributions(String userId) {
        WSRequestHolder holder = WS.url(url + "/user/" + userId + "/campaign" );

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> updateContribution(String userId, String campaignId) {
        WSRequestHolder holder = WS.url(url + "/campaign/" + campaignId + "/user/" + userId);

        JsonNode body = request().body().asJson();
        if (body == null) body = Json.newObject();

        final F.Promise<Result> resultPromise = holder.put(body).map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> deleteContribution(String userId, String campaignId) {
      WSRequestHolder holder = WS.url(url + "/campaign/" + campaignId + "/user/" + userId);

      final F.Promise<Result> resultPromise = holder.delete().map(
        new F.Function<WSResponse, Result>() {
          public Result apply(WSResponse response) {
            return ok();
          }
        }
      );
      return resultPromise;
    }
}
