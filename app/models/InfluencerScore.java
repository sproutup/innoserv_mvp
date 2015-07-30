package models;

import javax.persistence.*;

import play.libs.Json;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Created by Apurv on 7/6/15.
 */
@Entity
public class InfluencerScore extends TimeStampModel {

    public static Finder<Long, InfluencerScore> find = new Finder<>(
            Long.class, InfluencerScore.class);

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @OneToOne
    public User user;

    public Long twitterFollowers;
    public Long facebookFollowers;
    public Long blogFollowers;
    public Long youtubeSubs;
    public Double kloutScore;

    public Long influencerScore;

    public static Page<InfluencerScore> find(int page) {
        return
                find.where()
                        .orderBy("id desc")
                        .findPagingList(100)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    /**
     * Makes and returns JSON Object for any given row in table
     * @return JSON ObjectNode for the row on which this method is called
     */
    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("userId", this.user.id);
        node.put("twitterFollowers", this.twitterFollowers);
        node.put("facebookFollowers", this.facebookFollowers);
        node.put("blogFollowers", this.blogFollowers);
        node.put("youtubeSubs", this.youtubeSubs);
        node.put("kloutScore", this.kloutScore);
        node.put("influencerScore", this.influencerScore);
        return node;
    }
}
