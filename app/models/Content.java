package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

/**
 * Created by peter on 6/26/15.
 */
@Entity
public class Content extends SuperModel {

    public static Finder<Long, Content> find = new Finder<Long, Content>(
            Long.class, Content.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String url;

    @ManyToOne
    public ProductTrial productTrial;

    @ManyToOne
    public Trial trial;

    @ManyToOne
    public Product product;

    @ManyToOne
    public User user;

    @OneToOne
    public OpenGraph openGraph;

    public static Page<Content> find(int page) {
        return
            find.where()
                .orderBy("id desc")
                .findPagingList(1000)
                .setFetchAhead(false)
                .getPage(page);
    }

    public ObjectNode toFullJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("url", this.url);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        if(this.product != null) {
            node.put("product", this.product.toJsonShort());
        }
        if(this.openGraph != null) {
            node.put("openGraph", this.openGraph.toJson());
        }
        return node;
    }

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("url", this.url);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
//        if(this.user != null) {
//            node.put("user", this.user.toJsonShort());
//        }
//        if(this.product != null) {
//            node.put("product", this.product.toJsonShort());
//        }
        if(this.openGraph != null) {
            node.put("openGraph", this.openGraph.toJson());
        }
        return node;
    }

    public static ArrayNode toJson(List<Content> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Content item : items){
            arrayNode.add(item.toJson());
        }
        return arrayNode;
    }
}
