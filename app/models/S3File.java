package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import play.Logger;
import play.db.ebean.Model;
import plugins.S3Plugin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Entity
public class S3File extends Model {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Id
  public UUID id;

  private String bucket;

  public String fileName;
  
  public String folderName;
  
  public String submittedUser;
  
  public String productName;

  @Transient
  public File file;

  public URL getUrl() throws MalformedURLException {
    return new URL("https://s3.amazonaws.com/" + bucket + "/" + folderName + "/" + getActualFileName());
  }

  public String getActualFileName() {
    return id + "_" + fileName;
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

public String getFolderName() {
	    return folderName;
	  }

  
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
	return submittedUser;
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
