package models;

import java.net.URL;
import java.util.Date;
import java.util.List;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class Media extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@Constraints.Required
	public String mediaID;
	
	@ManyToOne
	public Product product;
	
	@ManyToOne
	public User user;
	
	@ManyToMany
	public List<Feedback> feedbackList;
	
	//@Constraints.Required
	public URL cloudFrontLink;
	public String submittedByUserID;//social media user id
	public String productName;//this is added in case we loose referential integrity to the product table for any damn reason.
	public String mediaFileName;
	
	public String mediaType;

	public int likesCount;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date dateTimeStamp = new Date();
	
	public boolean activeFlag = true;
	
	
	public Media() {
		super();
		
	}


	public Media(String mediaID, Product product, User user,String submittedByUserID,String productName,
			List<Feedback> feedbackList, URL cloudFrontLink,
			String mediaFileName, String mediaType, int likesCount,
			boolean activeFlag) {
		super();
		this.mediaID = mediaID;
		this.product = product;
		this.user = user;
		this.submittedByUserID = submittedByUserID;
		this.productName = productName;
		this.feedbackList = feedbackList;
		this.cloudFrontLink = cloudFrontLink;
		this.mediaFileName = mediaFileName;
		this.mediaType = mediaType;
		this.likesCount = likesCount;
		this.activeFlag = activeFlag;
	}

	
	
	
	
	
}
