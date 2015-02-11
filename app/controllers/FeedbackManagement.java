package controllers;


import java.net.MalformedURLException;
import java.net.URL;

import models.User;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import providers.MyUsernamePasswordAuthProvider;
import utils.AppConstants;
import views.html.*;

public class FeedbackManagement extends Controller {

 
  public static Result feedback() {
    return ok(feedback.render());
  }
  
  /*
   * Pop up Qualtrics Survey
   */
  public static Result fetchPopupQSurvey(Long productID){

	  //TODO add logic to fetch belleds survey url based on productID
		String belleds_url = AppConstants.BELLEDS_URL_AS_STRING;

		//TODO get user credentials from the session
		User user = Application.getLocalUser(session());
			
		if (user == null) {
			Logger.debug(">>User is null");
			//go to login page
			//return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
		}
		
		String userEmail = user.email;
		String userName =user.name;
		Logger.debug("ready to redirect to Qualtrics");
		
		belleds_url = belleds_url + "&userEmail=" + userEmail + "&userName=" + userName;
	  
	  
	  return redirect(belleds_url); 
	  
		
	}

}
