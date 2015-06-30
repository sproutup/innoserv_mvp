package controllers;

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
import java.util.List;

import models.ProductSuggestion;
import models.ProductTrial;
import models.User;

import com.avaje.ebean.*;

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
    return ok(userlist.render(users));
  }

  public static Result trialList(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<ProductTrial> trials = ProductTrial.find(page);//findAll();
	    return ok(trial_list.render(trials));
  }

  public static Result influencerTrialList(Integer page) {
        if(!admin_enabled){return notFound();};

        Logger.debug("mode: " + play.api.Play.current().mode());
        Page<ProductTrial> trials = ProductTrial.find(page);//findAll();
        return ok(influencer_trial_list.render(trials));
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


}
