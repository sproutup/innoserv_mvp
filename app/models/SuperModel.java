package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import play.Logger;
import play.db.ebean.Model;
import utils.Followeable;
import utils.Likeable;
import utils.Taggable;

@MappedSuperclass
public class SuperModel extends Model implements Likeable, Taggable, Followeable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Id
  @GeneratedValue
  public Long id;

  @Column(name = "created_at")
  public Date createdAt;

  @Column(name = "updated_at")
  public Date updatedAt;

  @Override
  public void save() {
    createdAt();
    super.save();
  }

  @Override
  public void update() {
    updatedAt();
    super.update();
  }

  @PrePersist
  void createdAt() {
    this.createdAt = this.updatedAt = new Date();
  }

  @PreUpdate
  void updatedAt() {
    this.updatedAt = new Date();
  }


  /*
  Likeable interface implementation
   */
  @Override
  public void addLike(Long userId) {
    Likes.addLike(userId, this.id, this.getClass().getName());
  }

  @Override
  public void removeLike(Long userId) {
    Likes.removeLike(userId, this.id, this.getClass().getName());
  }

  @Override
  public void removeAllLikes() {
    Likes.removeAllLikes(this.id, this.getClass().getName());
  }

  @Override
  public List<Likes> getAllLikes() {
    Logger.debug("getAllLikes:" + this.getClass().getName());
    return Likes.getAllLikes(this.id, this.getClass().getName());
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
  Taggable interface implementation
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
	

}
