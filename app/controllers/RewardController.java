package controllers;

import models.Post;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by peter on 3/19/15.
 */
public class RewardController extends Controller {

    /*
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAll()
    {
        return getRange(0);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProductRange(String slug, int start)
    {
        return ok(Post.range(slug, start, start+9 ));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getUserRange(String nickname, int start)
    {
        return ok(Post.userRange(nickname, start, start+9 ));
    }
    */
}
