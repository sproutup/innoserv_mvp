package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.hibernate.validator.constraints.URL;
import play.Logger;
import play.api.libs.json.DeserializerContext;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;
import play.libs.F;

import javax.persistence.*;

import com.avaje.ebean.Page;
import utils.Slugify;
import utils.Taggable;

import java.io.IOException;
import java.lang.Boolean;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@JsonSerialize(using = Product.ProductSerializer.class)
public class Product extends Model implements PathBindable<Product>,
		QueryStringBindable<Product>, Taggable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	public String productEAN;
	public String productName;
	@Column(unique=true)
	public String slug;
	public String productDescription;
	public String productLongDescription;
	public String featureList;
	@Column(columnDefinition = "TEXT")
	public String missionStatement;
	@Column(columnDefinition = "TEXT")
	public String productStory;

	public String urlHome;
	public String urlFacebook;
	public String urlTwitter;

	public boolean isFeatured;

	@OneToMany(mappedBy="product")
	public List<Post> postItems;

	@OneToMany(mappedBy="product")
	@OrderBy("dateTimeStamp desc")
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

	@Override
	public void save() {
		this.slugify();
		super.save();
	}

	@Override
	public void update(Object o) {
		this.slugify();
		super.update(o);
	}

	public void slugify() {
		// update slug every time product is saved
		try {

			slug = new Slugify().slugify(productName);

			//prevent duplicates
			// ToDo
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Product findbyProductEAN(String productEAN) {
		return find.where().eq("productEAN", productEAN).findUnique();
	}

	public Product findbySlug(String value) {
		return find.where().eq("slug", value).findUnique();
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

	public static Page<Product> find(int page) {
		    return
		            find.where()
		                .orderBy("id asc")
		                .findPagingList(10)
		                .setFetchAhead(false)
		                .getPage(page);
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
	Taggable interface implementation
	 */
	@Override
	public void addTag(String name) {
		Tag.addTag(name, id, this.getClass().getName());
	}
	@Override
	public void removeTag(String name) {
		Tag.removeTag(name, id, this.getClass().getName());
	}
	@Override
	public void removeAllTags() {
		Tag.removeAllTags(id, this.getClass().getName());
	}
	@Override
	public List<Tag> getAllTags() {
		return Tag.getAllTags(id, this.getClass().getName());
	}
	public List<Product> findAllByTag(String name) {

		List<Product> products = Product.find
				.query()
				.setRawSql(Tag.findAllByTagRawSql(name, this.getClass().getName()))
				.where()
				.findList();

		return products;
	}

	static class ProductSerializer extends JsonSerializer<Product> {
		@Override
		public void serialize(Product product, JsonGenerator generator, SerializerProvider provider)
				throws IOException {
			generator.writeStartObject();
			generator.writeObjectField("id", product.id);
			generator.writeStringField("productEAN", product.productEAN);
			generator.writeStringField("productName", product.productName);
			generator.writeStringField("slug", product.slug);
			generator.writeStringField("productDescription", product.productDescription);
			generator.writeStringField("productLongDescription", product.productLongDescription);
			generator.writeStringField("featureList", product.featureList);
			generator.writeStringField("missionStatement", product.missionStatement);
			generator.writeStringField("productStory", product.productStory);
			generator.writeStringField("urlHome", product.urlHome);
			generator.writeStringField("urlFacebook", product.urlFacebook);
			generator.writeStringField("urlTwitter", product.urlTwitter);
			generator.writeBooleanField("isFeatured", product.isFeatured);
			generator.writeEndObject();
		}
	}
}
