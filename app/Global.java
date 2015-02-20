import java.util.Arrays;

import models.Product;
import models.SecurityRole;

import com.avaje.ebean.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import constants.UserRole;
import controllers.routes;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Yaml;
import play.mvc.Call;
import play.data.format.Formatters;
import play.data.format.Formatters.*;
import utils.AnnotationDateFormatter;
import play.mvc.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.persistence.OptimisticLockException;


public class Global extends GlobalSettings {

	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
                Logger.debug("login call");
				return routes.AuthController.afterLogin();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved with PlayAuthenticate.storeOriginalUrl(...)
				//return routes.Application.index();
                return routes.AuthController.afterAuth();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
                Logger.debug("auth call global");
				// save original url so user is redirected back to where user came from
				//Logger.debug(PlayAuthenticate.storeOriginalUrl(Http.Context.current()));

				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
                return routes.Application.index(); // logoutfirststupid()
//                return null;
			}

			@Override
			public Call askLink() {
				return null;
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
                    Logger.debug("AccessDenied");
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
                Logger.debug("AccessDenied exception");
				return super.onException(e);
			}
		});

		initialData();
		dateFormatter();
	}

	private void initialData() {
		if (UserRole.values().length > SecurityRole.find.findRowCount()) {
			for (UserRole roleName : UserRole.values()) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName.name();
				role.save();
			}
		}

		//load products data
        try {

        	@SuppressWarnings("unchecked")
			Map<String,ArrayList<Object>> all = (Map<String,ArrayList<Object>>)Yaml.load("initial-data.yml");
			if (all!=null){
				ArrayList<Object> entries = all.get("products");
				int prodEntriesCount = Ebean.find(Product.class).findRowCount();
				int newEntriesCount = entries.size() - prodEntriesCount;
				if( newEntriesCount>0 ) {
					Logger.debug("Adding" + newEntriesCount + "new entries in the product table");
				    for (int i = 0; i< prodEntriesCount;i++)
				    	entries.remove(0);
					Ebean.save(entries);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error("Problem adding new entries in the product table");
			e.printStackTrace();
		}



	}

	private void dateFormatter(){
		  // Register our DateFormater
        Formatters.register(Date.class,
                     new SimpleFormatter<Date>() {

         private final static String PATTERN = "dd-MM-yyyy";

         public Date parse(String text, Locale locale)
            throws java.text.ParseException {
           if(text == null || text.trim().isEmpty()) {
             return null;
           }
           SimpleDateFormat sdf =
             new SimpleDateFormat(PATTERN, locale);
           sdf.setLenient(false);
           return sdf.parse(text);
         }

         public String print(Date value, Locale locale){
           if(value == null) {
            return "";
           }
           return new SimpleDateFormat(PATTERN, locale)
                      .format(value);
         }

        });
      Formatters.register(Date.class, new AnnotationDateFormatter());

	}
}
