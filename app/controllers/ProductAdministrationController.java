package controllers;

import com.google.common.io.Files;

import models.Product;
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
    return ok(detail_about.render(productForm));
  }

  public static Result details(Product product) {
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
    
    /*
    MultipartFormData.FilePart part = body.getFile("picture");
    if(part != null) {
      File picture = part.getFile();

      try {
        product.picture = Files.toByteArray(picture);
      } catch (IOException e) {
        return internalServerError("Error reading file upload");
      }
    }*/
    /*
    List<Tag> tags = new ArrayList<Tag>();
    for (Tag tag : product.tags) {
      if (tag.id != null) {
        tags.add(Tag.findById(tag.id));
      }
    }
    
    product.tags = tags;
    */
    if (product.id == null) {
      product.save();
    } else {
      product.update();
    }

    flash("success",
        String.format("Successfully added product %s", product));

    return redirect(routes.ProductAdministrationController.list(1));
  }
  /*
  public static Result picture(String ean) {
    final Product product = Product.findbyProductEAN(ean);
    if(product == null) return notFound();
    return ok(product.picture);
  }
   */
  public static Result delete(Long id) {
    final Product product = Product.findbyID(id);
    if(product == null) {
        return notFound(String.format("Product %s does not exists.", id));
    }
    product.delete();
    return redirect(routes.ProductAdministrationController.list(1));
  }
}

