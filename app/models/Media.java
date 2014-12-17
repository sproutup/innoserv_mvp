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
	
	public String mediaFileName;
	
	public String mediaType;

	public int likesCount;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date dateTimeStamp = new Date();
	
	public boolean activeFlag = true;
	
	
	public Media() {
		super();
		
	}

	public Media(Long id, String mediaID, 
			String mediaFileName, String mediaType) {
		super();
		this.id = id;
		this.mediaID = mediaID;
		this.mediaFileName = mediaFileName;
		this.mediaType = mediaType;
	}
	
	
	
	
}
