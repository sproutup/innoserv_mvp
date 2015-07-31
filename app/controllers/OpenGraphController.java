package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by Apurv on 7/30/15.
 */
public class OpenGraphController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result parse(String url) {
        try {
            ObjectNode res = Json.newObject();
            Document doc = Jsoup.connect(url).get();

            for(Element meta : doc.select("meta")) {
                String metaName = meta.attr("property");
                String metaContent = meta.attr("content");
                if (metaName != null && metaName.startsWith("og:")
                        && metaContent != null) {
                    metaName = metaName.substring(3).replace(':', '_');
                    res.put(metaName, metaContent);
                }
            }
            return ok(res);
        } catch (IOException e) {
            e.printStackTrace();
            return badRequest("Bad url given");
        }
    }
}
