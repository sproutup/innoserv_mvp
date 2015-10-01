package models;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import constants.AppConstants;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import service.GoogleURLShortener;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 3/23/15.
 */
@Entity
public class TrialLog extends TimeStampModel {

    public static Finder<Long, TrialLog> find = new Finder<Long, TrialLog>(
            Long.class, TrialLog.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

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
    public Trial trial;

    public static TrialLog findById(Long id) {
        return find.byId(id);
    }

    public static Page<TrialLog> find(int page) {
	    return
            find
            .where()
            .orderBy("id desc")
            .findPagingList(2000)
            .setFetchAhead(false)
            .getPage(page);
	}
    
    public String getStatus() {
		return status.toString();
	}

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("status", this.status);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        //node.put("updatedAt", new DateTime(this.updatedAt).toString());
        return node;
    }

    public static ArrayNode toJson(List<TrialLog> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (TrialLog item : items){
            arrayNode.add(item.toJson());
        }
        return arrayNode;
    }

    public static DateTime findTrialReceivedDate(List<TrialLog> items){
        for (TrialLog item : items){
            if(item.status == 3) return new DateTime(item.createdAt);
        }
        return null;
    }

    public void hmset(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            Map map = new HashMap();

            // Create the hashmap values
            map.put("status", this.status);
            map.put("updatedAt", new DateTime(this.updatedAt).toString());

            // add the values
            j.hmset(key(id.toString()) + this.id.toString(), map);
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
    }

    public static String key(String id){
        return "trial:log:" + id;
    }

    public static TrialLog log(Trial trial){
        TrialLog log = new TrialLog();
        log.status = trial.status;
        log.trial = trial;
        log.save();
        return log;
    }

    public static ObjectNode hmget(String id){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        ObjectNode node = Json.newObject();
        try {
            String key = TrialLog.key(id);
            // if key not found then add item to cache
            if(!j.exists(key)) {
                Logger.debug("item added to cache " + key);
                TrialLog.find.byId(Long.parseLong(id, 10)).hmset();
            }

            // get the values
            List<String> values = j.hmget(key,
                    "status",
                    "updatedAt"
            );

            // build json object
            node.put("id", id);
            if (values.get(0) != null) node.put("status", values.get(0));
            if (values.get(1) != null) node.put("updatedAt", values.get(1));
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
        return node;
    }
}
