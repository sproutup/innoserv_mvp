package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import org.joda.time.DateTime;

import play.libs.Json;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by nitin on 9/17/15.
 */
@Entity
public class Contest extends TimeStampModel implements PathBindable<Contest>{

    public static Finder<Long, Contest> find = new Finder<Long, Contest>(
            Long.class, Contest.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public Product product;

    public String contestName;
    
    /**
     * Get displayed
     */
    public String contestTitle;
    
    /**
     * Get displayed
     */
    public String contestButtonTitle;
    
    /**
     * Get displayed
     */
    @Column(columnDefinition = "TEXT")
	public String contestDescription;
    
    //contest begin date
    @Column(name = "begin_date")
	public Date beginDate;

    //campaign end date
	@Column(name = "end_date")
	public Date endDate;

	@PrePersist
	void beginDate() {
		if (this.beginDate==null){
			this.beginDate = new Date();
		}
	} 
    /**
     * Get displayed
     */
    @Column(columnDefinition = "TEXT")
	public String socialMediaShareMessage;
    
    /**
     * Get displayed
     */
    @Column(columnDefinition = "TEXT")
	public String contestPerk;

    /**
     * Get displayed to user after the contest is being participated
     */
    @Column(columnDefinition = "TEXT")
	public String contestConfirmation;
    
    public boolean active;
    
    /**
     * End of the contest, 
     * this is where admin can capture the outcome
     */
    @Column(columnDefinition = "TEXT")
	public String contestOutcome;

    public int totalNumParticipated;
    public int minimumNumRequired;
    
	public static Page<Contest> find(int page) {
	    return
	            find.where()
	                .orderBy("id desc")
	                .findPagingList(1000)
	                .setFetchAhead(false)
	                .getPage(page);
	}
	
	
	public String getBeginDate() {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
		//DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		if (beginDate!=null)
			return formatter.format(this.beginDate);
		else 
			return null;
	}
	
	public String getEndDate() {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
		//DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		if (endDate!=null)
			return formatter.format(this.endDate);
		else 
			return null;
	}
	public void setBeginDate(String beginDate) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		DateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
	    try {
	    	if (beginDate!=null && beginDate.length()>0) {
		    	this.beginDate = formatter.parse(beginDate);
	    	}	
		} catch (ParseException e) {
			try {
				if (beginDate!=null && beginDate.length()>0) {
		    		this.beginDate = formatter2.parse(beginDate);
		    	}
			} catch (ParseException e2) {
			// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}
	public void setEndDate(String endDate) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		DateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
	    try {
	    	if (endDate!=null && endDate.length()>0) {
	    		this.endDate = formatter.parse(endDate);
	    	}	
		} catch (ParseException e) {
			try {
				if (endDate!=null && endDate.length()>0) {
		    		this.endDate = formatter2.parse(endDate);
		    	}
			} catch (ParseException e2) {
			// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}	
	}


	public static List<Contest> findAll() {
	    return
	    		find.where()
                .orderBy("id desc").findList();
	}
	
	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("contestName", this.contestName);
		node.put("contestId", this.id);
		node.put("contestTitle", this.contestTitle);
		node.put("contestButtonTitle", this.contestButtonTitle);
		node.put("contestDescription", this.contestDescription);
		node.put("contestSocialMediaShareMessage", this.socialMediaShareMessage);
		node.put("beginDate", this.beginDate.toString());
		if (this.endDate!=null)
			node.put("endDate", this.endDate.toString());
		else
			node.put("endDate","");
	
		node.put("contestPerk", contestPerk);
		node.put("contestConfirmation", this.contestConfirmation);
		node.put("contestOutcome", this.contestOutcome);
		node.put("totalNumParticipated", this.totalNumParticipated);
		node.put("minimumNumRequired", this.minimumNumRequired);
		node.put("active", this.active);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("productId", this.product.id);
		node.put("productName", this.product.productName);
		node.put("productSlug", this.product.slug);
		
		if (this.product.productAdditionalDetail!=null && this.product.productAdditionalDetail.bannerPhoto!=null){
		  node.put("productPictureURL", this.product.productAdditionalDetail.bannerPhoto.getURL());
		}
		return node;
	}

	@Override
	public Contest bind(String key, String value) {
		// TODO Auto-generated method stub
		return find.byId(Long.getLong(value));
	}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

    
}
