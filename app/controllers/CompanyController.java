package controllers;

import models.Company;
import models.User;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.persistence.PersistenceException;
import java.util.List;

public class CompanyController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result query()
    {
        List<Company> companies = Company.getAll();
        return ok(Company.toJson(companies));
    }

//    @BodyParser.Of(BodyParser.Json.class)
//    public static Result getProduct(Long id)
//    {
//        Product item = new Product().findbyID(id);
//        return item == null ? notFound("Product not found [" + id + "]") : ok(item.toJson());
//    }
//
//    @BodyParser.Of(BodyParser.Json.class)
//    public static Result getProduct(String slug)
//    {
//        Product item = new Product().findbySlug(slug);
//        return item == null ? notFound("Product not found [" + slug + "]") : ok(item.toJson());
//    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result save()
    {
        Company obj = Json.fromJson(request().body().asJson(), Company.class);
        Logger.debug("create company " + obj.companyName);
        try {
            User user = Application.getLocalUser(ctx().session());
            if(user.company!=null){
                Logger.debug("Error - users can only have one company");
                return badRequest("Users can only have one company");
            }
            obj.save();
            if (user != null) {
                user.company = obj;
            }
            user.save();
            return created(obj.toJson());
        }
        catch(PersistenceException e){
            return Results.badRequest(e.getMessage());
        }
    }

//    public static Result updateProduct(Long id)
//    {
//        Product existing = new Product().findbyID(id);
//        if(existing != null) {
//            Product updated = Json.fromJson(request().body().asJson(), Product.class);
//            if(updated.productName != null) { existing.productName = updated.productName; };
//            if(updated.productEAN != null) { existing.productEAN = updated.productEAN; };
//            if(updated.productDescription != null) { existing.productDescription = updated.productDescription; };
//            if(updated.productLongDescription != null) { existing.productLongDescription = updated.productLongDescription; };
//            if(updated.urlHome != null) { existing.urlHome = updated.urlHome; };
//            if(updated.urlFacebook != null) { existing.urlFacebook = updated.urlFacebook; };
//            if(updated.urlTwitter != null) { existing.urlTwitter = updated.urlTwitter; };
//            existing.save();
//            return ok(existing.toJson());
//        }
//        else{
//            return Results.notFound("Product not found");
//        }
//    }

//    public static Result deleteProduct(Long id)
//    {
//        Product del = new Product().findbyID(id);
//        if(del != null) {
//            del.delete();
//            return ok();
//        }
//        else{
//            return Results.notFound("Product not found");
//        }
//    }
}
