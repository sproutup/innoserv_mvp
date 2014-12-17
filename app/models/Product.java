package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;
import play.libs.F;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.avaje.ebean.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
public class Product extends Model implements PathBindable<Product>,
		QueryStringBindable<Product> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	public String productID;
	public String productName;
	public String productDescription;
	public String productLongDescription;
	
	private List<Product> products;

	@ManyToMany(cascade=CascadeType.ALL)
	public List<Tag> tags = new LinkedList<Tag>();

	@OneToMany(mappedBy="product")
	public List<Feedback> feedbackItems;

	@OneToMany(mappedBy="product")
	public List<Media> mediaItems;
	
	public static Finder<Long, Product> find = new Finder<Long, Product>(
			Long.class, Product.class);

	public Product() {
		//initialize();
	}

	private Product(String productID, String productName,
			String productShortDescription) {

		this.productID = productID;
		this.productDescription = productShortDescription;
		this.productName = productName;

	}
  
	public List<Product> getAll() {
		return find.all();
	}

	public Product findbyProductID(String productID) {
		return find.where().eq("product_id", this.productID).findUnique();
	}

	public List<Product> findbyProductName(String productName) {
		final List<Product> results = new ArrayList<Product>();
		for (Product prod : products) {
			if (prod.productName.toLowerCase().contains(productName)) {
				results.add(prod);
			}
		}
		return results;
	}
	
	public Page<Product> find(int page) {
		    return 
		            find.where()
		                .orderBy("id asc")
		                .findPagingList(10)
		                .setFetchAhead(false)
		                .getPage(page);
		  }

	public boolean remove(Product product) {
		return products.remove(product);
		// Todo remove from the db too
	}

	public void add() {
		products.remove(findbyProductID(this.productID));
		products.add(this);
	}
	
	/*
	 * Look in the db for a product with a productID equal
	 * to the one passed in the URL
	 */
	@Override
	  public Product bind(String key, String value) {
	    return findbyProductID(value);
	  }

	
	@Override
	public F.Option<Product> bind(String key, Map<String, String[]> data) {
	   return F.Option.Some(findbyProductID(data.get("productID")[0]));
	}

	/*
	 * Unbinding
	 * return our raw value
	 */
	@Override
	public String unbind(String s) {
		return this.productID;
	}

	/*
	 * Unbinding javascript
	 * return our raw value
	 */
	@Override
	public String javascriptUnbind() {
		return this.productID;
	}

	/*
	private void initialize() {
		products = new ArrayList<Product>();
		products.add(new Product("11111", "Belleds",
				"Smart audio and lighting platform"));
		products.add(new Product("22222", "Soma Water",
				"Beautifully innovative all-natural water filters"));
		products.add(new Product("33333", "The Roost",
				"Stop hunching over your laptop"));
		products.add(new Product("44444", "Miselu C.25",
				"The Music Keyboard for iPad"));
	}*/

}
