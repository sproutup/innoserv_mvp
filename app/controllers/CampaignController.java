package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Campaign;
import models.Contest;
import models.Product;
import models.User;
import models.UserReferral;
import play.libs.Json;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import service.GoogleURLShortener;
import views.html.admin.*;

/**
 * Manage Share Campaign actions
 */
public class CampaignController extends Controller {
    
	private static final Form<Campaign> campaignForm = Form.form(Campaign.class);
	private static final Boolean admin_enabled = Boolean.parseBoolean(Play.application().configuration().getString("admin.enabled"));

//	
//	web service for sharing campaign
//	

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getActiveCampaign(Long productId) {//get campaign information
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("product_id", productId);
        params.put("active", "1");
        //TODO nj: need to handle when there are more than one active campaign? get the latest "order by id desc"? check if findUnique would fail..
        Campaign campaign = Campaign.find.where().allEq(params).findUnique();
		ObjectNode rs;
    	if (campaign==null) {
    		rs = Json.newObject();
    		rs.put("active", false);
    	} else {
    		rs = campaign.toJson();
    	}
    	return created(rs);
    }
    
    /*
	 * Gets called when user clicks on the SproutUp or Share button
	 */
	@BodyParser.Of(BodyParser.Json.class)
    public static Result getCampaignURL(String productSlug) {
        	
        	ObjectNode rs = Json.newObject();
    		String genURL = "http://sproutup.co/product/" + productSlug + "?refId=discount";
            String shortURL= GoogleURLShortener.shortenURL(genURL);
        	rs.put("url", shortURL);
            return created(rs);
    }
    
	/*
	 * Gets called when user clicks on the SproutUp or Share button
	 * Not using it anymore because user signup is not required 
	 */
	@SubjectPresent
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getCampaignInfo(Long productId) {
        User user = Application.getLocalUser(ctx().session());
        if (user == null) {
            return badRequest("User not found");
        } else {
        	//get campaign information
        	Map<String, Object> params = new HashMap<String, Object>();
            params.put("product_id", productId);
            params.put("active", "1");
            
            //TODO nj: need to handle when there are more than one active campaign? get the latest "order by id desc"? 
            //check if findUnique would fail..
        	Campaign campaign = Campaign.find.where().allEq(params).findUnique();
        	if (campaign==null)
        		return badRequest("Campaign not active or does not exist");
        	
        	ObjectNode rs = campaign.toJson();
    		String referralId = UserReferral.getReferralId(user.id, campaign.id, null);
        	
    		String genURL = "http://sproutup.co/product/" + campaign.product.slug + "?refId="+ referralId;
            String shortURL= GoogleURLShortener.shortenURL(genURL);
        	rs.put("url", shortURL);
    		rs.put("referralId", referralId);
        	rs.put("userId", user.id);
        	
            return created(rs);
        }
    }
    
    /*
     * This is post processing when user
     * has shared the campaign
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result processShare() {
        JsonNode json = request().body().asJson();
        long campaignId = json.findPath("campaignId").longValue();

        //update the counter
        Campaign camp = Campaign.find.byId(campaignId);

        camp.totalNumParticipated++;
        camp.update();
        return created(camp.toJson());
     }
	
    /*
     * This is post processing when user
     * has shared the campaign
     * Not using it anymore
     */
    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result processShare2() {
        JsonNode json = request().body().asJson();
        User user = Application.getLocalUser(ctx().session());
        if (user == null)
            return badRequest("User not found");
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            long userId = json.findPath("userId").longValue();
            String referralId = json.findPath("referralId").textValue();
            String referrerId = json.findPath("referrerId").textValue();
            
            //System.out.println("referrerId: " + referrerId);
            
            long campaignId = json.findPath("campaignId").longValue();

            boolean requestedDisc = json.findPath("requestedDisc").asBoolean();
            boolean sharedOnSocialMedia = json.findPath("sharedOnSocialMedia").asBoolean();
            UserReferral uref;
//            if (referralId != null) {
//            	uref = UserReferral.find.where().eq("referral_id", referralId).findUnique();
//            	if(uref!=null && uref.user.id.equals(user.id)){//user coming 2nd time
//            		//user is same as referrer
//            		uref.requestedDisc = requestedDisc;
//            		uref.sharedOnSocialMedia = sharedOnSocialMedia;
//            		uref.update();
//            		return created(uref.toJson());
//            	}
//            } 
            //update if the record exist otherwise create new
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("campaign_id", campaignId);
            params.put("user_id", userId);

            if (referrerId!=null && referrerId.length()>0)
                params.put("referrer_id", referrerId);

            uref = UserReferral.find.where().allEq(params).findUnique();

            if (uref==null){
                //user is coming for the first time
                uref = new UserReferral();
            }

            uref.numVisits++;
            uref.user = user;
            if (referralId!=null && referralId.length()>0)
                uref.referralId = referralId;
            uref.campaign = Campaign.find.byId(campaignId);
            if (referrerId!=null && referrerId.length()>0)
                uref.referrerId = referrerId;
            if (requestedDisc)//only set if marked true as we don't want to override with false if it was previously marked true in db
                uref.requestedDisc = requestedDisc;
            if (sharedOnSocialMedia)//only set if marked true
                uref.sharedOnSocialMedia = sharedOnSocialMedia;
            uref.save();
            	
            return created(uref.toJson());
            }
        }
  
    	
    	/*
    	 * for admin page
    	 */
    public static Result list() {
        if(!admin_enabled){return notFound();};

        Logger.debug("mode: " + play.api.Play.current().mode());
        List<Campaign> camps = Campaign.findAll();
        return ok(campaign_list.render(camps));
      }  
    
    public static Result newCampaign() {
		  if(!admin_enabled){return notFound();};

		  List<Product> products = new Product().getAllActive();
		  Campaign camp = new Campaign();
		  camp.active=true;
		  camp.beginDate = Calendar.getInstance().getTime(); //Get the current date

		  return ok(campaign_management.render(campaignForm.fill(camp), products));
	  }
    
    public static Result details(Long id) {
        if(!admin_enabled){return notFound();};
        List<Product> products = new Product().getAllActive();
        
        return ok(campaign_management.render(campaignForm.fill(Campaign.find.byId(id)), products));
      }
    
    
    public static Result save() {
        if(!admin_enabled){return notFound();};

        Form<Campaign> campF = campaignForm.bindFromRequest();
        Campaign campaign = campF.get();

        String[] postAction = request().body().asFormUrlEncoded().get("action");
        if (postAction == null || postAction.length == 0) {
          return badRequest("You must provide a valid action");
        } else {
          String action = postAction[0];
          System.out.println("Action is: " + action);
          if ("Copy".equals(action)) {
        	  campaign.id=null;
        	  campaign.product.id=null;
           } else {//if not copying update the flag;
        	   Product prod = Product.findbyID(campaign.product.id);
	           prod.discountFlag = (campaign.active && campaign.offerDiscount);
	           prod.update();
             
           }
        }  
        if (campaign.id == null) {
          campaign.save();
        } else {
        	//update campaign
        	campaign.update();
        }
        
       return redirect(routes.CampaignController.list());
    }
    
}
