package models.campaign;

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
 * Created by nitin on 12/3/15.
 */
@Entity
public class SocialMediaContest extends SuperCampaign implements PathBindable<SocialMediaContest>{

    public static Finder<Long, SocialMediaContest> find = new Finder<Long, SocialMediaContest>(
            Long.class, SocialMediaContest.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    /**
     * Get displayed
     */
    public String buttonTitle;

    public int minimumNumRequired;
    
    /**
     * Get displayed
     */
    @Column(columnDefinition = "TEXT")
	public String socialMediaShareMessage;
    
    /**
     * Get displayed to user after the contest is being participated
     */
    @Column(columnDefinition = "TEXT")
	public String confirmationMessage;
    
	public static Page<SocialMediaContest> find(int page) {
	    return
	            find.where()
	                .orderBy("id desc")
	                .findPagingList(1000)
	                .setFetchAhead(false)
	                .getPage(page);
	}
	
	
	public ObjectNode toJson(){
		ObjectNode node = super.toJson();
		node.put("contestId", this.id);
		node.put("buttonTitle", this.buttonTitle);
		node.put("socialMediaShareMessage", this.socialMediaShareMessage);
		node.put("confirmationMessage", this.confirmationMessage);
		node.put("minimumNumRequired", this.minimumNumRequired);
		return node;
	}

	@Override
	public SocialMediaContest bind(String key, String value) {
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
