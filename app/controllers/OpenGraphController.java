package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Content;
import models.OpenGraph;

import org.apache.commons.lang3.StringUtils;
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
            if (json == null) { return badRequest("Expected JSON data"); }
            String url;
            if (check(json, "url")) { url = json.findPath("url").asText(); }
            else { return badRequest("Missing parameter [url]"); }

            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
            OpenGraph openGraph = new OpenGraph();

            Element ogTitle = doc.select("meta[property=og:title]").first();
            Element ogType = doc.select("meta[property=og:type]").first();
            Element ogImage = doc.select("meta[property=og:image]").first();
            Element ogUrl = doc.select("meta[property=og:url]").first();
            Element ogDescription = doc.select("meta[property=og:description]").first();
            Element ogSiteName = doc.select("meta[property=og:site_name]").first();
            Element ogVideo = doc.select("meta[property=og:video]").first();

            if (ogTitle != null) openGraph.title = StringUtils.substring(ogTitle.attr("content").trim(), 0, 255);
            if (ogType != null) openGraph.type = ogType.attr("content").trim();
            if (ogImage != null) openGraph.image = ogImage.attr("content").trim();
            if (ogUrl != null) openGraph.url = ogUrl.attr("content").trim();
            if (ogDescription != null) openGraph.description = StringUtils.substring(ogDescription.attr("content").trim(), 0, 255);
            if (ogSiteName != null) openGraph.siteName = StringUtils.substring(ogSiteName.attr("content").trim(), 0, 255);
            if (ogVideo != null) openGraph.video = ogVideo.attr("content").trim();

            openGraph.save();
            return ok(openGraph.toJson());
        } catch (UnknownHostException e){
            Logger.debug(e.getMessage());
            return badRequest(e.getMessage());
        } catch (IOException e) {
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
