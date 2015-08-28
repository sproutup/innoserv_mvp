package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import org.joda.time.DateTime;
import play.Logger;
import play.data.validation.Constraints;
import play.libs.Json;
import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import utils.Taggable;

/**
 * Post class
 */
@Entity
public class Post extends SuperModel implements Taggable {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, Post> find = new Finder<Long, Post>(
            Long.class, Post.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String title;

    @Column(columnDefinition = "TEXT")
    public String body;

	public boolean activeFlag = true;

	@Constraints.Required //suggestion, question, compliment
	public Integer category;

    @OneToMany(mappedBy = "parent")
    public List<Post> comments;

    @ManyToOne
    public Post parent;

    @ManyToOne
    public User user;

	@ManyToOne
	public Product product;

	@OneToOne
	public Content content;

	public static Post findById(Long id) {
        return find.byId(id);
    }

    private ObjectNode toJsonRaw(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("title", this.title);
        node.put("body", this.body);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
		List<Likes> likes = this.getAllLikes();
		if(likes.size()>0){
			//node.put("likes", Likes.toJson(likes));
			node.put("likes", Likes.range(this.id.toString(), this.getClass().getName().toLowerCase()));
		}
		List<Tag> tags = this.getAllTags();
		if(tags.size()>0){
			node.put("tags", Tag.toJson(tags));
		}
        return node;
    }

    public ObjectNode toJson(){
        ObjectNode node = toJsonRaw();
        if(this.comments.size()>0) {
            ArrayNode list = node.putArray("comments");
            for (Post comment : this.comments) {
                list.add(comment.toJsonRaw());
            }
        }
        return node;
    }

    public static ArrayNode toJson(List<Post> posts){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Post post : posts){
            arrayNode.add(post.toJson());
        }
        return arrayNode;
    }

    public static List<Post> getAll() {
        return find.where().order("id desc").findList();
    }

	/*
	* @required categoryName
	* @required productID	 
	*/
	public List<Post> getActivePostsbyCategory(String categoryName, Long productID){
			return (List<Post>) find.where()
				.eq("category", categoryName)
				.eq("product_id", productID)
				.eq("active_flag", "1")
				.orderBy("date_time_stamp desc");
	}
		
	/*
	@return commentID
	*/
	public Long commentPost(Long postID, String commentText, Long userID){
		Long commentID = new Long(1);
		return commentID;
	}
	
	public Post getPostbyID(Long postID){
		return null;
	}

	/*
	 * future implementation for give me all post by tag
	 */
	public List<Post> getPostsbyTag(String[] tags){
		
		return null;
	}

	/*
	 * future implementation for give me all post on some keyword or text
	 */
	public List<Post> searchPosts(String keyword){
		
		return null;
	}
	
	/*
	Taggable interface implementation
	 */
	@Override
	public void addTag(String name) {
		Tag.addTag(name, id, this.getClass().getName());
	}

	@Override
	public void removeTag(String name) {
		Tag.removeTag(name, id, this.getClass().getName());
	}

	@Override
	public void removeAllTags() {
		Tag.removeAllTags(id, this.getClass().getName());
	}

	@Override
	public List<Tag> getAllTags() {
		return Tag.getAllTags(id, this.getClass().getName());
	}

	public List<Post> findAllByTag(String name) {

		List<Post> posts = Post.find
				.query()
				.setRawSql(Tag.findAllByTagRawSql(name, this.getClass().getName()))
				.where()
				.findList();

		return posts;
	}

	public static ArrayNode range(long start, long end){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			String key = "buzz:all";

			if(!j.exists(key)) {
				Logger.debug("adding all posts to cache: " + key);
				for(Post post: getAll()){
					post.zadd(key);
				}
			}

//			long count = j.zcard(key);
//			if(start+end+1 > count){
//				end = count - start - 1;
//			}
//			Logger.debug("range: " + start + " " + end + " " + count);
			Set<String> set = j.zrevrange(key, start, end);
			items.put("count", j.zcard(key));

			for(String id: set) {
				// get the data for each like
				data.add(Post.hmget(id));
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
			map.put("body", this.body);
			map.put("createdAt", new DateTime(this.createdAt).toString());

			if(this.user != null) {
				map.put("user.id", this.user.id.toString());
			}
			if(this.product != null) {
				map.put("product.id", this.product.id.toString());
			}
			if(this.content != null) {
				map.put("content.id", this.content.id.toString());
			}

			// add the values
			j.hmset("post:" + this.id.toString(), map);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode hmget(String id){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		ObjectNode node = Json.newObject();
		try {
			String key = "post:" + id;

			if(!j.exists(key)) {
				Logger.debug("post added to cache " + key);
				Post.find.byId(Long.parseLong(id, 10)).hmset();
			}

			List<String> values = j.hmget(key, "body", "createdAt", "user.id", "product.id", "content.id");

			node.put("id", id);
			if (values.get(0) != null) node.put("body", values.get(0));
			if (values.get(1) != null) node.put("createdAt", values.get(1));
			if (values.get(2) != null) {
				node.put("user", User.hmget(values.get(2)));
			}
			if (values.get(3) != null) {
				node.put("product", Product.hmget(values.get(3)));
			}
			if (values.get(4) != null) {
				node.put("content", Content.hmget(values.get(4)));
			}
			node.put("likes", Likes.range(id, "models.post"));
			node.put("comments", Comment.range(id, "models.post"));

		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
		return node;
	}
}
