package models;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import constants.AppConstants;

import org.joda.time.DateTime;

import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import com.typesafe.plugin.RedisPlugin;
import service.GoogleURLShortener;

import javax.persistence.*;

import java.util.*;

/**
 * Created by peter on 3/23/15.
 */
@Entity
public class Trial extends SuperModel {

    public static Finder<Long, Trial> find = new Finder<Long, Trial>(
            Long.class, Trial.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String name;

    public String phone;

    public String address;

    public String reason;

    public String refURL;

    public boolean active = true;

    /*
    0 : requested
    1 : approved
    2 : sent
    3 : received
    4 : returned
    -1 : rejected
    -2 : cancelled
    */
    public Integer status = 0;

	@ManyToOne
    public Product product;

    @OneToMany
    public List<Content> content;

    @OneToMany(mappedBy="trial")
    public List<TrialLog> log;

    @ManyToOne
    public User user;

    @Override
    public void save() {
        Logger.debug("trial save()");
        super.save();
        TrialLog.log(this);
        this.hmset();
        if(status>=0) {
            this.zadd("zset:trial:product:" + this.product.id);
        }
        else{
            this.zrem("zset:trial:product:" + this.product.id);
        }
    }

    @Override
    public void update() {
        Logger.debug("trial update()");
        super.update();
        TrialLog.log(this);
        this.hmset();
        if(status>=0) {
            this.zadd("zset:trial:product:" + this.product.id);
        }
        else{
            this.zrem("zset:trial:product:" + this.product.id);
        }
    }

    public static Trial findById(Long id) {
        return find.byId(id);
    }

    public static Page<Trial> find(int page) {
	    return
            find
            .where()
            .orderBy("id desc")
            .findPagingList(2000)
            .setFetchAhead(false)
            .getPage(page);
	}

    public static Page<Trial> findTrialsbyInfluencers(int page) {
    	
    	return
            find.fetch("user", new FetchConfig().query())
            		.fetch("user.roles", new FetchConfig().query())
            		.where()
                    .eq("user.roles.roleName", AppConstants.INFLUENCER)
                    .orderBy("id desc")
                .findPagingList(2000)
                .setFetchAhead(false)
                .getPage(page);
	}

    
    public String getStatus() {
		return status.toString();
	}

    public String generateRefUrl() {
        String referralId = UserReferral.getReferralId(user.id, null, this.id);
        String genURL = "http://sproutup.co/product/" + product.slug + "?refId="+ referralId;
        this.refURL = GoogleURLShortener.shortenURL(genURL);
        return this.refURL;
    }

    public ObjectNode toFullJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("name", this.name);
        node.put("email", this.email);
        node.put("address", this.address);
        node.put("reason", this.reason);
        node.put("phone", this.phone);
        node.put("active", this.active);
        node.put("status", this.status);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        if(this.product != null) {
            node.put("product", this.product.toJsonShort());
        }
        if(this.content != null) {
            node.put("content", Content.toJson(this.content));
        }
        node.put("refURL", refURL);
        return node;
    }

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("name", this.name);
//        node.put("email", this.email);
//        node.put("address", this.address);
        node.put("reason", this.reason);
//        node.put("phone", this.phone);
        node.put("active", this.active);
        node.put("status", this.status);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());

        if(this.log != null) {
            DateTime received = TrialLog.findTrialReceivedDate(this.log);
            if (received != null){
                node.put("trialEndsAt", received.plusDays(this.product.trialDurationNbrDays).toString());
            }
        }

        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        if(this.product != null) {
            node.put("product", this.product.toJsonShort());
        }
        if(this.content != null) {
            node.put("content", Content.toJson(this.content));
        }
        if(this.log != null) {
            node.put("log", TrialLog.toJson(this.log));
        }
        node.put("refURL", refURL);
        return node;
    }

    public ObjectNode toJsonShort(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
//        node.put("name", this.name);
        node.put("reason", this.reason);
        node.put("status", this.status);
//        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).plusDays(14).toString());
        if(this.user != null) {
            //node.put("user", this.user.toJsonShort());
            node.put("user", User.hmget(this.user.id.toString()));
        }
        if(this.product != null) {
            node.put("product", this.product.toJsonShort());
        }
//        node.put("refURL", refURL);
        return node;
    }

    public ObjectNode toJsonSuperShort(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
//        node.put("name", this.name);
//        node.put("reason", this.reason);
        node.put("status", this.status);
//        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).plusDays(14).toString());
        if(this.user != null) {
            //node.put("user", this.user.toJsonShort());
            node.put("user", User.hmget(this.user.id.toString()));
        }
//        if(this.product != null) {
//            node.put("product", this.product.toJsonShort());
//        }
//        node.put("refURL", refURL);
        return node;
    }

    public static ArrayNode toJson(List<Trial> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Trial item : items){
            arrayNode.add(item.toJson());
        }
        return arrayNode;
    }

    public static ArrayNode toJsonShort(List<Trial> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Trial item : items){
            arrayNode.add(item.toJsonShort());
        }
        return arrayNode;
    }

    public static ObjectNode toJsonOverview(List<Trial> items){
        ObjectNode node = Json.newObject();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Trial item : items){
            arrayNode.add(item.toJsonSuperShort());
        }
        node.put("count", items.size());
        node.put("users", arrayNode);

        return node;
    }

    public void hmset(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            Map map = new HashMap();

            // Create the hashmap values
            map.put("id", this.id.toString());
            map.put("updatedAt", new DateTime(this.updatedAt).toString());
            map.put("createdAt", new DateTime(this.createdAt).toString());
            map.put("name", this.name);
            map.put("reason", this.reason);
            map.put("status", this.status.toString());
            if(this.user != null) {
                map.put("user_id", this.user.id.toString());
            }
            if(this.product != null) {
                map.put("product_id", this.product.id.toString());
            }

            // add the values
            j.hmset("trial:" + this.id.toString(), map);
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
    }

    public static String key(String id){
        return "trial:" + id;
    }

    public static ObjectNode hmget(String id){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            return hmget(id, j);
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
    }

    public static ObjectNode hmget(String id, Jedis j){
        ObjectNode node = Json.newObject();

        String key = Trial.key(id);
        // if key not found then add item to cache
        if(!j.exists(key)) {
            Logger.debug("item added to cache " + key);
            Trial.find.byId(Long.parseLong(id, 10)).hmset();
        }

        // get the values
        List<String> values = j.hmget(key,
                "id",
                "updatedAt",
                "createdAt",
                "name",
                "reason",
                "status",
                "user_id",
                "product_id"
                );

        // build json object
        node.put("id", id);
        if (values.get(0) != null) node.put("id", Long.parseLong(values.get(0)));
        if (values.get(1) != null) node.put("updatedAt", values.get(1));
        if (values.get(2) != null) node.put("createdAt", values.get(2));
        if (values.get(3) != null) node.put("name", values.get(3));
        if (values.get(4) != null) node.put("reason", values.get(4));
        if (values.get(5) != null) node.put("status", Long.parseLong(values.get(5)));
        if (values.get(6) != null){
            node.put("user", User.hmget(values.get(6), j));
        }
        if (values.get(7) != null) node.put("product_id", Long.parseLong(values.get(7)));

        return node;
    }

    public static ObjectNode range(long product_id, long start, long end){
        ObjectNode items = Json.newObject();
        ArrayNode data = items.putArray("data");

        //Go to Redis to read the full roster of content.
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            String key = "zset:trial:product:" + product_id;

            if(!j.exists(key)) {
                Logger.debug("adding all trial users to cache: " + key);

                for(Trial item: Product.find.byId(product_id).trials){
                    if(item.status>=0) {
                        item.zadd(key, j);
                    }
                }
            }

            Set<String> set = j.zrevrange(key, start, end);
            items.put("count", j.zcard(key));

            for(String id: set) {
                // get the data for each like
                data.add(Trial.hmget(id, j));
            }
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }

        return items;
    }
}
