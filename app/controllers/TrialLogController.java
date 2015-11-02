package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import models.Trial;
import models.TrialLog;
import models.User;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class TrialLogController extends Controller {

    //
    // web service for triallog
    //

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result getall(Long id) {
        User user = Application.getLocalUser(ctx().session());

        Trial item = Trial.find.byId(id);

//        if(item.user.id != user.id){
//            return forbidden();
//        }

        return ok(TrialLog.toJson(item.log));
    }

}
