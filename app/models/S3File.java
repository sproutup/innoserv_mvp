package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.elastictranscoder.model.Job;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

import play.Logger;
import play.db.ebean.Model;
import plugins.S3Plugin;
import utils.ElasticTranscoder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
public class S3File extends Model {

	private static final long serialVersionUID = 1L;
	private static ElasticTranscoder ets;

	@Id
	public UUID id;

	private String bucket;

	public String fileName;

	public String folderName;


	public String submittedUser;

	public String productName; //format is <id>_#<name>

	@Transient
	public File file;

	public URL getImageURL() {
		try {
			return new URL("http://d209heoa233yab.cloudfront.net/" + getActualFileName());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

    public URL getVideoURL() {
        try {
            return new URL("http://dc2jx5ot5judg.cloudfront.net/" + productName + "/" + id + "/" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

	public String getActualFileName() {
		return (productName + "/"+ id + "_" + fileName);
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/*
	* (non-Javadoc)
	* @see play.db.ebean.Model#save()
	* currently this method only supports the manual upload
	* when feeding from social media, the user id needs to be fixed
	*/
	@Override
	public void save() {
		if (S3Plugin.amazonS3 == null) {
			Logger.error("Could not save because amazonS3 was null");
			throw new RuntimeException("Could not save");
		}
		else {
			this.bucket = S3Plugin.s3Bucket;
			
			super.save(); // assigns an id

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, getActualFileName(), file);
			putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
			S3Plugin.amazonS3.putObject(putObjectRequest); // upload file

			/*
			* Persist the data in media table
			*/
			MimetypesFileTypeMap mfm = new MimetypesFileTypeMap();
			String mediaType = "video";
			if(fileName!=null && mfm.getContentType(fileName).contains("image/")){
				mediaType = "image";
			}

			//for manual upload of media, we are not mapping to the user id;
			//TODO fix the user when we have user signup and we want to associate users with their media
			User user = null;

			/*
			* look up product
			*/
			//parse <id>_#<name>
			String productID = null;
			if (productName!=null ) {
				productID = productName.split("_")[0];
				Logger.debug("product ID fectched =" + productID);
			}
			//look up product based on id
			Product product = Product.findbyID(new Long(productID));

            if(mediaType == "image") {
                Media mediatab = new Media(
                        id.toString(),
                        product,
                        user,
                        getSubmittedUser(),
                        productName,
                        getImageURL(),
                        fileName,
                        mediaType,
                        0,
                        true
                );
                mediatab.save();
            }
            else if (mediaType == "video") {
                Media mediatab = new Media(
                        id.toString(),
                        product,
                        user,
                        getSubmittedUser(),
                        productName,
                        getVideoURL(),
                        fileName,
                        mediaType,
                        0,
                        true
                );
                mediatab.save();
            }

			//transcoder work
			if(mediaType == "video") {
				String INPUT_KEY = getActualFileName();
				String OUTPUT_KEY = id.toString();
				String OUTPUT_KEY_PREFIX = productName + "/";
				Job job = ets.createElasticTranscoderHlsJob(INPUT_KEY, OUTPUT_KEY, OUTPUT_KEY_PREFIX);
				Logger.info("  INPUT_KEY: " + INPUT_KEY);
				Logger.info("  OUTPUT_KEY_PREFIX: " + OUTPUT_KEY_PREFIX);
				Logger.info("  HLS Transcoder job has been created: ");
				Logger.info( job + "\n");
				Logger.info("==========================================================");
			}
		}
	}

		
	@Override
	public void delete() {
		if (S3Plugin.amazonS3 == null) {
			Logger.error("Could not delete because amazonS3 was null");
			throw new RuntimeException("Could not delete");
		}
		else {
			S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
			super.delete();
		}
	}

	public String getSubmittedUser() {
		if (submittedUser!=null)
		return submittedUser.trim();
		else return "";
	}

	public void setSubmittedUser(String submittedUser) {
		this.submittedUser = submittedUser;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

}
