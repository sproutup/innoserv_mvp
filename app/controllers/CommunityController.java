package controllers;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Community;
import models.File;
import models.Product;
import models.User;
import play.libs.Json;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import views.html.productadministration.*;

/**
 * Community
 */
public class CommunityController extends Controller {
    
	private static final Form<Community> communityForm = Form.form(Community.class);
	private static final Boolean admin_enabled = Boolean.parseBoolean(Play.application().configuration().getString("admin.enabled"));

	/*
	 * Gets called when homepage is loaded
	 */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getCommunities(Long productId) {//get contest information
    	List<Community> commList = Community.getAllActive();
		ObjectNode rs = null;
    	//TODO
    	return created(rs);
    }
    
    	
	/**
	 * for admin page
	 */
    public static Result list() {
        if(!admin_enabled){return notFound();};

        Logger.debug("mode: " + play.api.Play.current().mode());
        List<Community> community = Community.getAll();
        return ok(community_list.render(community));
    }  
    
    public static Result newCommunity() {
		  if(!admin_enabled){return notFound();};

		  Community comm = new Community();
		  comm.activeFlag=true;
		  
		  return ok(community_management.render(communityForm.fill(comm)));
	  }
    
    public static Result details(Long id) {
        if(!admin_enabled){return notFound();};
        
        return ok(community_management.render(communityForm.fill(Community.find.byId(id))));
      }
    
    
    public static Result save() {
        if(!admin_enabled){return notFound();};

        Form<Community> commF = communityForm.bindFromRequest();
        Community community = commF.get();
        
		MultipartFormData body = request().body().asMultipartFormData();
		Map<String, String[]> mp = body.asFormUrlEncoded();
		MultipartFormData.FilePart uploadFilePart = body.getFile("logoImage");

        if (community.id == null) {
        	community.save();
        } 
        
        //upload image
		if (uploadFilePart != null) {
			community.logoImage = new File();
			community.logoImage.mediaUploadedfile = uploadFilePart.getFile();
			community.logoImage.originalName = uploadFilePart.getFilename();
			
	        community.logoImage.refId = community.id;
			community.logoImage.refType = "models.Community";
			community.logoImage.companyMediaUpload();
			
		} 

		//update community
    	community.update();
        
        
       return redirect(routes.CommunityController.list());
    }
    
}
