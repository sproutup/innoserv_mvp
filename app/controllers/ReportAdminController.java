package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.google.common.io.Files;

import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.With;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.ProductSuggestion;
import models.ProductTrial;
import models.SecurityRole;
import models.User;

import com.avaje.ebean.*;

import constants.AppConstants;
import static play.mvc.Http.MultipartFormData;
import views.html.admin.*;

public class ReportAdminController extends Controller {

  private static final Form<User> userForm = Form.form(User.class);
  private static final Boolean admin_enabled = Boolean.parseBoolean(Play.application().configuration().getString("admin.enabled"));

  public static Result index() {
    return redirect(routes.ReportAdminController.userList(0));
  }

  public static Result home() {
	    return ok(admin.render());
  }
  
  public static Result userList(Integer page) {
    if(!admin_enabled){return notFound();};

    Logger.debug("mode: " + play.api.Play.current().mode());
    Page<User> users = User.find(page);//findAll();
    return ok(userlist.render(userForm, users));
  }

  public static Result trialList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<ProductTrial> trials = ProductTrial.find(page);//findAll();
	    return ok(trial_list.render(trials));
  }  
  
  public static Result suggestedProductList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<ProductSuggestion> suggestions = ProductSuggestion.find(page);//findAll();
	    return ok(suggested_product_list.render(suggestions));
}  

//  public static Result details(User user) {
//    if(!admin_enabled){return notFound();};
//
//    Form<User> filledForm = userForm.fill(user);
//    return ok(detail_about.render(filledForm));
//  }

  public static Result updateUserRole(){
      if(!admin_enabled){return notFound();};

      // get hidden objects from submitted form
      Map<String, String[]> map = request().body().asFormUrlEncoded();
      String userId = map.get("userId")[0];
      String roleType = map.get("roleType")[0];
   
      User user = User.find.byId(Long.valueOf(userId));
      if (user!=null){
    	  if(roleType.equals(AppConstants.INFLUENCER)) {
    		  if(user.isInfluencer()){//turned from true to false remove role
    			  if (user.roles!=null && user.roles.size()>0) {
    				  for (Iterator<SecurityRole> iter = user.roles.listIterator(); iter.hasNext(); ) {
    					    SecurityRole sr = iter.next();
    					    if (sr.roleName.equals(AppConstants.INFLUENCER)) {
    					        iter.remove();
    					    }
    					}
    			  }
    		  } else { //turned from false to true we need to add role
    			  SecurityRole sr = SecurityRole.findByRoleName(AppConstants.INFLUENCER);
    			  if (user.roles==null || user.roles.size()==0) {
    				  user.roles = new ArrayList<SecurityRole>();
    			  }  
    			  user.roles.add(sr);
    		  }
    		  
    	  } else if (roleType.equals(AppConstants.CREATOR)){
    		  if(user.isCreator()){//turned from true to false remove role
    			  if (user.roles!=null && user.roles.size()>0) {
    				  for (Iterator<SecurityRole> iter = user.roles.listIterator(); iter.hasNext(); ) {
    					    SecurityRole sr = iter.next();
    					    if (sr.roleName.equals(AppConstants.CREATOR)) {
    					        iter.remove();
    					    }
    					}
    			  }
    		  } else { //turned from false to true we need to add role
    			  SecurityRole sr = SecurityRole.findByRoleName(AppConstants.CREATOR);
    			  if (user.roles==null || user.roles.size()==0) {
    				  user.roles = new ArrayList<SecurityRole>();
    			  }  
    			  user.roles.add(sr);
    		  }
    	  }
    		  
      }
      
      //update user
      user.update();
      
     return redirect(routes.ReportAdminController.userList(0));

  
  }

}

