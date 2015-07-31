package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
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

    @OneToOne
    public Content content;

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

    /**
     * Makes and returns JSON Object for any given row in table
     * @return JSON ObjectNode for the row on which this method is called
     */
    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("contentId", this.content.id);
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
