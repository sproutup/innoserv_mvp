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

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Post class
 */
@Entity
public class RewardCategory extends SuperModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, RewardCategory> find = new Finder<Long, RewardCategory>(
            Long.class, RewardCategory.class);

    @Id
    public Long id;

    public String title;

    public String description;

	@OneToMany
	public List<RewardActivity> activities;

	public boolean active = true;

	public static RewardCategory findById(Long id) {
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
		node.put("description", this.description);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.activities != null) {
            node.put("events", RewardActivity.toJson(this.activities));
        }
        return node;
    }

    public static ArrayNode toJson(List<RewardCategory> posts){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (RewardCategory post : posts){
            arrayNode.add(post.toJson());
        }
        return arrayNode;
    }

	public static List<RewardCategory> getAll() {
		return find.where().order("id desc").findList();
	}

	public static List<RewardCategory> getAllActive() {
		return find.where()
				.eq("active", "1")
				.order("id desc")
				.findList();
	}

	public RewardCategory getPostbyID(Long postID){
		return null;
	}

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
		map.put("id", this.id);
		map.put("title", this.title);
		map.put("description", this.description);
		map.put("created_at", new DateTime(this.createdAt).toString());

		// add the values
		j.hmset("reward_category:" + this.id.toString(), map);
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
		String key = "reward_category:" + id;

		if(!j.exists(key)) {
			Logger.debug("reward_category added to cache " + key);
			RewardCategory.find.byId(Long.parseLong(id, 10)).hmset(j);
		}

		List<String> values = j.hmget(key, "id", "title", "description", "created_at");

		node.put("id", id);
		if (values.get(1) != null) node.put("title", values.get(1));
		if (values.get(2) != null) node.put("description", values.get(2));
		if (values.get(3) != null) node.put("created_at", values.get(3));

		return node;
	}
}
