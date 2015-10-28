package models;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
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

/**
 * Post class
 */
@Entity
public class RewardEvent extends SuperModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, RewardEvent> find = new Finder<Long, RewardEvent>(
            Long.class, RewardEvent.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

	public Long points;

	@ManyToOne
	public User user;

	@ManyToOne
	public Product product;

	@OneToOne
	public Content content;

	public boolean active = true;

    @ManyToOne
	@JoinColumn(name="reward_activity_id")
    public RewardActivity activity;

	public static RewardEvent findById(Long id) {
		return find.byId(id);
	}

	public static List<RewardEvent> findByUser(User user) {

		List<RewardEvent> events = new RewardEvent()
				.find
				.where()
				.eq("user_id", user.id)
				.orderBy("id asc")
				.findList();

		return events;
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

	/*
      Get reward summary
    */
	public static ArrayNode getUserSummary(Long id) {
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		// sql that selects all the tag links for the object
		String sql = "select re.reward_activity_id, ra.title, count(re.id), sum(re.points) " +
				"from reward_event re, reward_activity ra " +
				"where re.reward_activity_id = ra.id " +
				"and re.user_id = " + id + " " +
				"group by reward_activity_id";

		// parse the sql
		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("re.reward_activity_id", "id")
				.columnMapping("ra.title", "title")
				.columnMapping("count(re.id)", "quantity")
				.columnMapping("sum(re.points)", "points")
				.create();

		// execute the query
		List<RewardAggregate> summary = RewardAggregate.find.query()
				.setRawSql(rawSql)
				.where()
				.findList();

		for (RewardAggregate item : summary){
			ObjectNode res = Json.newObject();
			res.put("id", item.id);
			res.put("title", item.title);
			res.put("quantity", item.quantity);
			res.put("points", item.points);
			arrayNode.add(res);
		}

		return arrayNode;
	}

    private ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("points", this.points);
		node.put("title", this.activity.title);
		node.put("activity_id", this.activity.id);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
        return node;
    }

    public static ArrayNode toJson(List<RewardEvent> events){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (RewardEvent event : events){
            arrayNode.add(event.toJson());
        }
        return arrayNode;
    }

	public static List<RewardEvent> getAll() {
		return find.where().order("id desc").findList();
	}

	public static List<RewardEvent> getLatest(Long userId) {
		return find.where().eq("user_id", userId).order("id desc").setMaxRows(5).findList();
	}

	public static ArrayNode range(long start, long end){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "reward:event:all";

			if(!j.exists(key)) {
				Logger.debug("adding all events to cache: " + key);
				for(RewardEvent event: getAll()){
					event.zadd(key, j);
				}
			}

			Set<String> set = j.zrevrange(key, start, end);
			items.put("count", j.zcard(key));

			for(String id: set) {
				// get the data for each like
				data.add(RewardEvent.hmget(id, j));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return data;
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
		map.put("id", this.id.toString());
		map.put("createdAt", new DateTime(this.createdAt).toString());
		map.put("points", this.points.toString());

		if(this.user != null) {
			map.put("user_id", this.user.id.toString());
		}
		if(this.product != null) {
			map.put("product_id", this.product.id.toString());
		}
		if(this.content != null) {
			map.put("content_id", this.content.id.toString());
		}

		// add the values
		j.hmset("reward_event:" + this.id.toString(), map);
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
		String key = "reward_event:" + id;

		if(!j.exists(key)) {
			Logger.debug("reward event added to cache " + key);
			RewardEvent.find.byId(Long.parseLong(id, 10)).hmset(j);
		}

		List<String> values = j.hmget(key, "id", "created_at", "points", "user_id", "product_id", "content_id");

		node.put("id", id);
		//if (values.get(0) != null) node.put("id", values.get(0));
		if (values.get(1) != null) node.put("created_at", values.get(1));
		if (values.get(1) != null) node.put("points", Long.parseLong(values.get(2), 10));

		return node;
	}
}
