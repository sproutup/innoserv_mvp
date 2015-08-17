package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import org.joda.time.DateTime;
import play.libs.Json;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;
import redis.clients.jedis.Jedis;

@Entity
public class Likes extends TimeStampModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static Likes findById(Long id) {
		return find.byId(id);
	}

	public static Finder<Long, Likes> find = new Finder<Long, Likes>(Long.class,
			Likes.class);

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	public Long refId;

	public String refType;

	public Likes() {
		// Left empty
	}


	/*
	Add like to an object identified by refId and refType
	 */
	public static Likes addLike(Long userId, Long refId, String refType){
		Likes rs = null;

		// find user
		User user = User.find.byId(userId);

		if(user==null){
			return null;
		}

		Logger.debug("addLikes: found user");

		// see if like exists already
		rs = Likes.find.where()
				.eq("user", user)
				.eq("refId", refId)
				.eq("refType", refType)
				.findUnique();

		if(rs == null) {
			// Like not found so we insert
			rs = new Likes();
			rs.user = user;
			rs.refId = refId;
			rs.refType = refType;
			rs.save();

			// add to cache
			rs.hmset();
			rs.zadd(refId.toString(), refType);
		}

		return rs;
	}

	/*
	Remove like from an object identified by refId and refType
	 */
	public static void removeLike(Long userId, Long refId, String refType) {
		Likes rs = null;

		// find user
		User user = User.find.byId(userId);

		// if user is found then find like
		if(user != null) {
			rs = Likes.find.where()
					.eq("user", user)
					.eq("refId", refId)
					.eq("refType", refType)
					.findUnique();
		}

		// if link is found then delete
		if(rs != null){
			// delete from cache
			rs.zrem(refId.toString(), refType);
			rs.del();
			// delete from db
			rs.delete();
		}
	}

	/*
	Remove all likes from an object identified by refId and refType
	 */
	public static void removeAllLikes(Long refId, String refType) {

		// find all likes
		List<Likes> likes = Likes.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		// delete likes one by one
		for (Likes like : likes) {
			// delete from cache
			like.zrem(refId.toString(), refType);
			like.del();
			// delete from db
			like.delete();
		}
	}

	/*
	Get all likes on an object identified by refId and refType
	 */
	public static List<Likes> getAllLikes(Long refId, String refType) {

		// find all likes
		List<Likes> likes = Likes.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		return likes;
	}

	public static ObjectNode range(String refId, String refType){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "likes:"+ refType + ":" + refId;

			if(!j.exists(key)) {
				Logger.debug("adding likes to cache: " + key);
				for(Likes like: getAllLikes(Long.parseLong(refId, 10), refType)){
					like.zadd(refId, refType);
				}
			}

			Set<String> set = j.zrange(key, 0, -1);
			items.put("count", j.zcard(key).toString());

			for(String id: set) {
				// get the data for each like
				data.add(Likes.hmget(id));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return items;
	}

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
        node.put("id", this.user.id);
        node.put("name", this.user.name);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		return node;
	}

	public static ArrayNode toJson(List<Likes> likes){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Likes like : likes) {
			arrayNode.add(like.toJson());
		}
		return arrayNode;
	}

	public void zadd(String refId, String refType){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// compose key
			String key = "likes:"+ refType + ":" + refId;
			//String key = "likes:" + id.toString();
			// left push to list
			//j.lpush(key, this.id.toString());
			Logger.debug("like added to list " + key);
			j.zadd(key, createdAt.getTime(), this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void zrem(String refId, String refType){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// compose key
			String key = "likes:"+ refType + ":" + refId;
			// delete
			j.zrem(key, this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void hmset(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			Map content = new HashMap();

			// Create the hashmap values
			content.put("id", this.id.toString());
			content.put("createdAt", new DateTime(this.createdAt).toString());

			if(this.user != null) {
				content.put("user.id", this.user.id.toString());
			}

			// add the values
			j.hmset("like:" + this.id.toString(), content);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode hmget(String id){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		ObjectNode node = Json.newObject();
		try {
			String key = "like:" + id.toString();

			if(!j.exists(key)) {
				Logger.debug("like added to cache " + key);
				Likes.find.byId(Long.parseLong(id, 10)).hmset();
			}

			List<String> values = j.hmget(key, "id", "createdAt", "user.id");

			if (values.get(0) != null) node.put("id", values.get(0));
			if (values.get(1) != null) node.put("createdAt", values.get(1));
			if (values.get(2) != null) {
				node.put("user", User.hmget(values.get(2)));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
		return node;
	}

	public void del(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// delete
			j.del("like:" + this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

}
