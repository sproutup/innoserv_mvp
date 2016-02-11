package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import models.Content;
import models.Post;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import redis.clients.jedis.Jedis;

import play.Play;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import java.util.List;
import java.util.Set;

/**
 * Created by peter on 3/19/15.
 */
public class BuzzController extends Controller {
  private static String url = Play.application().configuration().getString("main.api.url");

    public static F.Promise<Result> getAll() {
        WSRequestHolder holder = WS.url(url + "/post/timeline/all");

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    public static F.Promise<Result> getRange(int start)
    {
      WSRequestHolder holder = WS.url(url + "/post/timeline/all/" + start);

      final F.Promise<Result> resultPromise = holder.get().map(
              new F.Function<WSResponse, Result>() {
                  public Result apply(WSResponse response) {
                      return ok(response.asJson());
                  }
              }
      );
      return resultPromise;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProductRange(String slug, int start)
    {
        return ok(Post.productRange(slug, start, start+9 ));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getUserRange(String nickname, int start)
    {
        return ok(Post.userRange(nickname, start, start+9 ));
    }
}
