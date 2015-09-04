package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.typesafe.plugin.RedisPlugin;
import org.joda.time.DateTime;
import org.xerial.snappy.Snappy;

import play.Logger;
import play.api.cache.Cache;
import play.cache.*;
import play.libs.Json;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;
import play.libs.F;

import javax.persistence.*;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Page;

import constants.AppConstants;
import redis.clients.jedis.Jedis;
import utils.Slugify;
import utils.Taggable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class Product extends SuperModel implements PathBindable<Product>,
		QueryStringBindable<Product>, Taggable {

	private static final long serialVersionUID = 1L;
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@JsonIgnore
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
	
	public String  urlBuy;
	public String  urlAppDownload;
	//# of units available for trial/ordered etc.
	public String unitsAvailable;
	
	public String contactEmailAddress;

	public boolean isFeatured;
	@Column(nullable=false, columnDefinition="boolean default true")
	public boolean activeFlag;
	
	public boolean trialSignUpFlag;
	public boolean buyFlag;
	public boolean trialFullHouseFlag;
	
	@OneToOne(cascade=CascadeType.ALL, mappedBy="product")
	public ProductAdditionalDetail productAdditionalDetail;

	@OneToMany(mappedBy="product")
	public List<Post> postItems;

	@OneToMany(mappedBy="product")
	public List<Trial> trials;

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

	public static List<Product> getAllActive() {
		return find.where().eq("activeFlag", "1").findList();
	}

	public static Product findbyID(Long id) {
		return find.byId(id);
	}

	@Override
	public void save() {
		cacheRemove();
		this.slugify();
		super.save();
	}

	@Override
	public void update(Object o) {
		cacheRemove();
		this.slugify();
		super.update(o);
	}
	
	public void update() {
		cacheRemove();
		this.slugify();
		super.update();
	}

	/*
		Clear product item from cache
	 */
	public void cacheRemove(){
		if(this.id != null) {
			play.cache.Cache.remove("product:" + this.id);
			//this.hmset();
		}
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

	public static Product findBySlug(String value) {
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
		            	.orderBy("active_flag desc, id desc")
		                .findPagingList(2000)
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

    public ObjectNode toJson() {
        //org.xerial.snappy.Snappy.compress(src, dest);
//        String input = "Hello snappy-java! Snappy-java is a JNI-based wrapper of "
//                + "Snappy, a fast compresser/decompresser.";
//        byte[] compressed = Snappy.compress(input.getBytes("UTF-8"));
//        byte[] uncompressed = Snappy.uncompress(compressed);
//
//        String result = new String(uncompressed, "UTF-8");
//        System.out.println(result);
        ObjectNode node;
        final ObjectReader reader = mapper.reader();

        byte[] compressed = (byte[]) play.cache.Cache.get("product:" + this.id.toString());
        if (compressed != null) {
            Logger.debug("product: cache hit");
            try {
                byte[] uncompressed = Snappy.uncompress(compressed);
                String result = new String(uncompressed, "UTF-8");
                node = (ObjectNode) mapper.readTree(result);
            } catch(Exception e){
                return null;
            };
            return node;
        }
        else{
            Logger.debug("product: cache miss");
            node =  this.toJsonRaw();
            String src = node.toString();
            try {
                compressed = Snappy.compress(src.getBytes());
                play.cache.Cache.set("product:" + this.id, compressed);
            } catch(Exception e){
                return null;
            }
            return node;
        }
    }

	public ObjectNode toJsonShort(){
		ObjectNode node = Json.newObject();
		node.put("id", this.id);
		node.put("name", this.productName);
		node.put("slug", this.slug);
		node.put("tagline", this.productDescription);
		node.put("urlHome", this.urlHome);
		node.put("urlFacebook", this.urlFacebook);
		node.put("urlTwitter", this.urlTwitter);

		if(this.productAdditionalDetail!=null) {
			if (productAdditionalDetail.bannerPhoto != null) {
				node.put("banner", productAdditionalDetail.bannerPhoto.toJson());
			}
		}

		return node;
	}


    private ObjectNode toJsonRaw(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
//        node.put("productEAN", this.productEAN);
        node.put("name", this.productName);
        node.put("slug", this.slug);
        node.put("tagline", this.productDescription);
        node.put("description", this.productLongDescription);
        node.put("features", this.featureList);
        node.put("mission", this.missionStatement);
        node.put("story", this.productStory);
        node.put("urlHome", this.urlHome);
        node.put("urlFacebook", this.urlFacebook);
        node.put("urlBuy", this.urlBuy);
        node.put("urlAppDownload", this.urlAppDownload);
        
        node.put("urlFacebook", this.urlFacebook);
        node.put("urlTwitter", this.urlTwitter);
		if(this.urlTwitter != null && this.urlTwitter.length() > 10){
			node.put("twitterUserName", this.urlTwitter.substring(this.urlTwitter.lastIndexOf(".com/")+5));
		}
        node.put("isFeatured", this.isFeatured);
        node.put("isAvailableToBuy", this.buyFlag);
        node.put("isAvailableForTrial", this.trialSignUpFlag);
        node.put("isTrialFullHouse", this.trialFullHouseFlag);
        
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
		if (this.trials!=null && this.trials.size()>0){
			node.put("trials", Trial.toJsonShort(trials));
		}

		if(this.productAdditionalDetail!=null) {
			if(productAdditionalDetail.bannerPhoto!=null) {
				node.put("banner", productAdditionalDetail.bannerPhoto.toJson());
			}

			if(productAdditionalDetail.descriptionVideo1!=null) {
				node.put("video", productAdditionalDetail.descriptionVideo1.toJson());
			}

			ArrayNode storyArrayNode = new ArrayNode(JsonNodeFactory.instance);
			if (this.productAdditionalDetail.productStoryPara1 != null) {
				storyArrayNode.add(this.productAdditionalDetail.productStoryPara1);
			}
			if (this.productAdditionalDetail.productStoryPara2 != null) {
				storyArrayNode.add(this.productAdditionalDetail.productStoryPara2);
			}
			if (this.productAdditionalDetail.productStoryPara3 != null) {
				storyArrayNode.add(this.productAdditionalDetail.productStoryPara3);
			}
			if (this.productAdditionalDetail.productStoryPara4 != null) {
				storyArrayNode.add(this.productAdditionalDetail.productStoryPara4);
			}
			node.put("story", storyArrayNode);
		}
        return node;
    }

    public static ArrayNode toJson(List<Product> products){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (Product product : products){
			ObjectNode prod = product.toJson();
			prod.remove("story");
			prod.remove("mission");
			prod.remove("description");
			prod.remove("urlHome");
			prod.remove("urlFacebook");
			prod.remove("urlTwitter");
//			prod.remove("banner");
			prod.remove("isAvailableToBuy");
			prod.remove("isFeatured");
			prod.remove("isTrialFullHouse");
			prod.remove("urlAppDownload");
			prod.remove("urlBuy");
			arrayNode.add(prod);
        }
        return arrayNode;
    }

	public void zadd(String key){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			Logger.debug("product added to set: " + key);
			j.zadd(key, updatedAt.getTime(), this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public void zrem(String key){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			// delete
			j.zrem(key, this.id.toString());
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode range(String key){
		ObjectNode items = Json.newObject();
		ArrayNode data = items.putArray("data");

		//Go to Redis to read the full roster of content.
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			if(!j.exists(key)) {
				Logger.debug("adding products to cache: " + key);
				for(Product item: Product.getAllActive()){
					item.zadd(key);
				}
			}

			Set<String> set = j.zrange(key, 0, -1);
			items.put("count", j.zcard(key));

			for(String id: set) {
				// get the data for each like
				data.add(Product.hmget(id));
			}
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}

		return items;
	}

	public void hmset(){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			Map map = new HashMap();

			// Create the hashmap values
			map.put("name", this.productName);
			map.put("slug", this.slug);
			if(this.productDescription!=null) map.put("tagline", this.productDescription);
			if(this.productLongDescription!=null) map.put("description", this.productLongDescription);
			if(this.productStory!=null) map.put("story", this.productStory);
			if(this.missionStatement!=null) map.put("mission", this.missionStatement);
			if(this.urlHome!=null) map.put("urlHome", this.urlHome);
			if(this.urlFacebook!=null) map.put("urlFacebook", this.urlFacebook);
			if(this.urlTwitter!=null) map.put("urlTwitter", this.urlTwitter);

			if(this.productAdditionalDetail!=null) {
				if (productAdditionalDetail.bannerPhoto != null) {
					map.put("banner", productAdditionalDetail.bannerPhoto.id.toString());
				}
			}

			map.put("isAvailableForTrial", String.valueOf(this.trialSignUpFlag));

			// add the values
			j.hmset("prod:" + this.id.toString(), map);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode hmget(String id){
		Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		try {
			return hmget(id, j);
		} finally {
			play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
		}
	}

	public static ObjectNode hmget(String id, Jedis j){
		ObjectNode node = Json.newObject();
		try {
			String key = "prod:" + id.toString();

			// if key not found then add item to cache
			if(!j.exists(key)) {
				Logger.debug("product added to cache " + key);
				Product.find.byId(Long.parseLong(id, 10)).hmset();
			}

			// get the values
			List<String> values = j.hmget(key,
					"name",
					"slug",
					"tagline",
					"description",
					"story",
					"mission",
					"urlHome",
					"urlFacebook",
					"urlTwitter",
					"banner",
					"isAvailableForTrial"
			);

			// build json object
			node.put("id", id);
			if (values.get(0) != null) node.put("name", values.get(0));
			if (values.get(1) != null) node.put("slug", values.get(1));
			if (values.get(2) != null) node.put("tagline", values.get(2));
			if (values.get(3) != null) node.put("description", values.get(3));
			if (values.get(4) != null) node.put("story", values.get(4));
			if (values.get(5) != null) node.put("mission", values.get(5));
			if (values.get(6) != null) node.put("urlHome", values.get(6));
			if (values.get(7) != null) node.put("urlFacebook", values.get(7));
			if (values.get(8) != null) node.put("urlTwitter", values.get(8));
			if (values.get(9) != null) {
				node.put("banner", File.hmget(values.get(9)));
			}
			if (values.get(10) != null) node.put("isAvailableForTrial", Boolean.valueOf(values.get(10)));
		} finally {
		}
		return node;
	}
}
