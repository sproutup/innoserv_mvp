package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Content;
import models.OpenGraph;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.commons.lang.StringUtils.*;

/**
 * Created by Apurv on 7/30/15.
 */
public class OpenGraphController extends Controller {

    public static Result getAll() {
        List<OpenGraph> openGraphs = OpenGraph
                .find
                .orderBy("id asc")
                .findList();
        return ok(OpenGraph.toJson(openGraphs));
    }

    public static Result get(Long id) {
        OpenGraph openGraph = OpenGraph.find.byId(id);
        return openGraph == null ? notFound("OpenGraph not found [" + id + "]") : ok(openGraph.toJson());
    }

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

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        try {
            JsonNode json = request().body().asJson();
            if (json == null) {
                return badRequest("Expected JSON data");
            }

            String url;
            if (check(json, "url")) {
                url = json.findPath("url").asText();
            }
            else {
                return badRequest("Missing parameter [url]");
            }

            Logger.debug("scrape url: " + url);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.sproutup.co")
                    .timeout(3000)
                    .get();

            OpenGraph openGraph = new OpenGraph();

            if (doc.select("meta[property=og:title]").first() != null) {
                openGraph.title = StringUtils.substring(doc.select("meta[property=og:title]").first().attr("content").trim(), 0, 255);
            }
            else if (doc.title() != null) {
                openGraph.title = StringUtils.substring(doc.title().trim(), 0, 255);
            }
            else {
                openGraph.title = url;
            }
            Logger.debug("scrape title: " + openGraph.title);

            if(doc.select("meta[property=og:description]").first() != null) {
                openGraph.description = StringUtils.substring(doc.select("meta[property=og:description]").first().attr("content").trim(), 0, 255);
            }
            else if (doc.select("meta[name=description]").first() != null) {
                openGraph.description = StringUtils.substring(doc.select("meta[name=description]").first().attr("content").trim(), 0, 255);
            }
            Logger.debug("scrape desc: " + openGraph.description);

            if(doc.select("meta[property=og:image]").first() != null){
                openGraph.image = doc.select("meta[property=og:image]").first().attr("content").trim();
            }
//            else if (doc.select("").first() != null) {
//
//            }

            //[href*=/path/]

            Element ogType = doc.select("meta[property=og:type]").first();
            Element ogUrl = doc.select("meta[property=og:url]").first();
            Element ogSiteName = doc.select("meta[property=og:site_name]").first();
            Element ogVideo = doc.select("meta[property=og:video]").first();

            if (ogType != null) openGraph.type = ogType.attr("content").trim();
            if (ogUrl != null) openGraph.url = ogUrl.attr("content").trim();
            if (ogSiteName != null) openGraph.siteName = StringUtils.substring(ogSiteName.attr("content").trim(), 0, 255);
            if (ogVideo != null) openGraph.video = ogVideo.attr("content").trim();

            openGraph.save();
            return ok(openGraph.toJson());
        } catch (UnknownHostException e){
            Logger.debug(e.getMessage());
            return badRequest(e.getMessage());
        } catch (HttpStatusException e) {
            Logger.debug(e.getMessage());
            return badRequest(e.getMessage());
        }catch (IOException e) {
            e.printStackTrace();
            return badRequest("Bad url given");
        }
    }

    public static Result delete(Long id) {
        OpenGraph openGraph = OpenGraph.find.byId(id);
        if (openGraph != null) {
            openGraph.delete();
            return ok();
        }
        else {
            return notFound("OpenGraph not found");
        }
    }

    private static boolean check(JsonNode root, String path){
        JsonNode node = root.path(path);
        return !node.isMissingNode() && !node.isNull();
    }
}
