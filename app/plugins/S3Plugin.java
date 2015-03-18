package plugins;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import play.Application;
import play.Logger;
import play.Plugin;

public class S3Plugin extends Plugin {

  public static final String AWS_S3_BUCKET = "aws.s3.bucket";
  public static final String AWS_ACCESS_KEY = "aws.access.key";
  public static final String AWS_SECRET_KEY = "aws.secret.key";
  private final Application application;

  public static AmazonS3 amazonS3;
  public static String accessKey;
  public static String secretKey;

  public static String s3Bucket;

  public S3Plugin(Application application) {
    this.application = application;
  }

  @Override
  public void onStart() {
    accessKey = application.configuration().getString(AWS_ACCESS_KEY);
    secretKey = application.configuration().getString(AWS_SECRET_KEY);
    s3Bucket = application.configuration().getString(AWS_S3_BUCKET);

    if ((accessKey != null) && (secretKey != null)) {
      AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
      amazonS3 = new AmazonS3Client(awsCredentials);
//      amazonS3.createBucket(s3Bucket);
      Logger.info("Using S3 Bucket: " + s3Bucket);
    }
  }

  @Override
  public boolean enabled() {
    return (application.configuration().keys().contains(AWS_ACCESS_KEY) &&
    application.configuration().keys().contains(AWS_SECRET_KEY) &&
    application.configuration().keys().contains(AWS_S3_BUCKET));
  }

}
