package models;

import javax.persistence.*;

import com.avaje.ebean.Page;

/**
 * Created by peter on 3/23/15.
 */
@Entity
public class ProductTrial extends TimeStampModel {

    public static Finder<Long, ProductTrial> find = new Finder<Long, ProductTrial>(
            Long.class, ProductTrial.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String name;

    public boolean active = true;

    @ManyToOne
    public Product product;

    @ManyToOne
    public User user;
    
	public static Page<ProductTrial> find(int page) {
	    return
	            find.where()
	                .orderBy("id asc")
	                .findPagingList(100)
	                .setFetchAhead(false)
	                .getPage(page);
	}
    
}
