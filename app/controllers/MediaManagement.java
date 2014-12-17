package controllers;

import java.util.List;
import java.util.UUID;

import models.S3File;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Result;
import play.db.ebean.Model;

import views.html.*;

public class MediaManagement extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	public static Result upload() {
		List<S3File> uploads = new Model.Finder(UUID.class, S3File.class).all();
		return ok(upload.render(uploads));
	}

	public static Result doUpload() {
		Http.MultipartFormData body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
		if (uploadFilePart != null) {
			S3File s3File = new S3File();
			s3File.name = uploadFilePart.getFilename();
			s3File.file = uploadFilePart.getFile();
			s3File.save();
			return redirect(routes.MediaManagement.upload());
		}
		else {
			return badRequest("File upload error");
		}
	}



	

}
