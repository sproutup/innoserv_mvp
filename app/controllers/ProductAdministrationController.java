package controllers;

import com.google.common.io.Files;

import models.Community;
import models.Company;
import models.Product;
import models.ProductAdditionalDetail;
import models.Tag;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.With;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avaje.ebean.*;

import constants.AppConstants;
import static play.mvc.Http.MultipartFormData;
import views.html.productadministration.*;

public class ProductAdministrationController extends Controller {

  private static final Form<Product> productForm = Form.form(Product.class);
  private static final Boolean admin_enabled = Boolean.parseBoolean(Play.application().configuration().getString("admin.enabled"));

  public static Result index() {
    return redirect(routes.ProductAdministrationController.list(0));
  }

  public static Result list(Integer page) {
    if(!admin_enabled){return notFound();};

    Logger.debug("mode: " + play.api.Play.current().mode());
    Page<Product> products = Product.find(page);//findAll();
    return ok(list.render(products));
  }

  /**
   * Products available for trial
   * @param page
   * @return
   */
  public static Result listTrials(Integer page) {
	    if(!admin_enabled){return notFound();};

	    Logger.debug("mode: " + play.api.Play.current().mode());
	    Page<Product> products = Product.find.where().eq("trial_sign_up_flag", "1")
                .orderBy("active_flag desc, id desc")
                .findPagingList(2000)
                .setFetchAhead(false)
                .getPage(page);
	    		
	    return ok(trial_list.render(products));
  }
  
	/**
	 * Products that have been requested by Influencers
	 * @param page
	 * @return
	 */
	public static Result listInfluencerTrials(int page) {
		if(!admin_enabled){return notFound();};
		List<Integer> s2 = Arrays.asList(0, 1, 2, 3, 4);
		Page<Product> products = Product.find.fetch("trials", new FetchConfig().query())
	            	.fetch("trials.user", new FetchConfig().query())
          		.fetch("trials.user.roles", new FetchConfig().query())
	            	.where()
	            	.eq("trials.user.roles.roleName", AppConstants.INFLUENCER)
	            	.in("trials.status", s2)
	            	.orderBy("productName asc, trials.id asc")
	                .findPagingList(2000)
	                .setFetchAhead(false)
	                .getPage(page);
		return ok(product_trial_list.render(products));
	}
	
	/**
	 * Products that have been requested by All Users
	 * @param page
	 * @return
	 */
	public static Result listAllUserTrials(int page) {
		if(!admin_enabled){return notFound();};
		Page<Product> products = Product.find.fetch("trials", new FetchConfig().query())
	            	.fetch("trials.user", new FetchConfig().query())
	            	.where()
	            	.isNotNull("trials.id")
                	.orderBy("productName asc, trials.id asc")
	                .findPagingList(2000)
	                .setFetchAhead(false)
	                .getPage(page);
		return ok(product_trial_list_all.render(products));
	}
  
  //@SecureSocial.SecuredAction
  public static Result newProduct() {
    if(!admin_enabled){return notFound();};

	  Product prod = new Product();
	  prod.activeFlag=true;
	  return ok(detail_about.render(productForm.fill(prod),Community.getAll()));
  }

  public static Result details(Product product) {
    if(!admin_enabled){return notFound();};

	List<Tag> tags = product.getAllTags();
	String tagName = "";
	if (tags!=null){
		for (int i = 0; i < tags.size(); i++) {
			tagName += tags.get(i).name;
			if (i<(tags.size()-1))
				tagName = tagName + ", ";
		}
	}
	product.tags = tagName;
	
    Form<Product> filledForm = productForm.fill(product);
    return ok(detail_about.render(filledForm,Community.getAll()));
  }

  public static Result save() {
    if(!admin_enabled){return notFound();};

    MultipartFormData body = request().body().asMultipartFormData();
    Form<Product> boundForm = productForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
      return badRequest(detail_about.render(boundForm,Community.getAll()));
    }
    Logger.debug("no errors encountered with the form; processing for save"); 
    Product product = boundForm.get();

    //set reference to community
	if (product.community.id!=null && !product.community.id.equals("") ) {
		Logger.debug("assigning product to community with id:" + product.community.id); 
		Community comm = Community.findbyID(product.community.id);
		product.community = comm;
	} else {
		Logger.debug("No community assigned:" + product.community.id); 
		product.community.id = null;
		product.community = null;
	}

    
    if (product.id == null) {
      product.save();
    } else {
    	//update company
    	if (product.company!=null && product.company.id!=null && !product.company.id.equals("") ) {
    		Company comp = Company.findbyID(product.company.id);
    		comp.companyName = product.company.companyName;		
    		product.company = comp;
    	}
    	
    	/*
       * nj: I struggled quite a bit to get the detail to update automatically by product.
       * AFter several trials, I had to call update on detail explicitly to get it working.. 
       */
    	ProductAdditionalDetail pad = ProductAdditionalDetail.findbyProductID(product.id);
    	if (pad!=null){
    		product.productAdditionalDetail.update();
    	}
    	product.update();
    }
    
    /*
     * save tags
     */
    //remove existing tags
    product.removeAllTags();
    
    //parse and add tags again
    if (product.tags!=null && product.tags.trim().length() > 0){
    	String[] tag = product.tags.split(",");
    	for (int i=0; i < tag.length; i++) {
    	    product.addTag(tag[i].trim());
    	}
    }
    
    flash("success",
        String.format("Successfully added product %s", product));

    return redirect(routes.ProductAdministrationController.list(0));
  }

  public static Result delete(Long id) {
    if(!admin_enabled){return notFound();};

    final Product product = Product.findbyID(id);
    if(product == null) {
        return notFound(String.format("Product %s does not exists.", id));
    }
    product.delete();
    return redirect(routes.ProductAdministrationController.list(0));
  }
}

