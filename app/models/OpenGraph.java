package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Apurv on 7/30/15.
 */
@Entity
public class OpenGraph extends TimeStampModel {

    public static Model.Finder<Long, OpenGraph> find = new Model.Finder<>(
            Long.class, OpenGraph.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String title;
    public String type;
    public String image;
    public String url;

    public String description;
    public String siteName;
    public String video;

    public static Page<OpenGraph> find(int page) {
        return
            find.where()
                    .orderBy("id desc")
                    .findPagingList(1000)
                    .setFetchAhead(false)
                    .getPage(page);
    }

    public boolean scrape(String url){
        Logger.debug("scrape url: " + url);
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.sproutup.co")
                .timeout(3000)
                .get();
            scrapeTitle(doc);
            scrapeDescription(doc);
            scrapeImage(doc);
            scrapeVideo(doc);
            return true;
        } catch (UnknownHostException e){
            Logger.debug(e.getMessage());
            return false;
        } catch (HttpStatusException e) {
            Logger.debug(e.getMessage());
            return false;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean scrapeTitle(Document doc){
        if (doc.select("meta[property=og:title]").first() != null) {
            title = StringUtils.substring(doc.select("meta[property=og:title]").first().attr("content").trim(), 0, 255);
            return true;
        }
        else if (doc.title() != null) {
            title = StringUtils.substring(doc.title().trim(), 0, 255);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean scrapeDescription(Document doc){
        if(doc.select("meta[property=og:description]").first() != null) {
            description = StringUtils.substring(doc.select("meta[property=og:description]").first().attr("content").trim(), 0, 255);
            return true;
        }
        else if (doc.select("meta[name=description]").first() != null) {
            description = StringUtils.substring(doc.select("meta[name=description]").first().attr("content").trim(), 0, 255);
            return true;
        }
        return false;
    }

    public boolean scrapeImage(Document doc){
        if(doc.select("meta[property=og:image]").first() != null){
            image = doc.select("meta[property=og:image]").first().attr("content").trim();
            return true;
        }
        return false;
    }

    public boolean scrapeVideo(Document doc){
        if(doc.select("meta[property=og:video]").first() != null){
            video = doc.select("meta[property=og:video]").first().attr("content").trim();
            return true;
        }
        return false;
    }

    /**
     * Makes and returns JSON Object for any given row in table
     * @return JSON ObjectNode for the row on which this method is called
     */
    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("title", this.title);
        node.put("type", this.type);
        node.put("image", this.image);
        node.put("url", this.url);
        node.put("description", this.description);
        node.put("siteName", this.siteName);
        node.put("video", this.video);
        return node;
    }

    /**
     * Makes and returns a JSON ArrayNode with all the OpenGraphs provided
     * @param openGraphs List of OpenGraph objects
     * @return openGraphs in JSON ArrayNode
     */
    public static ArrayNode toJson(List<OpenGraph> openGraphs){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (OpenGraph openGraph : openGraphs){
            arrayNode.add(openGraph.toJson());
        }
        return arrayNode;
    }
}
