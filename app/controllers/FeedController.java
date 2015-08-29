package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import models.Content;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by peter on 3/19/15.
 */
public class FeedController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAll()
    {
        return getRange(0);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getRange(int start)
    {
        ObjectNode feed = Json.newObject();
        ArrayNode content = feed.putArray("content");
        //Go to Redis to read the full roster of content.
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            if(!j.exists("feed:all")) Content.initRedis();

            List<String> list = j.lrange("feed:all", start, start+10);
            for(String id: list) {
                content.add(Content.hmget(id));
            }
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }

        return content == null ? notFound("Feed out of bounds [" + start + "]") : ok(content);
    }
}
