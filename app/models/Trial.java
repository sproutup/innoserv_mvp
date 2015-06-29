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
 * Created by peter on 3/23/15.
 */
@Entity
public class Trial extends TimeStampModel {

    public static Finder<Long, Trial> find = new Finder<Long, Trial>(
            Long.class, Trial.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String name;

    public String phone;

    public String address;

    public String reason;

    public boolean active = true;

    @ManyToOne
    public Product product;

    @OneToMany
    public List<Content> content;

    @ManyToOne
    public User user;

    public static Trial findById(Long id) {
        return find.byId(id);
    }

    public static Page<Trial> find(int page) {
	    return
            find.where()
                .orderBy("id desc")
                .findPagingList(100)
                .setFetchAhead(false)
                .getPage(page);
	}

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("name", this.name);
        node.put("email", this.email);
        node.put("active", this.active);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
        if(this.user != null) {
            node.put("user", this.user.toJsonShort());
        }
        if(this.product != null) {
            node.put("product", this.product.toJsonShort());
        }
        if(this.content != null) {
            node.put("content", Content.toJson(this.content));
        }
        return node;
    }

    public static ArrayNode toJson(List<Trial> items){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Trial item : items){
            arrayNode.add(item.toJson());
        }
        return arrayNode;
    }
}