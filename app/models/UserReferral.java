package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import org.joda.time.DateTime;

import play.libs.Json;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by nitin on 6/23/15.
 */
@Entity
public class UserReferral extends TimeStampModel {

    public static Finder<Long, UserReferral> find = new Finder<Long, UserReferral>(
            Long.class, UserReferral.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

	//refId of the current user
    public String referralId;

    public int numVisits;
    
    public boolean requestedDisc = false;
    public boolean sharedOnSocialMedia = false;
    
    public boolean active = true;

    //campaign this referral is related to
    @ManyToOne 
    @Column(nullable=true)
    public Campaign campaign;

	//trial this referral is related to
	@ManyToOne
	@Column(nullable=true)
	public Trial trial;


	@ManyToOne
    public User user;
    
    //refId of the user who referred this user
    public String referrerId;
    
	public static Page<UserReferral> find(int page) {
	    return
	            find.where()
	                .orderBy("id desc")
	                .findPagingList(100)
	                .setFetchAhead(false)
	                .getPage(page);
	}
	
	/**
	 * Generate referralId associated with the current user if asking for the first time, 
	 * otherwise return from db
	 * @param userId
	 * @param campaignId
	 * @return
	 */
	public static String getReferralId(Long userId, Long campaignId, Long trialId){
		
		String refId;
		
		//lookup for the referralId in the db if it exists
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", userId);
		if (campaignId !=null)
	        params.put("campaign_id", campaignId);
		if (trialId !=null)
			params.put("trial_id", trialId);

        params.put("active", "1");
        
        List<UserReferral> refList = find.where().allEq(params).findList();
		UserReferral ref = null;
        if(refList!=null && refList.size()>0){
			ref = refList.get(0);
		}
        
		if (ref == null || ref.referralId == null || ref.referralId.isEmpty()
				|| ref.referralId.equals("-1")) {
			//generate one if it doesn't exist
			refId = String.valueOf((int)(Math.random()*999000)+1000);
			//save an entry in the db
			ref = new UserReferral();
			if (campaignId !=null)
				ref.campaign = Campaign.find.byId(campaignId);
			if (trialId !=null)
				ref.trial = Trial.find.byId(trialId);
			ref.referralId = refId;
			ref.user = User.find.byId(userId);
			ref.save();
			
		} else {
			refId = ref.referralId;
			
		}
		
		return refId;
		
	}
	
	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("referralId", this.referralId);
		if (this.user!=null)
			node.put("referrerUserId", this.user.id);
		if (this.campaign!=null)
			node.put("campaignId", this.campaign.id);
		if (this.trial!=null)
			node.put("trialId", this.trial.id);
		node.put("referrerId", this.referrerId);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		return node;
	}
	
	
    
}
