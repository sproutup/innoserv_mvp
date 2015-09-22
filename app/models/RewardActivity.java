package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import org.joda.time.DateTime;
import play.Logger;
import play.data.validation.Constraints;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import utils.Taggable;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Post class
 */
@Entity
public class RewardActivity extends SuperModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, RewardActivity> find = new Finder<Long, RewardActivity>(
            Long.class, RewardActivity.class);

    @Id
    public Long id;

    public String title;

	public Long points;

	public String description;

	public String criteria;

	@ManyToOne
	@JoinColumn(name="reward_category_id")
	public RewardCategory category;

	public boolean active = true;

	public static RewardActivity findById(Long id) {
        return find.byId(id);
    }

	@Override
	public void save() {
		super.save();
		hmset();
	}

	@Override
	public void update() {
		super.update();
		hmset();
	}

    private ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("title", this.title);
        node.put("points", this.points);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
        return node;
    }

    public static ArrayNode toJson(List<RewardActivity> posts){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (RewardActivity post : posts){
            arrayNode.add(post.toJson());
        }
        return arrayNode;
    }

    public static List<RewardActivity> getAll() {
        return find.where().order("id desc").findList();
    }
/*
	public static ArrayNode range(long start, long end){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "buzz:all";

			if(!j.exists(key)) {
				Logger.debug("adding all posts to cache: " + key);
				for(RewardActivity post: getAll()){
					post.zadd(key, j);
				}
			}

			Set<String> set = j.zrevrange(key, start, end);
			items.put("count", j.zcard(key));

			for(String id: set) {
				// get the data for each like
				data.add(RewardActivity.hmget(id, j));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return data;
	}
*/
	public void hmset(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			hmset(j);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void hmset(Jedis j){
		Map map = new HashMap();

		// Create the hashmap values
		map.put("title", this.title);
		map.put("points", this.points.toString());
		map.put("created_at", new DateTime(this.createdAt).toString());

		// add the values
		j.hmset("reward:activity:" + this.id.toString(), map);
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
		String key = "reward:activity:" + id;

		if(!j.exists(key)) {
			Logger.debug("activity added to cache " + key);
			RewardActivity.find.byId(Long.parseLong(id, 10)).hmset(j);
		}

		List<String> values = j.hmget(key, "title", "created_at", "points");

		node.put("id", id);
		if (values.get(0) != null) node.put("title", values.get(0));
		if (values.get(1) != null) node.put("created_at", values.get(1));
		if (values.get(2) != null) node.put("points", values.get(2));

		return node;
	}
}
