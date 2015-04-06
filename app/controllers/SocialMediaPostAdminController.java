package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Company;
import models.File;
import models.Post;
import models.Product;
import models.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import views.html.admin.*;

import com.avaje.ebean.*;

import play.Routes;
import play.mvc.Http.MultipartFormData.FilePart;
import play.db.ebean.Model;


public class SocialMediaPostAdminController extends Controller{

	  private static final Form<Post> socialMediaPostForm = Form.form(Post.class);


	  //@SecureSocial.SecuredAction
	  public static Result newPost() {
		  List<Product> products = new Product().getAll();
		  Post post = new Post();
		  post.activeFlag=true;
		  post.createdAt = Calendar.getInstance().getTime(); //Get the current date

		  return ok(socialmedia_post.render(socialMediaPostForm.fill(post), products));
	  }

	  public static Result save() {
	    MultipartFormData body = request().body().asMultipartFormData();
	    Map<String, String[]> mp = body.asFormUrlEncoded();
		MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
	    Form<Post> filledForm = socialMediaPostForm.bindFromRequest();
	    
	    if(filledForm.hasErrors()) {
	      flash("error", "Please correct the form below.");
//	      String errormsg = "";
//	      Iterator<ValidationError> it = boundForm.globalErrors().iterator();
//	      while(it.hasNext()){
//	    	  errormsg = errormsg + it.next().message();
//	      }
	      return badRequest("Post save error - " + filledForm.errorsAsJson());
	    }
	    
	    Post post = filledForm.get();
	    User user = null;
	    switch(post.user.externalType){
	    	
	    case "TWT" : user = User.find.where().eq("active", true).eq("url_twitter", post.user.urlTwitter).findUnique();
	    			break;
	    case "FB" : user = User.find.where().eq("active", true).eq("url_facebook", post.user.urlFacebook).findUnique();
	    			break;
	    case "PT" : user = User.find.where().eq("active", true).eq("url_pinterest", post.user.urlPinterest).findUnique();
	    			break;
	    case "BG" : user = User.find.where().eq("active", true).eq("url_blog", post.user.urlBlog).findUnique();
					break; 			
	    default: user = null; break;
	    }
	    if (user==null){
	    	
	    	post.user.external = true;
	    	post.user.save();
	    	if (uploadFilePart != null) {
				File mediaFile = new File();
				mediaFile.comment =  "external user";
				mediaFile.submittedBy = "0_admin";
				mediaFile.mediaUploadedfile = uploadFilePart.getFile();
				//Long userID = new Long(mediaFile.comment.split("_")[0]);
				mediaFile.originalName = uploadFilePart.getFilename();

				mediaFile.refId = post.user.id;
				mediaFile.refType = "models.User";
				mediaFile.userPhotoUpload();
	    	}
	    	
	    }
	    String postedDateTime = mp.get("postedAt")[0];
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
	    try {
			post.createdAt = formatter.parse(postedDateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    post.save();
	    
	    
		

	    
	    flash("success",
	        String.format("Successfully added post %s", post));

	    return redirect(routes.SocialMediaPostAdminController.newPost());

	  }

}
