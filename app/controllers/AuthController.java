package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

import models.User;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;

/**
 * Created by peter on 2/13/15.
 */
public class AuthController extends Controller {


    @BodyParser.Of(BodyParser.Json.class)
    public static Result login(){
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Missing request body");
        } else {// Everything was filled
//            String email = json.findPath("email").textValue();
//            String password = json.findPath("password").textValue();
        
            com.feth.play.module.pa.controllers.Authenticate.noCache(response());
    		MyUsernamePasswordAuthProvider.LOGIN_FORM.bind(json, "email", "password");
    		return UsernamePasswordAuthProvider.handleLogin(ctx());
        }

    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result signup(){
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Missing request body");
        } else {
           // String fullname = json.findPath("fullname").textValue();
            //String email = json.findPath("email").textValue();
            //String password = json.findPath("password").textValue();

	        // signup user and return result
	        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
	        MyUsernamePasswordAuthProvider.SIGNUP_FORM.bind(json,"name","email","password", "repeatpassword");

	        MyUsernamePasswordAuthProvider.context = ctx();
			// perform signup
			return UsernamePasswordAuthProvider.handleSignup(ctx());
			
		}
        
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
