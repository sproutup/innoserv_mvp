package models;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;
import plugins.S3Plugin;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by peter on 3/3/15.
 */
@Entity
public class File extends TimeStampModel {
    public static Finder<Long, File> find = new Model.Finder<Long, File>(Long.class,
            File.class);

    @Id
    @GeneratedValue
    public Long id;

    public UUID uuid;

    @ManyToOne
    public User user;

    public Long refId;

    public String refType;

    // S3 specific fields
    public String bucket;
    public String name;
    public String originalName;
    public String type;
    public Long length;
    public String folder;

    @Column(columnDefinition = "TEXT")
    public String comment;

    @Column(columnDefinition = "boolean default false")
    public boolean verified;

    public static File findByUUID(final UUID uuid) {
        return find.where().eq("uuid", uuid).findUnique();
    }

    public static File findByName(final String name) {
        return find.where().eq("name", name).findUnique();
    }

    @Override
    public void save() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not save because amazonS3 was null");
            throw new RuntimeException("Could not save");
        } else {
            if(this.bucket == null) {
                this.bucket = S3Plugin.s3Bucket;
            }

            super.save(); // assigns an id
        }
    }

    @Override
    public void delete() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not delete because amazonS3 was null");
            throw new RuntimeException("Could not delete");
        }
        else {
            S3Plugin.amazonS3.deleteObject(bucket, fileName());
            super.delete();
        }
    }

    public static boolean verify(String id){
        File file = File.findByUUID(UUID.fromString(id));
        if(file != null) {
            file.verified = true;
            file.save();
            return true;
        }
        else {
            return false;
        }
    }

    public String fileName() {
        return (uuid + "_"+ user.id + ".jpg");
    }

    /**
     * Generates an Amazon S3 signature V4 authorization.
     */
    public ObjectNode authorize(String bucketName, String regionName, String awsAccessKey, String awsSecretKey, String contentHashString, User user, Long contentLength) {
        ObjectNode node = Json.newObject();

        this.uuid = UUID.randomUUID();
        this.bucket = bucketName;
        this.folder = "";
        this.user = user;
        this.name = this.fileName();
        this.save();

        URL endpointUrl;
        try {
            if (regionName.equals("us-west-2")) {
                endpointUrl = new URL("https://" + bucketName + ".s3.amazonaws.com/" + this.fileName());
            } else {
                endpointUrl = new URL("https://s3-" + regionName + ".amazonaws.com/" + bucketName + "/" + this.fileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }

        node.put("url", endpointUrl.toString());

        // precompute hash of the body content
//        byte[] contentHash = utils.AWS4SignerBase.hash(objectContent);
//        String contentHashString = utils.BinaryUtils.toHex(contentHash);

        Map<String, String> headers = new HashMap<String, String>();

        headers.put("x-amz-content-sha256", contentHashString);
        headers.put("content-length", "" + contentLength);
        headers.put("x-amz-storage-class", "REDUCED_REDUNDANCY");

        utils.AWS4SignerForAuthorizationHeader signer = new utils.AWS4SignerForAuthorizationHeader(
                endpointUrl, "PUT", "s3", regionName);
        String authorization = signer.computeSignature(headers,
                null, // no query parameters
                contentHashString,
                awsAccessKey,
                awsSecretKey);

        // express authorization for this as a header
        headers.put("Authorization", authorization);

        node.put("xamzcontentsha256", contentHashString);
        node.put("contentlength", contentLength);
        node.put("xamzstorageclass", "REDUCED_REDUNDANCY");
        node.put("xamzdate", headers.get("x-amz-date"));
        node.put("host", headers.get("Host"));
        node.put("authorization", authorization);

        node.put("uuid", this.uuid.toString());
        node.put("filename", this.fileName());

        // make the call to Amazon S3
//        String response = utils.HttpUtils.invokeHttpRequest(endpointUrl, "PUT", headers, objectContent);
//        System.out.println("--------- Response content ---------");
//        System.out.println(response);
//        System.out.println("------------------------------------");

        return node;
    }
}
