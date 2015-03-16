package models;

import com.amazonaws.services.elastictranscoder.model.Job;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;
import plugins.S3Plugin;
import utils.ElasticTranscoder;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by peter on 3/3/15.
 */
@Entity
public class File extends SuperModel {
    private static ElasticTranscoder elasticTranscoder;

    public static Finder<Long, File> find = new Model.Finder<Long, File>(Long.class, File.class);

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

    @Column(columnDefinition = "boolean default false")
    public boolean isTranscoded;

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

    private void transcode() {
        if(type.contains("video/")) {
            String INPUT_KEY = fileName();
            String OUTPUT_KEY = this.uuid.toString();
            String OUTPUT_KEY_PREFIX = this.uuid.toString() + "/";
            Job job = elasticTranscoder.createElasticTranscoderHlsJob(INPUT_KEY, OUTPUT_KEY, OUTPUT_KEY_PREFIX);
            Logger.info("  INPUT_KEY: " + INPUT_KEY);
            Logger.info("  OUTPUT_KEY_PREFIX: " + OUTPUT_KEY_PREFIX);
            Logger.info("  HLS Transcoder job has been created: ");
            Logger.info( job + "\n");
            Logger.info("==========================================================");
        }
    }

    /*
    Get all files on an object identified by refId and refType
    */
    public static List<File> getAllFiles(Long refId, String refType) {

        // find all likes
        List<File> files = File.find.where()
                .eq("refId", refId)
                .eq("refType", refType)
                .findList();

        return files;
    }

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("filename", this.fileName());
        node.put("type", this.type);
        ObjectNode urlnode = Json.newObject();
        if(type.contains("video/")) {
            urlnode.put("mpeg", this.url().concat(".m3u8"));
            urlnode.put("webm", this.url().concat(".webm"));
            urlnode.put("mp4", this.url().concat(".mp4"));
        }
        else{
            urlnode.put("image", this.url());
        }
        node.put("url", urlnode);
        node.put("comment", this.comment);
        node.put("username", this.user.name);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("isVerified", this.verified);
        node.put("isTranscoded", this.isTranscoded);

        // add likes to the node
        List<Likes> likes = this.getAllLikes();
        if(likes.size()>0){
            node.put("likes", Likes.toJson(likes));
        }

        return node;
    }

    public static ArrayNode toJson(List<File> files){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (File file : files){
            arrayNode.add(file.toJson());
        }
        return arrayNode;
    }

    public static File verify(String id){
        File file = File.findByUUID(UUID.fromString(id));
        if(file != null) {
            file.verified = true;
            file.save();
            file.transcode();
            return file;
        }
        else {
            return null;
        }
    }

    public static boolean transcodeCompleted(String id){
        Logger.debug("file - transcode completed - " + id);
        File file = File.findByUUID(UUID.fromString(id));
        if(file != null) {
            Logger.debug("file - transcode updated - " + id);
            file.isTranscoded = true;
            file.save();
            return true;
        }
        else {
            Logger.debug("file - transcode not found - " + id);
            return false;
        }
    }

    public String fileName() {
        if (type.contains("video/")) {
            return (uuid + "_" + user.id);
        } else if (type.contains("image/")) {
            return (uuid + "_" + user.id + ".jpg");
        } else{
            return (uuid + "_" + user.id + ".jpg");
        }
    }

    public String url() {
        if (type.contains("video/")) {
            return ("http://dc2jx5ot5judg.cloudfront.net/" + this.uuid.toString() + "/" + this.uuid.toString() );
        } else if (type.contains("image/")) {
            return ("http://d2ggucmtk9u4go.cloudfront.net" + "/" + this.fileName());
        } else{
            return ("http://d2ggucmtk9u4go.cloudfront.net" + "/" + this.fileName());
        }
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
