import java.util.Arrays;

import models.Product;
import models.SecurityRole;

import com.avaje.ebean.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;
import play.mvc.Call;
import play.data.format.Formatters;
import play.data.format.Formatters.*;
import utils.AnnotationDateFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Global extends GlobalSettings {

	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Application.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return routes.Application.index();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});

		initialData();
		dateFormatter();
	}

	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays
					.asList(controllers.Application.USER_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
		
		//load products data
        if(Ebean.find(Product.class).findRowCount() == 0) {
            @SuppressWarnings("unchecked")
			Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");
            Ebean.save(all.get("products"));
          
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