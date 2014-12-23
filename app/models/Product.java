package models;

import play.Logger;
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

	public String productEAN;
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

	private Product(String productEAN, String productName,
			String productShortDescription) {

		this.productEAN = productEAN;
		this.productDescription = productShortDescription;
		this.productName = productName;

	}

	public List<Product> getAll() {
		return find.all();
	}

	public Product findbyID(Long id) {
		return find.byId(id);
	}

	public Product findbyProductEAN(String productEAN) {
		return find.where().eq("productEAN", productEAN).findUnique();
	}

	public List<Product> findbyProductName(String productName) {
		List<Product> results;
		 return results = find.where().eq("productName", productName).findList();
	}

	public Product getDetailwithMedia(Long id) {
		Product prod = find.byId(id);
		if (prod!=null){
			if (prod.mediaItems!=null){
				Logger.debug("product media Items before query are "+ prod.mediaItems.size());
			} else {
				Logger.debug("product media Items before query are null");
			}
			//retrieve media associated with this product
			//prod.mediaItems = new Media().findMediabyProductID(id);
		}
		if (prod.mediaItems!=null){
			Logger.debug("product media Items after query are "+ prod.mediaItems.size());
		} else {
			Logger.debug("product media Items after query are null");
		}

		return prod;
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
		products.remove(findbyProductEAN(this.productEAN));
		products.add(this);
	}

	/*
	 * Look in the db for a product with a productID equal
	 * to the one passed in the URL
	 */
	@Override
	  public Product bind(String key, String value) {
	    return findbyProductEAN(value);
	  }


	@Override
	public F.Option<Product> bind(String key, Map<String, String[]> data) {
	   return F.Option.Some(findbyProductEAN(data.get("productEAN")[0]));
	}

	/*
	 * Unbinding
	 * return our raw value
	 */
	@Override
	public String unbind(String s) {
		return this.productEAN;
	}

	/*
	 * Unbinding javascript
	 * return our raw value
	 */
	@Override
	public String javascriptUnbind() {
		return this.productEAN;
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
