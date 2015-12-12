package models.campaign;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import models.Product;
import models.SuperModel;


import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by nitin on 12/2/15.
 */
@MappedSuperclass
public class SuperCampaign extends SuperModel{
	private static final long serialVersionUID = 1L;

    @ManyToOne
    public Product product;

    public String title;
    
    public String shortDescription;
    
    @Column(columnDefinition = "TEXT")
	public String objectiveStatement;
    
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
    
    public boolean active;
    
    //outcome
    public int totalNumParticipated;
    //public int totalOutreachAchieved;
	
    public Perks perks;
    
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


	
	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("title", this.title);
		node.put("shortDescription", this.shortDescription);
		node.put("longDescription", this.longDescription);
		node.put("beginDate", this.beginDate.toString());
		if (this.endDate!=null)
			node.put("endDate", this.endDate.toString());
		else
			node.put("endDate","");
		
//		ArrayNode perksArrayNode = new ArrayNode(JsonNodeFactory.instance);
//		if (this.campaignPerks1 != null && this.campaignPerks1.length()>0) {
//			perksArrayNode.add(this.campaignPerks1);
//		}
//		if (this.campaignPerks2 != null && this.campaignPerks2.length()>0) {
//			perksArrayNode.add(this.campaignPerks2);
//		}
//		if (this.campaignPerks3 != null && this.campaignPerks3.length()>0) {
//			perksArrayNode.add(this.campaignPerks2);
//		}
//		if (this.campaignPerks4 != null && this.campaignPerks4.length()>0) {
//			perksArrayNode.add(this.campaignPerks4);
//		}
		
//		node.put("perks", perksArrayNode);
		node.put("totalNumParticipated", this.totalNumParticipated);
		node.put("active", this.active);
		node.put("productId", this.product.id);
		node.put("productName", this.product.productName);
		node.put("productSlug", this.product.slug);
		
		if (this.product.productAdditionalDetail!=null && this.product.productAdditionalDetail.bannerPhoto!=null){
		  node.put("productPictureURL", this.product.productAdditionalDetail.bannerPhoto.getURL());
		}
		return node;
	}

	
	

    
}
