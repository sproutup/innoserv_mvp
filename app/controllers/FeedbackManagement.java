package controllers;

import java.util.List;
import java.util.UUID;

import models.Feedback;
import models.Product;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Result;
import play.db.ebean.Model;
import play.mvc.Http.MultipartFormData;

import views.html.*;

public class FeedbackManagement extends Controller {

  private static final Form<Feedback> feedbackForm = Form.form(Feedback.class);

  public static Result feedback() {
    return ok(feedback.render());
  }

}
