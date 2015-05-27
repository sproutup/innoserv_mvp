package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Product;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by peter on 3/19/15.
 */
public class UserController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getUser(String nickname)
    {
        User user = new User().findByNickname(nickname);
        return user == null ? notFound("User not found [" + nickname + "]") : ok(user.toJson());
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result updateUser(Long id)
    {
        User user = Application.getLocalUser(ctx().session());
        if(user != null) {
            JsonNode root = request().body().asJson();

            Logger.debug("user controller > update");
            JsonNode firstname = root.path("firstname");
            if (!firstname.isMissingNode()) {
                Logger.debug("user controller > firstname > " + firstname.asText());
                user.firstName = firstname.asText();
            }
            JsonNode lastname = root.path("lastname");
            if (!lastname.isMissingNode()) {
                Logger.debug("user controller > lastname > " + lastname.asText());
                user.lastName = lastname.asText();
            }
            JsonNode name = root.path("name");
            if (!name.isMissingNode()) {
                Logger.debug("user controller > name > " + name.asText());
                user.name = name.asText();
            }
            if(root.has("nickname")){
                user.nickname = root.path("nickname").asText();
            }
            JsonNode desc = root.path("description");
            if (!desc.isMissingNode()) {
                Logger.debug("user controller > description > " + desc.asText());
                user.description = desc.asText();
            }
            JsonNode urlFacebook = root.path("urlFacebook");
            if (!urlFacebook.isMissingNode()) {
                user.urlFacebook = urlFacebook.asText();
            }
            JsonNode urlTwitter = root.path("urlTwitter");
            if (!urlTwitter.isMissingNode()) {
                user.urlTwitter = urlTwitter.asText();
            }
            JsonNode urlPinterest = root.path("urlPinterest");
            if (!urlPinterest.isMissingNode()) {
                user.urlPinterest = urlPinterest.asText();
            }
            JsonNode urlBlog = root.path("urlBlog");
            if (!urlBlog.isMissingNode()) {
                user.urlBlog = urlBlog.asText();
            }
            user.save();
            return ok(user.toJson());
        }
        else{
            return badRequest("Not authorized");
        }
    }

}
