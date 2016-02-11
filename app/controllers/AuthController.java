package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

import com.feth.play.module.pa.user.AuthUser;
import models.RewardEvent;
import models.User;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.Play;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyLoginUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider;

/**
 * Created by peter on 2/13/15.
 */
public class AuthController extends Controller {
    private static String url = Play.application().configuration().getString("analytics.api.url");


    @BodyParser.Of(BodyParser.Json.class)
    public static Result login(){
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Missing request body");
        } else {// Everything was filled
            com.feth.play.module.pa.controllers.Authenticate.noCache(response());
            final Form<MyUsernamePasswordAuthProvider.MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bind(json, "email", "password");
            if (filledForm.hasErrors()) {
                // User did not fill everything properly
                return badRequest(filledForm.errorsAsJson());
            }
            else{
                return UsernamePasswordAuthProvider.handleLogin(ctx());
            }
        }
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result signup(){
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Missing request body");
        } else {
            String name = json.findPath("name").textValue();
            Logger.debug("name: " + name);

	        // signup user and return result
	        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
            final Form<MyUsernamePasswordAuthProvider.MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bind(json, "name", "email", "password", "repeatPassword");

            if (filledForm.hasErrors()) {
                // User did not fill everything properly
                return badRequest(filledForm.errorsAsJson());
            }
            else{
                // perform signup
                MyUsernamePasswordAuthProvider.context = ctx();
                return UsernamePasswordAuthProvider.handleSignup(ctx());
            }
		}
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result provider(String provider){
        JsonNode json = request().body().asJson();
        if(json != null) {
            String url = json.findPath("originalUrl").textValue();
            if(url != null) {
                Logger.debug("url: " + url);
                return ok(com.feth.play.module.pa.controllers.Authenticate.authenticateJson(provider, url));
            }
        }
        return badRequest("Missing request body");
    }


    @SubjectPresent
    public static Result validate(String username){
        boolean res = false;
        User user = Application.getLocalUser(ctx().session());

        ObjectNode node = Json.newObject();
        if(user.nickname.equalsIgnoreCase(username.trim())){
            node.put("unique", true);
            node.put("mine", true);
            return ok(node);
        }

        if(User.findByNickname(username) == null) {
            node.put("unique", true);
            return ok(node);
        }
        else{
            node.put("unique", false);
            return ok(node);
        }
    }

    @SubjectPresent
    public static Result points(){
        User user = Application.getLocalUser(ctx().session());
        ObjectNode node = Json.newObject();
        node.put("points", user.points());
        node.put("events", RewardEvent.toJson(RewardEvent.getLatest(user.id)));
        return ok(node);
    }

    public static F.Promise<Result> videos(Long user_id) {
        WSRequestHolder holder = WS.url(url + "/user/" + user_id + "/youtube/video");

        final F.Promise<Result> resultPromise = holder.get().map(
                new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.asJson());
                    }
                }
        );
        return resultPromise;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result logout(){

        //todo capture user from session and logout
        com.feth.play.module.pa.controllers.Authenticate.logout();

        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result user(){
        User localUser = Application.getLocalUser(session());
        if(localUser != null) {
            return ok(localUser.toJson());
        }
        else {
            return noContent();
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


    public static Result afterLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        return badRequest(MyUsernamePasswordAuthProvider.LOGIN_FORM.errorsAsJson());
    }

//    @BodyParser.Of(BodyParser.Json.class)
    public static Result afterAuth() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final AuthUser currentUser = PlayAuthenticate.getUser(ctx().session());

        ObjectNode node = Json.newObject();
        node.put("status", "ok");
        node.put("id", currentUser.getId());
        Logger.debug("provider: " +  currentUser.getProvider());
        node.put("provider", currentUser.getProvider());

        return ok(node);
    }
}
