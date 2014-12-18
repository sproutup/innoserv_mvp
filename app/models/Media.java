package models;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.libs.F;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;

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
	
	public static Finder<Long, Media> find = new Finder<Long, Media>(Long.class, Media.class);

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

	public Media findMediabyID(Long id){
		return Media.find.byId(id);
	}
	
	public List<Media> findMediabyProductID(Long productID){
		List<Media> results = null;
		 results = find.where().eq("id", productID).findList();
		 if(results!=null){
			 Logger.debug("# of media fetched for product- "+ productID + "_#" + productName + "- are " + results.size());
		 } else { 
			 Logger.debug("no media found for product- " + productName );
		 }
		 return results;

	}

}
