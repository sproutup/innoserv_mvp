package controllers;

import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.With;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Content;
import models.EarlyAccessRequest;
import models.InfluencerScore;
import models.ProductSuggestion;
import models.ProductTrial;
import models.Trial;
import models.SecurityRole;
import models.User;
import models.UserReferral;

import com.avaje.ebean.*;

import constants.AppConstants;
import static play.mvc.Http.MultipartFormData;

import plugins.KloutPlugin;
import views.html.admin.*;

public class ReportAdminController extends Controller {

  private static final Form<User> userForm = Form.form(User.class);
  private static final Form<Trial> trialForm = Form.form(Trial.class);

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

  public static Result influencerList() {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    List<User> users = User.findInfluencers();//findAll();
	    return ok(influencer_list.render(users));
	  }
  
/**
 * @deprecated
 */
  public static Result trialList(Integer page) {
    if(!admin_enabled){return notFound();};

    Logger.debug("mode: " + play.api.Play.current().mode());
    Page<ProductTrial> trials = ProductTrial.find(page);//findAll();
    return ok(trial_list.render(trials));
  }
  
  public static Result allTrialList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    
	    Page<Trial> trials = Trial.find(page);//findAll();
	    //return ok(influencer_trial_list.render(trials));
	    return ok(all_trial_list.render(trialForm, trials));
	  }


  public static Result influencerTrialList(Integer page) {
    if(!admin_enabled){return notFound();};

    Logger.debug("mode: " + play.api.Play.current().mode());
    
    Page<Trial> trials = Trial.findTrialsbyInfluencers(page);
    
    return ok(influencer_trial_list.render(trialForm, trials));
  }
  
  public static Result influencerReferralList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    
	    Page<UserReferral> trials = UserReferral.findTrialsbyInfluencers(page);
	    
	    return ok(influencer_referral_list.render(trials));
	  }
  
  /**
   * Update Trial status for a specific trial request
   * @return
   */
  public static Result updateTrialStatus(){
	    if(!admin_enabled){return notFound();};

	    // get hidden objects from submitted form
	    Map<String, String[]> map = request().body().asFormUrlEncoded();
	    String requestId = map.get("requestId")[0];
	    String status = map.get("statusVal")[0];
	    String page = map.get("page")[0];
	    String pageNum = map.get("pageNum")[0];
	    if (pageNum==null)
	    	pageNum = "0";

	    Trial trial = Trial.find.byId(Long.valueOf(requestId));
	    if (trial!=null && status!=null && !status.equals("")){
	         trial.status = Integer.valueOf(status);
	 	    //update user
	 	    trial.update();
	    }
	    
	    if (page!=null && page.equals("influencer")){
	    	return redirect(routes.ReportAdminController.influencerTrialList(0));
	    } else if (page!=null && page.equals("producttrialinfluencer")) {
	    	return redirect(routes.ProductAdministrationController.listInfluencerTrials(Integer.valueOf(pageNum)));
	    } else if (page!=null && page.equals("producttrialAll")) {
	    	return redirect(routes.ProductAdministrationController.listAllUserTrials(Integer.valueOf(pageNum)));
	    }
	    else {
	    	return redirect(routes.ReportAdminController.allTrialList(0));
	    }

  }
  
  /**
   * Get the content published by user on Trial
   * @return
   */
  public static Result contentList(Integer page) {
	  if(!admin_enabled){return notFound();};
	  
	  	Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<Content> urls = Content.find(page);//findAll();
	    return ok(content_list.render(urls));
  }

  
  /**
   * Update Content OG
   * @return
   */
  public static Result updateContentOG(){
	    if(!admin_enabled){return notFound();};
	    // get hidden objects from submitted form
	    Map<String, String[]> map = request().body().asFormUrlEncoded();
	    String contentId = map.get("contentId")[0];
	    String page = map.get("page")[0];

	    Content con = Content.find.byId(Long.valueOf(contentId));
	    if (con!=null){
	        //update content
	        con.update();
	    }
	    
	    if (page!=null){
	    	return redirect(routes.ReportAdminController.contentList(Integer.valueOf(page)));
	    } else {
	    	return redirect(routes.ReportAdminController.contentList(0));
	    }
  }
  
  /**
   * @deprecated
   * @param page
   * @return
   */
  public static Result suggestedProductList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<ProductSuggestion> suggestions = ProductSuggestion.find(page);//findAll();
	    return ok(suggested_product_list.render(suggestions));
  }
  
  public static Result earlyAccessRequestList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<EarlyAccessRequest> suggestions = EarlyAccessRequest.find(page);//findAll();
	    return ok(early_access_list.render(suggestions));
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

    /**
     * Adds Klout score to user given by userId. Adds to Influencer Score table if not currently present.
     *
     * @param userId id of user for whom Klout score must be gotten/updated
     * @return redirect result leading back to first page of user list
     */
    public static Result updateKloutScore(Long userId) {
        User influencer = User.find.byId(userId);
        InfluencerScore influence = InfluencerScore.find.where().eq("user_id", userId).findUnique();
        Double score = KloutPlugin.getKloutScoreFromUser(influencer);
        if (score != null) {
            if (influence == null) {
                influence = new InfluencerScore();
                influence.user = influencer;
                influence.kloutScore = score;
                influence.save();
            }
            else {
                influence.kloutScore = score;
                influence.update();
            }
        }
        return redirect(routes.ReportAdminController.userList(0));
    }

    /**
     * Adds Klout score for new users/those missing one if possible
     * @return redirect result leading back to first page of user list
     */
    public static Result addKloutScores() {

        //only add scores for users that don't have an existing score
        String sql = "select id from users where users.id not in (select user_id from influencer_score where klout_score is not null)";

        //parse sql
        RawSql rSql = RawSqlBuilder.parse(sql).create();
        List<User> userList = User.find.query().setRawSql(rSql).findList();

        for (int i = 0; i < userList.size(); i++) {
            Double score = KloutPlugin.getKloutScoreFromUser(userList.get(i));
            if (score != null) {
                InfluencerScore influence = new InfluencerScore();
                influence.user = userList.get(i);
                influence.kloutScore = score;
                influence.save();
            }
        }
        return redirect(routes.ReportAdminController.userList(0));
    }

    /**
     * Updates Klout score for all entries in Influencer Score table
     * @return redirect result leading back to first page of user list
     */
    public static Result updateAllKloutScores() {
        List<InfluencerScore> influencerScoreList = InfluencerScore.find.all();
        for (int i = 0; i < influencerScoreList.size(); i++) {
            influencerScoreList.get(i).kloutScore = KloutPlugin.getKloutScoreFromUser(influencerScoreList.get(i).user);
            influencerScoreList.get(i).update();
        }
        return redirect(routes.ReportAdminController.userList(0));
    }

}

