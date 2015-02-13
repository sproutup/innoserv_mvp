package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by peter on 2/13/15.
 */
public class AuthController extends Controller {


    @BodyParser.Of(BodyParser.Json.class)
    public static Result login(){
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Missing request body");
        } else {
            String email = json.findPath("email").textValue();
            String password = json.findPath("password").textValue();
        }

        // todo

        return ok();
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result signup(){
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Missing request body");
        } else {
            String fullname = json.findPath("fullname").textValue();
            String email = json.findPath("email").textValue();
            String password = json.findPath("password").textValue();
        }

        // todo signup user and return result

        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result logout(){

        //todo capture user from session and logout

        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result user(){
        User localUser = Application.getLocalUser(session());
        if(localUser != null){
            return ok(localUser.toJson());
        }
        else {
            return badRequest();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result forgotPassword(){
        // todo
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result changePassword(){
        // todo
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result requestVerifyEmail(){
        // todo
        return ok();
    }
}
