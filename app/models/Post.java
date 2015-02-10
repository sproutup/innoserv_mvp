package models;

import java.util.Date;
import java.util.List;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Taggable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Post extends Model implements Taggable{

	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	@ManyToOne
	public Product product;


	@Column(columnDefinition = "TEXT")
	@Constraints.Required
	public String postText;
	
	public String title;
	public boolean activeFlag = true;
	
	@Constraints.Required//suggestion, question, compliment
	public String category;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date dateTimeStamp = new Date();
	
	//number of likes this post has fetched
	public int likesCount;

    @OneToMany(mappedBy = "parent")
    public List<Post> comments;

    @ManyToOne
    public Post parent;

    

	
	public Post(User user, Product product, String postText, String title,
			 String category, int likesCount,
			Post parent) {
		super();
		this.user = user;
		this.product = product;
		this.postText = postText;
		this.title = title;
		this.category = category;
		this.likesCount = likesCount;
		this.parent = parent;
	}
	public static Finder<Long, Post> find = new Finder<Long, Post>(
			Long.class, Post.class);
	

	
	/*
	@return postID
	*/
	public Long postFeed() {
		
		//verify		
		
		
		//save post
		Long postID = new Long(-1);
		
		
		//save tag
		
		return postID;
	}
	/*
	 * a user can edit title or a text
	 */
	public Long editPost() {
		Long postID = new Long(-1);
		return postID;
	}

	/*
	 * @return true if successful
	 */
	public boolean deletePost(Long postID) {
		
		return false;
	}
	/*
	* @required categoryName
	* @required productID	 
	*/
	public List<Post> getActivePostsbyCategory(String categoryName, Long productID){
		 
			return (List<Post>) find.where()
				.eq("category", categoryName)
				.eq("product_id", productID)
				.eq("active_flag", "0")
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
	
	public static Post findById(Long id) {
        return find.byId(id);
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
	 * Like/Unlike
	 */
		
	public int likePost(Long postID){
		int likeCount = 0;
		return likeCount;
	}
	
	public int unLikePost(Long postID){
		int likeCount = 0;
		return likeCount;
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