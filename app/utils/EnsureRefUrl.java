package utils;

import models.Trial;
import play.Play;

import java.util.List;

/**
 * Created by Apurv on 7/23/15.
 */
public class EnsureRefUrl {
    public static void genMissingUrls() {
        if (Play.application().configuration().getString("google.urlshortener.enabled") == null ||
                Boolean.parseBoolean(Play.application().configuration().getString("google.urlshortener.enabled"))) {
            List<Trial> missingUrls = Trial.find.where().isNull("refURL").findList();
            for (int i = 0; i < missingUrls.size(); i++) {
                missingUrls.get(i).generateRefUrl();
                missingUrls.get(i).update();
            }
        }
    }
}
