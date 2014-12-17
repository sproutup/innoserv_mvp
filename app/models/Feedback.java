package models;

import java.util.Date;
import java.util.List;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Feedback extends Model {

	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	@ManyToOne
	public Product product;

	/*
	 * media submitted related to the feedback;
	 * remember multiple media can be submitted on a feedback
	 */
	@ManyToMany(mappedBy="feedbackList")
	public List<Media> mediaList;
	
	public String textComments;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date dateTimeStamp = new Date();
	
	//number of likes this feedback has fetched
	public int likesCount;

	public static Finder<Long, Feedback> find = new Finder<Long, Feedback>(
			Long.class, Feedback.class);

}