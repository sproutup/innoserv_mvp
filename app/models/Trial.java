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
public class Trial extends TimeStampModel {

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

    @ManyToOne
    public User user;

    @Override
    public void save() {
        if(this.product != null) this.product.cacheRemove();
        super.save();
    }

    @Override
    public void update() {
        if(this.product != null) this.product.cacheRemove();
        super.update();
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

    public ObjectNode toJsonShort(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
//        node.put("name", this.name);
        node.put("reason", this.reason);
        node.put("status", this.status);
//        node.put("createdAt", new DateTime(this.createdAt).toString());
//        node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        if(this.product != null) {
            node.put("slug", this.product.slug);
        }
//        if(this.content != null) {
//            node.put("content", Content.toJson(this.content));
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

    public void hmset(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            Map map = new HashMap();

            // Create the hashmap values
            map.put("name", this.name);
            map.put("updatedAt", new DateTime(this.updatedAt).toString());
            if(this.user != null) {
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
        ObjectNode node = Json.newObject();
        try {
            String key = Trial.key(id);
            // if key not found then add item to cache
            if(!j.exists(key)) {
                Logger.debug("item added to cache " + key);
                Trial.find.byId(Long.parseLong(id, 10)).hmset();
            }

            // get the values
            List<String> values = j.hmget(key,
                    "name",
                    "updatedAt"
            );

            // build json object
            node.put("id", id);
            if (values.get(0) != null) node.put("name", values.get(0));
            if (values.get(1) != null) node.put("updatedAt", values.get(1));
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
        return node;
    }
}
