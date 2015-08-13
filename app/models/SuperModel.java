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
    return Likes.getAllLikes(this.id, this.getClass().getName());
  }


  /*
    Commentable interface implementation
  */
  @Override
  public void addComment(Long userId, String body) {
      Comment.addComment(userId, body, this.id, this.getClass().getName());
  }

  @Override
  public void removeComment(Long commentId) {
      Comment.removeComment(commentId, this.id, this.getClass().getName());
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
