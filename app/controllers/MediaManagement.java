package controllers;

import java.util.List;
import java.util.UUID;

import models.S3File;
import models.Product;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Result;
import play.db.ebean.Model;
import play.mvc.Http.MultipartFormData;

import views.html.*;

public class 	MediaManagement extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	private static final Form<S3File> mediaUploadForm = Form.form(S3File.class);

	public static Result upload() {
		List<S3File> uploads = new Model.Finder(UUID.class, S3File.class).all();
		List<Product> products = new Product().getAll();
		return ok(internalonlyupload.render(uploads, mediaUploadForm, products));
	}

	public static Result doUpload() {

		Form<S3File> filledForm = mediaUploadForm.bindFromRequest();
		S3File s3File = filledForm.get();

		MultipartFormData body = request().body().asMultipartFormData();
		MultipartFormData.FilePart uploadFilePart = body.getFile("upload");

		Long productID = new Long(s3File.productName.split("_")[0]);

		if (uploadFilePart != null) {
			s3File.fileName = uploadFilePart.getFilename();
			s3File.file = uploadFilePart.getFile();
			s3File.save();
			return redirect(routes.MediaManagement.upload());
		}
		else {
			return badRequest("File upload error");
		}
	}

}
