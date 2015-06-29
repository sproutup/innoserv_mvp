package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Campaign;
import models.Product;
import models.User;
import models.UserReferral;
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

import views.html.admin.*;

/**
 * Manage Share Campaign actions
 */
public class CampaignController extends Controller {
    
	  private static final Form<Campaign> campaignForm = Form.form(Campaign.class);
	  private static final Boolean admin_enabled = Boolean.parseBoolean(Play.application().configuration().getString("admin.enabled"));

	
	//
    // web service for sharing campaign
    //
	
	/*
	 * Gets called when user clicks on the SproutUp or Share button
	 */
	@SubjectPresent
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getCampaignInfo(Long productId) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            User user = Application.getLocalUser(ctx().session());
            if (user == null) {
                return badRequest("User not found");
            } else {
            	//get campaign information
            	Map<String, Object> params = new HashMap<String, Object>();
                params.put("product_id", productId);
                params.put("active", "1");
            	Campaign campaign = Campaign.find.where().allEq(params).findUnique();
            	if (campaign==null)
            		return badRequest("Campaign not active or does not exist");
            	
            	ObjectNode rs = campaign.toJson();
        		String referralId = UserReferral.getReferralId(user.id, campaign.id);
	        	
        		//TODO: replace it with URL shortener service
        		String genURL = "http://sproutup.co/product/" + campaign.product.slug + "?refId="+ referralId; 
	        	rs.put("url", genURL);
        		rs.put("referralId", referralId);
	        	rs.put("referrerUserId", user.id);
	        	
                return created(rs);
            }
        }
    }
    
    /*
     * This is post processing when user
     * has shared the campaign
     */
    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result processShare() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            long referrerUserId = json.findPath("referrerUserId").longValue();
            String referralId = json.findPath("referralId").textValue();
            
            long campaignId = json.findPath("campaignId").longValue();
            User user = Application.getLocalUser(ctx().session());
            long signedupUserId = user.id;
            
            boolean requestedDisc = json.findPath("requestedDisc").asBoolean();
            boolean sharedOnSocialMedia = json.findPath("sharedOnSocialMedia").asBoolean();
            UserReferral uref;
            if (referralId != null) {
            	uref = UserReferral.find.where().eq("referral_id", referralId).findUnique();
            	if(uref!=null && uref.user.id.equals(signedupUserId)){
            		//user is same as referrer
            		uref.requestedDisc = requestedDisc;
            		uref.sharedOnSocialMedia = sharedOnSocialMedia;
            		uref.update();
            		return created(uref.toJson());
            	}
            } 
            //user is a referrer friend/social media follower 
            uref = new UserReferral();
            uref.user.id = referrerUserId;
            uref.referralId = referralId;
            uref.campaign.id = campaignId;
            uref.signedupUserId = signedupUserId;
            uref.requestedDisc = requestedDisc;
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
