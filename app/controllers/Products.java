package controllers;

import models.Product;
import play.*;
import play.mvc.*;
import views.html.*;
import java.util.List;

public class Products extends Controller {
  
  //public static Result GO_HOME = redirect(routes.Application.index());

  public static Result list() {
    List<Product>products = new Product().getAll();
    return ok(product.render(products));
  }

  /*
  public static Result newProduct() {
  return TODO;
}*/

public static Result details(String productID) {
  return TODO;
}

/*
public static Result save() {
return TODO;
}*/

}
