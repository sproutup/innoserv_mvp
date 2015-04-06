package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import net.coobird.thumbnailator.Thumbnails;
import play.Routes;
import play.Logger;
import play.api.cache.Cache;
import play.cache.Cached;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Session;
import plugins.S3Plugin;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

import constants.AppConstants;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";

    public static Result main(String any) {
        return ok(index.render());
    }

    public static Result home() {
        List<Product>products = new Product().getAll();
        return ok(home.render(products));
    }

    public static Result index() {
        List<Product>products = new Product().getAll();
        return ok(index.render());
    }

    public static Result about() {
        return ok(about.render());
    }

    public static Result search() {
        return ok(search.render());
    }

	public static Result creator() {
		return ok(creator.render());
	}

	public static User getLocalUser(final Session session) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		return localUser;
	}

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile() {
        return ok(profile.render());
    }

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile_photos() {return ok(profile_photos.render()); }

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile_videos() {return ok(profile_videos.render()); }

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile_products() {return ok(profile_products.render()); }

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile_followers() {return ok(profile_followers.render()); }

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile_followings() {return ok(profile_followings.render()); }

    @Restrict({@Group(AppConstants.CONSUMER)})
    public static Result profile_account() {return ok(profile_account.render()); }

    public static Result login() {
		return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	public static Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(login.render(filledForm));
		} else {
			// Everything was filled
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

	public static Result signup() {
		return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
	}

	public static Result jsRoutes() {
		return ok(
				Routes.javascriptRouter("jsRoutes",
						controllers.routes.javascript.Signup.forgotPassword()))
				.as("text/javascript");
	}

	public static Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		MyUsernamePasswordAuthProvider.context = ctx();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(signup.render(filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup
			return UsernamePasswordAuthProvider.handleSignup(ctx());
		}
	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addProductSuggestion() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            ProductSuggestion prds = new ProductSuggestion();
            prds.email = json.path("email").asText();
            prds.productName = json.path("productName").asText();
            prds.productUrl = json.path("productUrl").asText();
            prds.save();

            return created();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addProductTrial() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            ProductTrial trial = new ProductTrial();
            trial.email = json.path("email").asText();
            trial.name = json.path("name").asText();
            Product prod = Product.findbyID(json.path("product_id").asLong());
            if(prod != null){
                trial.product = prod;
            }
            User user = Application.getLocalUser(ctx().session());
            if (user != null) {
                trial.user = user;
                trial.email = user.email;
                trial.name = user.firstName + " " + user.lastName;
            }

            trial.save();

            return created();
        }
    }

    @SubjectPresent
    public static Result getProductTrial(Long product_id, String type){
        User user = Application.getLocalUser(ctx().session());
        if (user == null) {
            return badRequest("User not found");
        } else {
            ProductTrial trial = ProductTrial.find.where()
                    .eq("user_id", user.id)
                    .eq("product_id", product_id)
                    .eq("active", "1").findUnique();
            if(trial != null){
                return ok();
            }
            else{
                return notFound();
            }
        }
    }

            // Scala and render an image from S3
    public static Result image(String image, int w, int h) {
        Logger.debug("get object image");
        S3Object obj = S3Plugin.amazonS3.getObject("sproutup-test-upload", image);

        if (obj != null) {
            BufferedImage thumbnail;

            try {
                S3ObjectInputStream originalImage = obj.getObjectContent();
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                Logger.debug("convert image");

                if(w==-1 && h==-1){
                    Thumbnails.of(originalImage)
                            .size(80, 80)
                            .outputFormat("jpg")
                            .toOutputStream(out);
                }
                else
                if(h==-1){
                    Thumbnails.of(originalImage)
                            .width(w)
                            .outputFormat("jpg")
                            .toOutputStream(out);
                }
                else
                if(w==-1){
                    Thumbnails.of(originalImage)
                            .height(h)
                            .outputFormat("jpg")
                            .toOutputStream(out);
                }
                else {
                    Thumbnails.of(originalImage)
                            .size(w, h)
                            .outputFormat("jpg")
                            .toOutputStream(out);
                }

                return ok(out.toByteArray()).as("image/jpg");
            } catch (Exception e) {
                Logger.debug("image error: " + e.getMessage());
            }
        }
        return notFound();
    }
}
