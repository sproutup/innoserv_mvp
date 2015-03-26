package controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Company;
import models.File;
import models.S3File;
import models.Product;
import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.MultipartFormData.FilePart;
import play.db.ebean.Model;
import play.mvc.Http.MultipartFormData;
import views.html.*;
import views.html.productadministration.*;

public class 	MediaManagement extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	private static final Form<S3File> mediaUploadForm = Form.form(S3File.class);
	private static final Form<File> companymediaUploadForm = Form.form(File.class);

	/*
	 * Admin method for uploading user content
	 * @author Tao date: Jan 2015
	 */
	public static Result upload() {
		List<S3File> uploads = new Model.Finder(UUID.class, S3File.class).all();
		List<Product> products = new Product().getAll();
		return ok(internalonlyupload.render(uploads, mediaUploadForm, products));
	}

	/*
	 * Admin Method for uploading user content
	 * @author Tao date: Jan 2015 
	 */
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

	/*
	 * Method for uploading media content for AboutUs page
	 * @author Nitin Date: March 2015
	 */
	public static Result uploadCompanyMedia(String companySlug, String mediaType) {
		//List<File> uploads = new Model.Finder(UUID.class, File.class).all();
		List<Company> companies = Company.getAll();
		File fl = new File();
		fl.comment =  companySlug;
		fl.submittedBy = "0_admin";
		fl.mediaLinkType = mediaType;
		return ok(mediaupload.render(companymediaUploadForm.fill(fl), companies));
	}

	/*
	 * Method for uploading user content
	 * @author Tao 
	 */
	public static Result doCompanyMediaUpload() {
		User user = Application.getLocalUser(ctx().session());
        
		Form<File> filledForm = companymediaUploadForm.bindFromRequest();
		
		File mediaFile = filledForm.get();

		MultipartFormData body = request().body().asMultipartFormData();
		Map<String, String[]> mp = body.asFormUrlEncoded();
		MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
		if (uploadFilePart != null) {

			mediaFile.mediaUploadedfile = uploadFilePart.getFile();
			Long companyID = new Long(mediaFile.comment.split("_")[0]);
			mediaFile.originalName = uploadFilePart.getFilename();

			mediaFile.refId = companyID;
			mediaFile.refType = "models.Company";
			if(user != null) {
				mediaFile.user = user;
	        }
			
			mediaFile.companyMediaUpload();
			String refererUrl = mp.get("previousURL")[0];
			return redirect(refererUrl);
			//return redirect(routes.MediaManagement.uploadCompanyMedia(mediaFile.comment));
		}
		else {
			return badRequest("File upload error");
		}
	}

}
