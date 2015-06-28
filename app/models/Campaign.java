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
 * Created by nitin on 6/23/15.
 */
@Entity
public class Campaign extends TimeStampModel implements PathBindable<Campaign>{

    public static Finder<Long, Campaign> find = new Finder<Long, Campaign>(
            Long.class, Campaign.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public Product product;

    public String campaignName;
    
    public String campaignShortDescription;
    
    @Column(columnDefinition = "TEXT")
	public String campaignLongDescription;
    
    //campaign begin date
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
    
    @Column(columnDefinition = "TEXT")
	public String campaignShareMessage;
    
    @Column(columnDefinition = "TEXT")
	public String campaignPerks1;

    @Column(columnDefinition = "TEXT")
	public String campaignPerks2;

    @Column(columnDefinition = "TEXT")
	public String campaignPerks3;
    
    @Column(columnDefinition = "TEXT")
	public String campaignPerks4;
    
    public boolean active;

    @Column(columnDefinition = "TEXT")
	public String campaignOutcome;

    public int totalNumViewed;
    public int totalNumParticipated;
    
	public static Page<Campaign> find(int page) {
	    return
	            find.where()
	                .orderBy("id desc")
	                .findPagingList(100)
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


	public static List<Campaign> findAll() {
	    return
	    		find.where()
                .orderBy("id desc").findList();
	}
	
	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("campaignName", this.campaignName);
		node.put("campaignShortDescription", this.campaignShortDescription);
		node.put("campaignLongDescription", this.campaignLongDescription);
		node.put("campaignLongDescription", this.campaignLongDescription);
		node.put("beginDate", this.beginDate.toString());
		if (this.endDate!=null)
			node.put("endDate", this.endDate.toString());
		else
			node.put("endDate","");
		
		ArrayNode perksArrayNode = new ArrayNode(JsonNodeFactory.instance);
		if (this.campaignPerks1 != null && this.campaignPerks1.length()>0) {
			perksArrayNode.add(this.campaignPerks1);
		}
		if (this.campaignPerks2 != null && this.campaignPerks2.length()>0) {
			perksArrayNode.add(this.campaignPerks2);
		}
		if (this.campaignPerks3 != null && this.campaignPerks3.length()>0) {
			perksArrayNode.add(this.campaignPerks2);
		}
		if (this.campaignPerks4 != null && this.campaignPerks4.length()>0) {
			perksArrayNode.add(this.campaignPerks4);
		}
		
		node.put("perks", perksArrayNode);
		node.put("campaignOutcome", this.campaignOutcome);
		node.put("totalNumViewed", this.totalNumViewed);
		node.put("totalNumViewed", this.totalNumViewed);
		node.put("active", this.active);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("productId", this.product.id);
		node.put("productName", this.product.productName);
		node.put("productSlug", this.product.slug);
		return node;
	}

	@Override
	public Campaign bind(String key, String value) {
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
