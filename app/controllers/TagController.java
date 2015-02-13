package controllers;

import models.Post;
import models.Tag;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by peter on 2/13/15.
 */
public class TagController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getTopTags(int size)
    {
        return ok(Tag.toJson(Tag.getTopTags(size)));
    }

}
