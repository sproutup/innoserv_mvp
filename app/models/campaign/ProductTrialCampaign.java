package models.campaign;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.Product;
import play.db.ebean.Model.Finder;
import play.libs.F.Option;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;
import utils.Taggable;

@Entity
public class ProductTrialCampaign extends SuperCampaign implements PathBindable<ProductTrialCampaign>, QueryStringBindable<ProductTrialCampaign>, Taggable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
	
    /*
     * Number of trials to be fulfilled in a given frequency period
     */
    public int spots; 
    
    /*
     * as per the codes campaign.Frequency
     */
    public int frequency; 
    
    /*
     * Total units available for trial
     */
    public int totalUnits; 
   
    /*
     * Total units to be given away during trials
     */
    public int unitsGiveaway;
    
    /*
     * Duration for each trial: 15 days or 21 days etc.
     * Default is 15 days;
     */
	@Column(name = "duration_days")
	public int	durationNbrDays = 15;

	/*
	 * Whether campaign will accept open bids from Influencers
	 * default is true
	 */
	public boolean acceptBids = true;
	
	public boolean trialFullHouseFlag;
	
	/*
	 * Limit on number of requests to be received
	 * mark trialFullHouseFlag True when number of requests reach the limit.
	 */
	public int maxRequests;

	/*
	 * Description or type of Influencers to target
	 */
    @Column(columnDefinition = "TEXT")
	public String targetInfluencers; 

    /*
     * Total target outreach for this campaign
     */
    public Long outreachGoal;
    
    /*
     * Minimum average outreach required per influencer
     * This could vary based on the subscription tier
     */
    public Long averageInfluencerOutreach;

    
    public static Finder<Long, ProductTrialCampaign> find = new Finder<Long, ProductTrialCampaign>(
			Long.class, ProductTrialCampaign.class);

	public ProductTrialCampaign() {
		//initialize();
	}

	public List<ProductTrialCampaign> getAll() {
		return find.all();
	}

	public static List<ProductTrialCampaign> getAllActive() {
		return find.where().eq("active", "1").findList();
	}

	public static ProductTrialCampaign findbyID(Long id) {
		return find.byId(id);
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
