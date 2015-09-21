package controllers;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import service.GoogleURLShortener;
import views.html.admin.*;

/**
 * Manage Share Contest actions
 */
public class ContestController extends Controller {
    
	private static final Form<Contest> contestForm = Form.form(Contest.class);
	private static final Boolean admin_enabled = Boolean.parseBoolean(Play.application().configuration().getString("admin.enabled"));

	/*
	 * Gets called when product detail page is loaded
	 */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getActiveContest(Long productId) {//get contest information
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("product_id", productId);
        params.put("active", "1");
        //TODO nj: need to handle when there are more than one active contest? get the latest "order by id desc"? check if findUnique would fail..
        Contest contest = Contest.find.where().allEq(params).findUnique();
		ObjectNode rs;
    	if (contest==null) {
    		rs = Json.newObject();
        	rs.put("active", false);
    	} else {
    		rs = contest.toJson();
    	}
    	return created(rs);
    }
    
	/*
	 * Gets called when user clicks on the SproutUp or Share button
	 */
	@BodyParser.Of(BodyParser.Json.class)
    public static Result getContestURL(String productSlug) {
        	
        	ObjectNode rs = Json.newObject();
    		String genURL = "http://sproutup.co/product/" + productSlug + "?refId=contest";
            String shortURL= GoogleURLShortener.shortenURL(genURL);
        	rs.put("url", shortURL);
            return created(rs);
        
    }
    
    /*
     * This is post processing when user
     * has shared the contest
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result processShare() {
        JsonNode json = request().body().asJson();
        long contestId = json.findPath("contestId").longValue();

        //update the counter
        Contest cont = Contest.find.byId(contestId);

            cont.totalNumParticipated++;
            cont.update();
	
            return created(cont.toJson());
            
        }
  
    	
    	/*
    	 * for admin page
    	 */
    public static Result list() {
        if(!admin_enabled){return notFound();};

        Logger.debug("mode: " + play.api.Play.current().mode());
        List<Contest> contests = Contest.findAll();
        return ok(contest_list.render(contests));
      }  
    
    public static Result newContest() {
		  if(!admin_enabled){return notFound();};

		  List<Product> products = new Product().getAllActive();
		  Contest cont = new Contest();
		  cont.active=true;
		  cont.beginDate = Calendar.getInstance().getTime(); //Get the current date

		  return ok(contest_management.render(contestForm.fill(cont), products));
	  }
    
    public static Result details(Long id) {
        if(!admin_enabled){return notFound();};
        List<Product> products = new Product().getAllActive();
        
        return ok(contest_management.render(contestForm.fill(Contest.find.byId(id)), products));
      }
    
    
    public static Result save() {
        if(!admin_enabled){return notFound();};

        Form<Contest> contF = contestForm.bindFromRequest();
        Contest contest = contF.get();

        String[] postAction = request().body().asFormUrlEncoded().get("action");
        if (postAction == null || postAction.length == 0) {
          return badRequest("You must provide a valid action");
        } else {
          String action = postAction[0];
          
          if ("Copy".equals(action)) {
        	  contest.id=null;
        	  contest.product.id=null;
           } else {//if not copying update the flag;
        	   Product prod = Product.findbyID(contest.product.id);
	           prod.contestFlag = contest.active;
	           prod.update();
             
           }
        }  
        if (contest.id == null) {
        	contest.save();
        } else {
        	//update campaign
        	contest.update();
        }
        
        
       return redirect(routes.ContestController.list());
    }
    
}
