package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class Comment extends SuperModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static Comment findById(Long id) {
		return find.byId(id);
	}

	public static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class,
			Comment.class);

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	public Long refId;

	public String refType;

	public String body;

	public Comment() {
		// Left empty
	}


	/*
	Add comment to an object identified by refId and refType
	 */
	public static Comment addComment(Long userId, String comment, Long refId, String refType){
		Comment rs = null;

		// find user
		User user = User.find.byId(userId);

		if(user==null){
			return null;
		}

		Logger.debug("addComment: found user");

		// Like not found so we insert
		rs = new Comment();
		rs.user = user;
		rs.body = comment;
		rs.refId = refId;
		rs.refType = refType;
		rs.save();

		// add to cache
		rs.hmset();
		rs.zadd(refId.toString(), refType);

		return rs;
	}

	/*
	Remove comment from an object identified by refId and refType and check if user owns it
	 */
	public static boolean deleteComment(Long commentId, Long refId, String refType, Long userId) {
		Comment rs = null;

		rs = Comment.find.byId(commentId);

		// if found then delete if user owns it
		if(rs != null && rs.user.id == userId){
			// delete from cache
			rs.zrem(refId.toString(), refType);
			rs.del();
			// delete from db
			rs.delete();
			return true;
		}
		return false;
	}

	/*
	Remove all comments from an object identified by refId and refType
	 */
	public static void removeAllComments(Long refId, String refType) {

		// find all
		List<Comment> items = Comment.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		// delete one by one
		for (Comment item : items) {
			item.delete();
		}
	}

	/*
	Get all comments on an object identified by refId and refType
	 */
	public static List<Comment> getAllComments(Long refId, String refType) {

		// find all
		List<Comment> items = Comment.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		return items;
	}

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
        node.put("id", this.id);
		node.put("body", this.body);
		node.put("createdAt", new DateTime(this.createdAt).toString());

		if(this.user != null) {
			node.put("user", this.user.toJsonShort());
		}

		return node;
	}

	public static ArrayNode toJson(List<Comment> items){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Comment item : items){
			arrayNode.add(item.toJson());
		}
		return arrayNode;
	}

	public void hmset(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			Map hashmap = new HashMap();

			// Create the hashmap values
			hashmap.put("id", this.id.toString());
			hashmap.put("createdAt", new DateTime(this.createdAt).toString());
			hashmap.put("body", this.body);

			if(this.user != null) {
				hashmap.put("user.id", this.user.id.toString());
			}

			// add the values
			j.hmset("comment:" + this.id.toString(), hashmap);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
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
		String key = "comment:" + id.toString();

		if(!j.exists(key)) {
			Logger.debug("comment added to cache " + key);
			Comment.find.byId(Long.parseLong(id, 10)).hmset();
		}

		List<String> values = j.hmget(key, "id", "createdAt", "body", "user.id");

		if (values.get(0) != null) node.put("id", values.get(0));
		if (values.get(1) != null) node.put("createdAt", values.get(1));
		if (values.get(2) != null) node.put("body", values.get(2));
		if (values.get(3) != null) {
			node.put("user", User.hmget(values.get(3)));
		}
		node.put("likes", Likes.range(id, "models.comment"));
		return node;
	}

	public void del(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// delete
			j.del("comment:" + this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void zadd(String refId, String refType){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			zadd(refId, refType, j);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void zadd(String refId, String refType, Jedis j){
		// compose key
		String key = "comments:"+ refType + ":" + refId;
		// left push to list
		Logger.debug("comment added to list " + key);
		j.zadd(key, createdAt.getTime(), this.id.toString());
	}

	public void zrem(String refId, String refType){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// compose key
			String key = "comments:"+ refType + ":" + refId;
			// delete
			j.zrem(key, this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode range(String refId, String refType){
		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			return range(refId, refType, j);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode range(String refId, String refType, Jedis j){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		String key = "comments:" + refType + ":" + refId;

		if(!j.exists(key+ ":init")) {
			Logger.debug("adding comments to cache: " + key);
			for(Comment item: getAllComments(Long.parseLong(refId, 10), refType)){
				item.zadd(refId, refType, j);
			}
			j.set(key + ":init", "1");
		}

		Set<String> set = j.zrange(key, 0, -1);
		items.put("count", j.zcard(key).toString());

		for(String id: set) {
			// get the data for each like
			data.add(Comment.hmget(id, j));
		}

		return items;
	}
}
