package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.libs.Json;
import javax.persistence.*;
import java.util.List;
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
    public String content;

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

    public static Post findById(Long id) {
        return find.byId(id);
    }

    private ObjectNode toJsonRaw(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("title", this.title);
        node.put("content", this.content);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
		List<Likes> likes = this.getAllLikes();
		if(likes.size()>0){
			node.put("likes", Likes.toJson(likes));
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

    public List<Post> getAll() {
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

}
