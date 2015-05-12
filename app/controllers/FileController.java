package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;

import models.File;
import models.Likes;
import models.Post;
import models.User;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import plugins.S3Plugin;

import java.util.List;
import java.util.UUID;


/**
 * Created by peter on 3/1/15.
 */
public class FileController  extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getFiles(Long id, String type) {

        // get all likes for object
        List<File> files = File.getAllFiles(id, type);

        // convert to json and return
        return ok(File.toJson(files));
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getUserFiles() {

        User user = Application.getLocalUser(ctx().session());
        if(user != null) {
            // convert to json and return
            return ok(File.toJson(user.files));
        }
        else{
            return badRequest();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result authorize(String contentHash, String contentName, Long contentLength, String contentType, String comment, Long refId, String refType)
    {
        User user = Application.getLocalUser(ctx().session());
        File file = new File();
        file.length = contentLength;
        file.originalName = contentName;
        file.type = contentType;
        file.refId = refId;
        file.refType = refType;
        file.comment = comment;
        ObjectNode policy = file.authorize(S3Plugin.s3Bucket, "us-west-2", S3Plugin.accessKey, S3Plugin.secretKey, contentHash, user, contentLength);
        file.save();
        Logger.debug("policy: " + policy);
        return ok(policy);
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result verify(String uuid)
    {
        File res = File.verify(uuid);
        if(res != null){
            return ok(res.toJson());
        }
        else{
            return notFound();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result addAvatar(String uuid)
    {
        File file = File.findByUUID(UUID.fromString(uuid));
        if(file != null) {
            Logger.debug("add avatar to user");
            User user = Application.getLocalUser(ctx().session());
            user.setAvatar(file);
            user.save();
            return ok();
        }
        else{
            return notFound();
        }
    }
}
