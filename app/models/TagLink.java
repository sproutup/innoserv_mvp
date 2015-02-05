package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by peter on 2/4/15.
 */
@Entity
public class TagLink extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public Tag tag;

    public Long refId;

    public String refType;

    public static Finder<Long, TagLink> find = new Finder<Long, TagLink>(Long.class,
            TagLink.class);

    public TagLink() {
        // Left empty
    }

}
