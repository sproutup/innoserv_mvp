package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;
import play.libs.F;

import javax.persistence.*;

import com.avaje.ebean.Page;

import utils.Slugify;
import utils.Taggable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Entity
public class Product extends SuperModel implements PathBindable<Product>,
		QueryStringBindable<Product>, Taggable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	public Company company;

	public String productEAN;
	public String productName;
	@Column(unique=true)
	public String slug;
	public String productDescription;

	@Column(columnDefinition = "TEXT")
	public String productLongDescription;
	
	@Column(columnDefinition = "TEXT")
	public String featureList;
	
	@Column(columnDefinition = "TEXT")
	public String missionStatement;
	
	@Column(columnDefinition = "TEXT")
	public String productStory;

	public String urlHome;
	public String urlFacebook;
	public String urlTwitter;
	public String urlCrowdFundingCampaign;
	public String contactEmailAddress;

	public boolean isFeatured;
	@Column(nullable=false, columnDefinition="boolean default true")
	public boolean activeFlag;
	
	public boolean trialSignUpFlag;
	public boolean buyFlag;

	@OneToOne(cascade=CascadeType.ALL, mappedBy="product")
	public ProductAdditionalDetail productAdditionalDetail;
	
	@OneToMany(mappedBy="product")
	public List<Post> postItems;

	@OneToMany(mappedBy="product")
	@OrderBy("dateTimeStamp desc")
	public List<Media> mediaItems;

	@Transient
	public String tags; //comma separated values of tags
	
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

	public static Product findbyID(Long id) {
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
	
	public void update() {
		this.slugify();
		super.update();
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

	public List<Product> findbyCompanyID(Long companyID) {
		List<Product> results;
		 return results = find.where().eq("company_id", companyID).findList();
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
	 * Look in the db for a product with a product ID equal
	 * to the one passed in the URL
	 */
	@Override
	  public Product bind(String key, String value) {
	    return findbySlug(value);
	  }


	@Override
	public F.Option<Product> bind(String key, Map<String, String[]> data) {
	   return F.Option.Some(findbySlug(data.get("slug")[0]));
	}

	/*
	 * Unbinding
	 * return our raw value
	 */
	@Override
	public String unbind(String s) {
		return this.slug;
	}

	/*
	 * Unbinding javascript
	 * return our raw value
	 */
	@Override
	public String javascriptUnbind() {
		return this.slug;
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

	public void setProductAdditionalDetail(
			ProductAdditionalDetail productAdditionalDetail) {
		this.productAdditionalDetail = productAdditionalDetail;
		if (productAdditionalDetail != null) {
			productAdditionalDetail.setProduct(this);
        }
	}

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("productEAN", this.productEAN);
        node.put("productName", this.productName);
        node.put("slug", this.slug);
        node.put("productDescription", this.productDescription);
        node.put("productLongDescription", this.productLongDescription);
        node.put("featureList", this.featureList);
        node.put("missionStatement", this.missionStatement);
        node.put("productStory", this.productStory);
        node.put("urlHome", this.urlHome);
        node.put("urlFacebook", this.urlFacebook);
        node.put("urlTwitter", this.urlTwitter);
		if(this.urlTwitter != null && this.urlTwitter.length() > 10){
			node.put("twitterUserName", this.urlTwitter.substring(this.urlTwitter.lastIndexOf(".com/")+5));
		}
        node.put("isFeatured", this.isFeatured);
        node.put("createdAt", new DateTime(this.createdAt).toString());
        node.put("updatedAt", new DateTime(this.updatedAt).toString());
//        if(this.user != null) {
//            node.put("user", this.user.toJson());
//        }
        List<Likes> likes = this.getAllLikes();
        if(likes.size()>0){
            node.put("likes", Likes.toJson(likes));
        }
        List<Tag> tags = this.getAllTags();
        if(tags.size()>0){
            node.put("tags", Tag.toJson(tags));
        }
        return node;
    }

    public static ArrayNode toJson(List<Product> products){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Product product : products){
            arrayNode.add(product.toJson());
        }
        return arrayNode;
    }
}
