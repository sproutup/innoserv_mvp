package controllers;

import models.Product;
import play.*;
import play.mvc.*;
import views.html.*;

import java.util.List;

import com.avaje.ebean.Page;

public class Products extends Controller {

  //public static Result GO_HOME = redirect(routes.Application.index());

  public static Result list() {
    //List<Product>products = new Product().getAll();
	  Page<Product> products = new Product().find(0);
	  return ok(product_list.render(products));
  }

  /*
  public static Result newProduct() {
  return TODO;
}*/

	public static Result details(Long id) {
	  Product product = new Product().findbyID(id);
	  return ok(product_item.render(product));
	}

/*
	public static Result save() {
	Form<Product> boundForm = productForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
      return badRequest(details.render(boundForm));
    }

    Product product = boundForm.get();

    List<Tag> tags = new ArrayList<Tag>();
    for (Tag tag : product.tags) {
      if (tag.id != null) {
        tags.add(Tag.findById(tag.id));
      }
    }
    product.tags = tags;

    product.save();
    flash("success",
        String.format("Successfully added product %s", product));

    return redirect(routes.Products.list(1));
	}*/

}
