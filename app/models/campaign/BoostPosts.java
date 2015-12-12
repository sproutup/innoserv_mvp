package models.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BoostPosts extends SuperCampaign{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
	
    /*
     * code= "1"-per week; "2"-every 2 weeks; "3"-per month; "4"-active period; 
     */
    //public int[] frequency={1,2,3,4}; 
    
    /*
     * Max number of participants on this campaign; 
     * Unlimited if left Null
     */
    public Long maxParticipation; 
    
    /**
     * Currently allow up to 4 posts to be boosted. 
     * Get configured by the creator/admin 
     */
    @Column(columnDefinition = "TEXT")
	public String socialMediaPost1;

    @Column(columnDefinition = "TEXT")
	public String socialMediaPost2;

    @Column(columnDefinition = "TEXT")
	public String socialMediaPost3;
    
    @Column(columnDefinition = "TEXT")
	public String socialMediaPost4;

    
    /**
     * Get displayed to user after the contest is being participated
     */
    @Column(columnDefinition = "TEXT")
	public String confirmationMessage;
    

}
