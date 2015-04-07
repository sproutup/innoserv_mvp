package controllers;

import com.google.common.io.Files;

import models.Company;
import models.Product;
import models.ProductAdditionalDetail;
import models.Tag;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.With;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.*;

import static play.mvc.Http.MultipartFormData;
import views.html.productadministration.*;

public class ProductAdministrationController extends Controller {

  private static final Form<Product> productForm = Form.form(Product.class);

  public static Result index() {
    return redirect(routes.ProductAdministrationController.list(0));
  }

  public static Result list(Integer page) {
    Page<Product> products = Product.find(page);//findAll();
    return ok(list.render(products));
  }

  //@SecureSocial.SecuredAction
  public static Result newProduct() {
	  Product prod = new Product();
	  prod.activeFlag=true;
	  
	  return ok(detail_about.render(productForm.fill(prod)));
  }

  public static Result details(Product product) {
	List<Tag> tags = product.getAllTags();
	String tagName = "";
	for (int i = 0; i < tags.size(); i++) {
		tagName += tags.get(i).name;
		if (i<(tags.size()-1))
			tagName = tagName + ", ";
	}
	product.tags = tagName;
	
    Form<Product> filledForm = productForm.fill(product);
    return ok(detail_about.render(filledForm));
  }

  public static Result save() {
    MultipartFormData body = request().body().asMultipartFormData();
    Form<Product> boundForm = productForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
      return badRequest(detail_about.render(boundForm));
    }
    
    Product product = boundForm.get();
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

    return redirect(routes.ProductAdministrationController.list(1));
  }

  public static Result delete(Long id) {
    final Product product = Product.findbyID(id);
    if(product == null) {
        return notFound(String.format("Product %s does not exists.", id));
    }
    product.delete();
    return redirect(routes.ProductAdministrationController.list(1));
  }
}

