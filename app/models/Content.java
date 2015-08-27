package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import org.joda.time.DateTime;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import play.Logger;

/**
 * Created by peter on 6/26/15.
 */
@Entity
public class Content extends SuperModel {

    public static Finder<Long, Content> find = new Finder<Long, Content>(
            Long.class, Content.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String url;

    @ManyToOne
    public ProductTrial productTrial;

    @ManyToOne
    public Trial trial;

    @ManyToOne
    public Product product;

    @ManyToOne
    public User user;

    @OneToOne
    public OpenGraph openGraph;

    @Override
    public void save() {
        super.save();
        hmset();
        lpush();
    }

    @Override
    public void update() {
        super.update();
        hmset();
    }

    public static Page<Content> find(int page) {
        return
            find.where()
                .orderBy("id desc")
                .findPagingList(1000)
                .setFetchAhead(true)
                .getPage(page);
    }

    public ObjectNode toFullJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("url", this.url);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        if(this.product != null) {
            node.put("product", this.product.toJsonShort());
        }
        if(this.openGraph != null) {
            node.put("openGraph", this.openGraph.toJson());
        }
        return node;
    }

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("url", this.url);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
//        if(this.user != null) {
//            node.put("user", this.user.toJsonShort());
//        }
//        if(this.product != null) {
//            node.put("product", this.product.toJsonShort());
//        }
        if(this.openGraph != null) {
            node.put("openGraph", this.openGraph.toJson());
        }
        return node;
    }

    public static ArrayNode toJson(List<Content> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Content item : items){
            arrayNode.add(item.toJson());
        }
        return arrayNode;
    }

    public static void initRedis(){
        Logger.debug("init redis");

        clearRedis();

        for (Content content : find.all()){
            content.hmset();
            content.lpush();
        }
    }

    public void lpush(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            // There is an interesting thing to notice in the code below:
            // we use a new command called LTRIM after we perform the LPUSH operation in the global timeline.
            // This is used in order to trim the list to just 1000 elements.
            // The global timeline is actually only used in order to show a few posts in the home page,
            // there is no need to have the full history of all the posts.
            // Basically LTRIM + LPUSH is a way to create a capped collection in Redis.

            j.lpush("feed:all", this.id.toString());
            j.ltrim("feed:all", 0, 1000);
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
    }

    public static void clearRedis(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            j.del("feed:all");
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
    }

    public void hmset(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            Map content = new HashMap();

            // Create the hashmap values
            content.put("url", this.url);
            content.put("createdAt", new DateTime(this.createdAt).toString());
            if(this.openGraph != null) {
                if (this.openGraph.title != null) content.put("title", this.openGraph.title);
                if (this.openGraph.description != null) content.put("description", this.openGraph.description);
                if (this.openGraph.image != null) content.put("image", this.openGraph.image);
                if (this.openGraph.video != null) content.put("video", this.openGraph.video);
            }
            else{
                content.put("title", "Link");
            }

            if(this.user != null) {
                content.put("user.id", this.user.id.toString());
            }
            if(this.product != null) {
                content.put("product.id", this.product.id.toString());
            }

            // add the values
            j.hmset("content:" + this.id.toString(), content);
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
    }

    public static ObjectNode hmget(String id){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        ObjectNode node = Json.newObject();
        try {
            String key = "content:" + id;
            List<String> values = j.hmget(key, "title", "url", "createdAt", "description", "image", "video", "user.id", "product.id");

            node.put("id", id);
            if (values.get(0) != null) node.put("title", values.get(0));
            if (values.get(1) != null) node.put("url", values.get(1));
            if (values.get(2) != null) node.put("createdAt", values.get(2));
            if (values.get(3) != null) node.put("description", values.get(3));
            if (values.get(4) != null) node.put("image", values.get(4));
            if (values.get(5) != null) node.put("video", values.get(5));
            if (values.get(6) != null) {
                node.put("user", User.hmget(values.get(6)));
            }
            if (values.get(7) != null) {
                node.put("product", Product.hmget(values.get(7)));
            }
            node.put("likes", Likes.range(id, "models.content"));
            node.put("comments", Comment.range(id, "models.content"));

        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
        return node;
    }
}
