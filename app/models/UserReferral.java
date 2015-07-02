package models;

import java.util.HashMap;
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

    public String referralId;

    public int numVisits;
    
    public boolean requestedDisc = false;
    public boolean sharedOnSocialMedia = false;
    
    public boolean active = true;

    //campaign this referral is related to
    @ManyToOne 
    @Column(nullable=true)
    public Campaign campaign;

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
	public static String getReferralId(Long userId, Long campaignId){
		
		String refId;
		
		//lookup for the referralId in the db if it exists
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", userId);
        params.put("campaign_id", campaignId);
        params.put("active", "1");
        
        UserReferral ref = find.where().allEq(params).findUnique();
		
		if (ref == null || ref.referralId == null || ref.referralId.equals("")
				|| ref.referralId.equals("-1")) {
			//generate one if it doesn't exist
			refId = String.valueOf((int)(Math.random()*9000)+1000);
			//save an entry in the db
			ref = new UserReferral();
			ref.campaign = Campaign.find.byId(campaignId);
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
		node.put("referrerUserId", this.user.id);
		node.put("campaignId", this.campaign.id);
		node.put("referrerId", this.referrerId);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		return node;
	}
	
	
    
}
