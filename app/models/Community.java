package models;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.joda.time.DateTime;

import play.Logger;
import play.db.ebean.Model.Finder;
import play.libs.F;
import play.libs.Json;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;
import utils.Slugify;
import redis.clients.jedis.Jedis;
import com.typesafe.plugin.RedisPlugin;


@Entity
public class Community extends SuperModel implements PathBindable<Community>,
QueryStringBindable<Community>{

	private static final long serialVersionUID = 1L;
	public static final String zset_key = "community:active";


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	public String name;
	
	public String slug;
	
	public String tagline;
	
	@OneToOne
	@Column(nullable=true)
	public File logoImage;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="community")
	public List<Product> products;
	
	@Column(nullable=false, columnDefinition="boolean default true")
	public boolean activeFlag;
	
	@Override
	public void save() {
		cacheRemove();
		this.slugify();
		super.save();
		this.hmset();
		if(activeFlag) this.zadd(zset_key);
		if(!activeFlag) this.zrem(zset_key);
	}

	@Override
	public void update(Object o) {
		cacheRemove();
		this.slugify();
		super.update(o);
		this.hmset();
		if(activeFlag) this.zadd(zset_key);
		if(!activeFlag) this.zrem(zset_key);
	}
	
	public void update() {
		cacheRemove();
		this.slugify();
		super.update();
		this.hmset();
		if(activeFlag) this.zadd(zset_key);
		if(!activeFlag) this.zrem(zset_key);
	}

	/*
		Clear community item from cache
	 */
	public void cacheRemove(){
		if(this.id != null) {
			play.cache.Cache.remove("community:" + this.id);
			//this.hmset();
		}
	}

	public void slugify() {
		// update slug every time community is saved
		try {
			slug = new Slugify().slugify(name);

			//prevent duplicates
			// ToDo
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static Finder<Long, Community> find = new Finder<Long, Community>(
			Long.class, Community.class);

	public static List<Community> getAll() {
		return find.all();
	}

	public static List<Community> getAllActive() {
		return find.where().eq("activeFlag", "1").findList();
	}
	
	public static Community findbyID(Long id) {
		return find.byId(id);
	}

	public static Community findBySlug(String value) {
		return find.where().eq("slug", value).findUnique();
	}
	
	public Community findbySlug(String value) {
		return find.where().eq("slug", value).findUnique();
	}

	public List<Community> findbyCommunityName(String name) {
		List<Community> results;
		 return results = find.where().eq("name", name).findList();
	}


	public static Page<Community> find(int page) {
		    return
		            find.where()
		            	.orderBy("active_flag desc, id desc")
		                .findPagingList(2000)
		                .setFetchAhead(false)
		                .getPage(page);
	}
	

	/*
	 * Look in the db for a community with a slug equal
	 * to the one passed in the URL
	 */
	@Override
	  public Community bind(String key, String value) {
	    return findbySlug(value);
	  }


	@Override
	public F.Option<Community> bind(String key, Map<String, String[]> data) {
	   return F.Option.Some(findbySlug(data.get("slug")[0]));
	}

	/*
	 * Unbinding
	 * return our raw value
	 */
	@Override
	public String unbind(String s) {
		return this.slug;
	}

	/*
	 * Unbinding javascript
	 * return our raw value
	 */
	@Override
	public String javascriptUnbind() {
		return this.slug;
	}


	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("id", this.id);
		node.put("name", this.name);
		node.put("slug", this.slug);
        node.put("tagline", this.tagline);
        node.put("logo", logoImage.toJson());
		node.put("products", Product.toJson(this.products));
		return node;
	}

	public static ArrayNode toJson(List<Community> communities){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Community community : communities){
			arrayNode.add(community.toJson());
		}
		return arrayNode;
	}
	
	public void zadd(String key){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			Logger.debug("community added to set: " + key);
			j.zadd(key, updatedAt.getTime(), this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void zrem(String key){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// delete
			j.zrem(key, this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ArrayNode range(){
		ArrayNode data = new ArrayNode(JsonNodeFactory.instance);

		//Go to Redis to read the full roster.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			if(!j.exists(zset_key)) {
				Logger.debug("adding products to cache: " + zset_key);
				for(Community item: Community.getAllActive()){
					item.zadd(zset_key);
				}
			}

			Set<String> set = j.zrange(zset_key, 0, -1);

			for(String id: set) {
				// get the data for each item
				data.add(Community.hmget(id));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return data;
	}

	public void hmset(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			Map map = new HashMap();

			// Create the hashmap values
			map.put("name", this.name);
			map.put("slug", this.slug);
			if(this.tagline!=null) map.put("tagline", this.tagline);
			if (logoImage != null) {
				map.put("logo", logoImage.id.toString());
			}

			// add the values
			j.hmset("community:" + this.id.toString(), map);
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
		try {
			String key = "community:" + id.toString();

			// if key not found then add item to cache
			if(!j.exists(key)) {
				Logger.debug("community added to cache " + key);
				Community.find.byId(Long.parseLong(id, 10)).hmset();
			}

			// get the values
			List<String> values = j.hmget(key,
					"name",
					"slug",
					"tagline",
					"logo"
			);

			// build json object
			node.put("id", id);
			if (values.get(0) != null) node.put("name", values.get(0));
			if (values.get(1) != null) node.put("slug", values.get(1));
			if (values.get(2) != null) node.put("tagline", values.get(2));
			if (values.get(3) != null) {
				node.put("logo", File.hmget(values.get(6)));
			}
     		node.put("products", Product.range(Long.parseLong(id, 10)));

		} finally {
			//do something
		}
		return node;
	}

	
}
