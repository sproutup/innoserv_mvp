package controllers;

import models.S3File;
import models.Product;
import play.data.Form;
import play.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import java.util.List;
import javax.persistence.PersistenceException;

public class Products extends Controller {

  //public static Result GO_HOME = redirect(routes.Application.index());

  private static final Form<S3File> mediaUploadForm = Form.form(S3File.class);

  public static Result list() {
    List<Product>products = new Product().getAll();
	  return ok(product_list.render(products));
  }

  /*
  public static Result newProduct() {
  return TODO;
}*/

    public static Result details(Long id) {
        //Product product = new Product().findbyID(id);
        Product product = new Product().getDetailwithMedia(id);
        return ok(product_item_2.render(product, mediaUploadForm));
    }

    public static Result detailsBySlug(String name) {
        Product product = new Product().findbySlug(name);
        return ok(product_item_2.render(product, mediaUploadForm));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProducts()
    {
        List<Product> products_ = new Product().getAll();
        return ok(Json.toJson(products_));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProduct(Long id)
    {
        Product item = new Product().findbyID(id);
        return item == null ? notFound("Product not found [" + id + "]") : ok(Json.toJson(item));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result createProduct()
    {
        models.Product newProduct = Json.fromJson(request().body().asJson(), models.Product.class);
        Logger.debug("create product " + newProduct.productName);
        try {
            newProduct.save();
            return created(Json.toJson(newProduct));
        }
        catch(PersistenceException e){
            return play.mvc.Results.badRequest(e.getMessage());
        }
    }

    public static Result updateProduct(Long id)
    {
        Product existing = new Product().findbyID(id);
        if(existing != null) {
            models.Product updated = Json.fromJson(request().body().asJson(), models.Product.class);
            if(updated.productName != null) { existing.productName = updated.productName; };
            if(updated.productEAN != null) { existing.productEAN = updated.productEAN; };
            if(updated.productDescription != null) { existing.productDescription = updated.productDescription; };
            if(updated.productLongDescription != null) { existing.productLongDescription = updated.productLongDescription; };
            if(updated.urlHome != null) { existing.urlHome = updated.urlHome; };
            if(updated.urlFacebook != null) { existing.urlFacebook = updated.urlFacebook; };
            if(updated.urlTwitter != null) { existing.urlTwitter = updated.urlTwitter; };
            existing.save();
            return ok(Json.toJson(existing));
        }
        else{
            return play.mvc.Results.notFound("Product not found");
        }
    }

    public static Result deleteProduct(Long id)
    {
        Product del = new Product().findbyID(id);
        if(del != null) {
            del.delete();
            return ok();
        }
        else{
            return play.mvc.Results.notFound("Product not found");
        }
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
