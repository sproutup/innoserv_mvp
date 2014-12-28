package controllers;


import java.net.MalformedURLException;
import java.net.URL;

import models.Feedback;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import utils.AppConstants;
import views.html.*;

public class FeedbackManagement extends Controller {

  private static final Form<Feedback> feedbackForm = Form.form(Feedback.class);

  public static Result feedback() {
    return ok(feedback.render());
  }
  
  /*
   * Pop up Qualtrics Survey
   */
  public static Result fetchPopupQSurvey(Long productID){

	  //TODO add logic to fetch belleds survey url based on productID
		String belleds_url = AppConstants.BELLEDS_URL_AS_STRING;
		//TODO if user is not in session redirect to login and then to survey else
		//TODO get user credentials from the session
		String userEmail = "nitintest%40gmail.com";
		String userID ="123456";
		Logger.debug("ready to redirect to Qualtrics");
		
		belleds_url = belleds_url + "&userEmail=" + userEmail + "&userID=" + userID;
	  
	  
	  return redirect(belleds_url); 
	  
		
	}

}
