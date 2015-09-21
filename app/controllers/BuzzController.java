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

import java.util.List;
import java.util.Set;

/**
 * Created by peter on 3/19/15.
 */
public class BuzzController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAll()
    {
        return getRange(0);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getRange(int start)
    {
        return ok(Post.range(start, start + 9));

/*
        ObjectNode buzz = Json.newObject();
        ArrayNode items = buzz.putArray("buzz");
        //Go to Redis to read the full roster of content.
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            Set<String> set = j.zrange("buzz:all", start, start + 9);
            for(String id: set) {
                items.add(Post.hmget(id));
            }
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
*/

//        return items == null ? notFound("buzz out of bounds [" + start + "]") : ok(items);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProductRange(String slug, int start)
    {
        return ok(Post.range(slug, start, start+9 ));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getUserRange(Long user_id, int start)
    {
        return ok(Post.range(user_id, start, start+9 ));
    }
}
