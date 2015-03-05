package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import models.File;
import models.Product;
import models.User;
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
	
	@Restrict({@Group(AppConstants.CONSUMER),@Group(AppConstants.CREATOR)})
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
		final User localUser = getLocalUser(session());
		return ok(profile.render(localUser));
	}

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


    // Scala and render an image from S3
    public static Result image(String image, int x, int y) {

//        ByteArrayOutputStream hit = (ByteArrayOutputStream) Cache.get(image + x + "x" + y, null);
//
//        if(hit!=null){
//            Logger.debug("cache hit");
//            return ok(hit).as("image/jpg");
//        }

        Logger.debug("get object image");
        S3Object obj = S3Plugin.amazonS3.getObject("sproutup-test-upload", image);

        if (obj != null) {
            BufferedImage thumbnail;

            try {

                Logger.debug("get object content stream");

                S3ObjectInputStream originalImage = obj.getObjectContent();

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                Logger.debug("convert image");

                Thumbnails.of(originalImage)
                        .size(x, y)
                        .outputFormat("jpg")
                .toOutputStream(out);

//                Cache.set(image + x + "x" + y, out, 60 * 15, null);

                return ok(out.toByteArray()).as("image/jpg");
            } catch (Exception e) {
                Logger.debug("error getting object image");
            }
        }
        return notFound();
    }
}
