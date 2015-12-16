package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.joda.time.DateTime;

import play.Logger;
import play.db.ebean.Model.Finder;
import play.libs.Json;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import redis.clients.jedis.Jedis;
import com.typesafe.plugin.RedisPlugin;


/**
 * Orginally created by peter on 3/23/15.
 * refactored by nitin during Hackathon 12/12/15; 
 * Added Suggest Product feature by community members
 */
@Entity
public class ProductSuggestion extends SuperModel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String productName;

    public String productUrl;
    
    @ManyToOne
    public User user;

    @OneToOne
    public OpenGraph openGraph;

    @Column(columnDefinition = "TEXT")
    public String body;

	public boolean activeFlag = true;
    
    public static Finder<Long, ProductSuggestion> find = new Finder<Long, ProductSuggestion>(
            Long.class, ProductSuggestion.class);
    
//    public static Page<ProductSuggestion> find(int page) {
//	    return
//	            find.where()
//	                .orderBy("id desc")
//	                .findPagingList(1000)
//	                .setFetchAhead(false)
//	                .getPage(page);
//	}
    
    
    @Override
    public void save() {
    	
    	OpenGraph og = new OpenGraph();
        og.scrape(this.productUrl);
        og.save();
        openGraph = og;

        super.save();
        hmset();
		zadd("suggestion:all");
		if(user!=null) {
			zadd("suggestion:user:" + this.user.id);
		}

        //lpush();
    }
    

    @Override
    public void update() {
        OpenGraph og = new OpenGraph();
        og.scrape(this.productUrl);
        og.save();
        Long oldOGId = null;
        if (openGraph!=null){
        	oldOGId =openGraph.id; 
        }
        openGraph = og;
        super.update();
        if (oldOGId!=null){
        	OpenGraph.find.byId(oldOGId).delete();
        }
        hmset();
    }

    public static Page<ProductSuggestion> find(int page) {
        return
            find.where()
                .orderBy("id desc")
                .findPagingList(1000)
                .setFetchAhead(true)
                .getPage(page);
    }
    
    /*
     * get all suggestion submitted by the community members
     * not show the suggestions made by creators without login
     */
    public static List<ProductSuggestion> getAll() {
		return find
				.where()
				.eq("active_flag", "1")
				.isNotNull("user_id")
				.order("id desc")
				.findList();
		//TODO see if we can sort by most recent activity on the post
    }

	public static List<ProductSuggestion> getAllByUser(User user) {
		return find
				.where()
				.eq("user_id", user.id)
				.eq("active_flag", "1")
				.order("id desc")
				.findList();
	}


    public boolean isYoutubeVideo(){
        boolean res = false;

        String pattern = "(http(s?):\\/\\/)?(www\\.)?youtu(be)?\\.([a-z])+\\/(watch(.*?)(\\?|\\&)v=)?(.*?)(&(.)*)?";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(this.productUrl);
        res = m.find();
        Logger.debug("is youtube: " + res);
        return res;
    }

    public boolean isWebPage(){
        boolean res = false;
        Pattern urlpattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

        if(this.productUrl == null) return false;
        Logger.debug(productUrl);

        // Now create matcher object.
        Matcher murl = urlpattern.matcher(this.productUrl);

        if(murl.groupCount() == 0 ) return false;

        res = murl.matches();
        Logger.debug(murl.group(2));
        Logger.debug("is webpage: " + res);
        return res;
    }
    
    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("name", this.productName);
        node.put("url", this.productUrl);
        node.put("body", this.body);
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
       	List<Likes> likes = this.getAllLikes();
    		if(likes.size()>0){
    			//node.put("likes", Likes.toJson(likes));
    			node.put("likes", Likes.range(this.id.toString(), this.getClass().getName().toLowerCase()));
    		}

        if(this.openGraph != null) {
            node.put("openGraph", this.openGraph.toJson());
        }
        node.put("comments", Comment.range(this.id.toString(), this.getClass().getName().toLowerCase()));
        
         return node;
    }

    public static ArrayNode toJson(List<ProductSuggestion> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (ProductSuggestion item : items){
            arrayNode.add(item.toJson());
        }
        return arrayNode;
    }

    
	public static ArrayNode range(long start, long end){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "suggestion:all";

			if(!j.exists(key)) {
				Logger.debug("adding all suggestions to cache: " + key);
				for(ProductSuggestion suggestion: getAll()){
					suggestion.zadd(key, j);
					if(suggestion.user != null){
						suggestion.zadd("suggestion:user:" + suggestion.user.id, j);
					}
				}
			}

			Set<String> set = j.zrevrange(key, start, end);
			items.put("count", j.zcard(key));

			for(String id: set) {
				// get the data for each like
				data.add(ProductSuggestion.hmget(id, j));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return data;
	}


	public static ArrayNode userRange(String nickname, long start, long end){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		User user = User.findByNickname(nickname);
		if(user == null) return data;

		//Go to Redis to read the full roster of suggestion.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "suggestion:user:" + user.id.toString();

			if(!j.exists(key)) {
				Logger.debug("adding all user suggestions to cache: " + key);
				for(ProductSuggestion suggestion: getAllByUser(user)){
					suggestion.zadd("suggestion:user:" + suggestion.user.id, j);
				}
			}

			Set<String> set = j.zrevrange(key, start, end);

			for(String itemid: set) {
				// get the data for each like
				data.add(ProductSuggestion.hmget(itemid, j));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return data;
	}

	public static Long userCount(Long id){
		//Go to Redis to read the full roster of suggestion.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "suggestion:user:" + id.toString();

			Long number = j.zcard(key);
			return number;
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

    
    public void zadd(String key){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
          Logger.debug("added to set: " + key);
          j.zadd(key, updatedAt.getTime(), this.id.toString());
        } finally {
          play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }
      }

      public void zadd(String key, Jedis j){
        Logger.debug("added to set: " + key);
        j.zadd(key, updatedAt.getTime(), this.id.toString());
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

      public void zrem(String key, Jedis j){
        // delete
        j.zrem(key, this.id.toString());
      }

    public void hmset(){
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            Map suggestion = new HashMap();

            // Create the hashmap values
            suggestion.put("id", this.id.toString());
            suggestion.put("name", this.productName.toString());
            suggestion.put("url", this.productUrl);
            suggestion.put("body", this.body);
            suggestion.put("createdAt", new DateTime(this.createdAt).toString());
            if(this.openGraph != null) {
                if (this.openGraph.title != null) suggestion.put("title", this.openGraph.title);
                if (this.openGraph.description != null) suggestion.put("description", this.openGraph.description);
                if (this.openGraph.image != null) suggestion.put("image", this.openGraph.image);
                if (this.openGraph.video != null) suggestion.put("video", this.openGraph.video);
            }
            else{
            	suggestion.put("title", "Link");
            }

            if(this.user != null) {
            	suggestion.put("user_id", this.user.id.toString());
            }

            // add the values
            j.hmset("suggestion:" + this.id.toString(), suggestion);
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
            String key = "suggestion:" + id;

            if(!j.exists(key)) {
                Logger.debug("suggestion added to cache " + key);
                ProductSuggestion.find.byId(Long.parseLong(id, 10)).hmset();
            }

            List<String> values = j.hmget(key, "name", "url", "body", "createdAt", "description", "image", "video", "user_id", "title");

            node.put("id", id);
            if (values.get(0) != null) node.put("name", values.get(0));
            if (values.get(1) != null) node.put("url", values.get(1));
            if (values.get(2) != null) node.put("body", values.get(2));
            if (values.get(3) != null) node.put("createdAt", values.get(3));
            if (values.get(4) != null) node.put("description", values.get(4));
            if (values.get(5) != null) node.put("image", values.get(5));
            if (values.get(6) != null) node.put("video", values.get(6));
            if (values.get(7) != null) node.put("user_id", values.get(7));
            if (values.get(8) != null) node.put("title", values.get(8));

            
            if (values.get(7) != null) {
                node.put("user", User.hmget(values.get(7)));
            }
            node.put("likes", Likes.range(id, "models.productsuggestion"));
            node.put("comments", Comment.range(id, "models.productsuggestion"));


        } finally {
        }
        return node;
    }

    
    
    
}
