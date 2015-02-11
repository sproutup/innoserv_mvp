package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.libs.Json;
import scala.collection.immutable.StreamViewLike;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import play.Logger;

/**
 * Post class
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Post extends SuperModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, Post> find = new Finder<Long, Post>(
            Long.class, Post.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;

    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    public List<Post> comments;

    @ManyToOne
    public Post parent;

    @ManyToOne
    public User user;

    public static Post findById(Long id) {
        return find.byId(id);
    }

    private ObjectNode toJsonRaw(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("title", this.title);
        node.put("content", this.content);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        return node;
    }

    public ObjectNode toJson(){
        ObjectNode node = toJsonRaw();
        if(this.comments.size()>0) {
            ArrayNode list = node.putArray("comments");
            for (Post comment : this.comments) {
                list.add(comment.toJsonRaw());
            }
        }
        return node;
    }

    public static ArrayNode toJson(List<Post> posts){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Post post : posts){
            arrayNode.add(post.toJson());
        }
        return arrayNode;
    }

    public List<Post> getAll() {
        return find.where().order("id desc").findList();
    }

}
