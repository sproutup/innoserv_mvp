package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.db.ebean.Model.Finder;

import com.avaje.ebean.Page;

/**
 * Created by peter on 3/23/15.
 */
@Entity
public class EarlyAccessRequest extends TimeStampModel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String name;

    public String productUrl;
    
    public static Finder<Long, EarlyAccessRequest> find = new Finder<Long, EarlyAccessRequest>(
            Long.class, EarlyAccessRequest.class);
    
    public static Page<EarlyAccessRequest> find(int page) {
	    return
	            find.where()
	                .orderBy("id desc")
	                .findPagingList(1000)
	                .setFetchAhead(false)
	                .getPage(page);
	}
}
