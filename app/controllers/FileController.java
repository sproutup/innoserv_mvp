package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.File;
import models.Likes;
import models.Post;
import models.User;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


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
        ObjectNode policy = file.authorize("sproutup-test-upload", "us-west-2", "AKIAJM5X5NV444LJEUSA", "UHpVP/axa3eOmfCOcSQFGXwK4fzYMzHV8aYkh38X", contentHash, user, contentLength);
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
}
