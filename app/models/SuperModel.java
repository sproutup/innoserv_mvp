package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import com.typesafe.plugin.RedisPlugin;

import play.Logger;
import play.db.ebean.Model;
import utils.*;

@MappedSuperclass
public class SuperModel extends TimeStampModel implements Likeable, Taggable, Followeable, Fileable, Commentable {

  /**
	 * For social media features
	 */
	private static final long serialVersionUID = 1L;

	@Id
  @GeneratedValue
  public Long id;


  /*
  Likeable interface implementation
   */
  @Override
  public void addLike(Long userId) {
    Likes.addLike(userId, this.id, this.getClass().getName().toLowerCase());
  }

  @Override
  public void removeLike(Long userId) {
    Likes.removeLike(userId, this.id, this.getClass().getName().toLowerCase());
  }

  @Override
  public void removeAllLikes() {
    Likes.removeAllLikes(this.id, this.getClass().getName().toLowerCase());
  }

  @Override
  public List<Likes> getAllLikes() {
    return Likes.getAllLikes(this.id, this.getClass().getName().toLowerCase());
  }

  /*
  Buzzable interface implementation
   */
/*
  @Override
  public void addBuzz() {
    Likes.addLike(1L, this.id, this.getClass().getName().toLowerCase());
  }
*/
  public void zadd(String key){
      Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
      try {
          Logger.debug("added to set: " + key);
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
/*
  public static ObjectNode range(String key, long start, long end){
    ObjectNode items = Json.newObject();
    ArrayNode data = items.putArray("data");

    //Go to Redis to read the full roster of content.
    Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
    try {
      if(!j.exists(key)) {
        Logger.debug("adding item to cache: " + key);
        for(Product item: Product.getAllActive()){
          item.zadd(key);
        }
      }

      Set<String> set = j.zrange(key, start, end);
      items.put("count", j.zcard(key));

      for(String id: set) {
        // get the data for each like
        data.add(hmget(id));
      }
    } finally {
      play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
    }

    return items;
  }
*/
  /*
    Commentable interface implementation
  */
  @Override
  public void addComment(Long userId, String body) {
      Comment.addComment(userId, body, this.id, this.getClass().getName());
  }

  @Override
  public void removeComment(Long commentId, Long userId) {
      Comment.deleteComment(commentId, this.id, this.getClass().getName(), userId);
  }

  @Override
  public void removeAllComments() {
    Comment.removeAllComments(this.id, this.getClass().getName());
  }

  @Override
  public List<Comment> getAllComments() {
    return Comment.getAllComments(this.id, this.getClass().getName());
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

  /*
  Followeable interface implementation
   */
  @Override
  public void follow(Long userId) {
	  Follow.addFollow(userId, this.id, this.getClass().getName());
  }
	
	@Override
	public void unFollow(Long userId) {
		Follow.removeFollow(userId, this.id, this.getClass().getName());
	}
	
	@Override
	public boolean isFollowing(Long userId) {
		return Follow.isFollowing(userId, this.id, this.getClass().getName());
	}
	
	@Override
	public List<Follow> getAllFollowers() {
		return Follow.getAllFollowers(this.id, this.getClass().getName());
	}
	
	@Override
	public List<Follow> getUserFollowings(Long userId) {
		return Follow.getAllFollowings(userId);
	}

	@Override
	public void removeAllFollowers() {
		Follow.removeAllFollowers(this.id, this.getClass().getName());
		
	}

    /*
    Fileable interface implementation
    */

    @Override
    public void addFile(Long id) {
        //todo
    }

    @Override
    public void removeFile(Long id) {
        //todo
    }

    @Override
    public void removeAllFiles() {
        //todo
    }

    @Override
    public List<File> getAllFiles() {
        return File.getAllFiles(id, this.getClass().getName());
    }
}
