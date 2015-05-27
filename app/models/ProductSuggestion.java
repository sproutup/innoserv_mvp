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
public class ProductSuggestion extends TimeStampModel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String productName;

    public String productUrl;
    
    public static Finder<Long, ProductSuggestion> find = new Finder<Long, ProductSuggestion>(
            Long.class, ProductSuggestion.class);
    
    public static Page<ProductSuggestion> find(int page) {
	    return
	            find.where()
	                .orderBy("id desc")
	                .findPagingList(100)
	                .setFetchAhead(false)
	                .getPage(page);
	}
}
