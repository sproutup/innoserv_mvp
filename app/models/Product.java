package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import utils.Slugify;
import utils.Taggable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

	public List<Product> getAllActive() {
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

        byte[] compressed = (byte[]) play.cache.Cache.get("product:" + this.id);
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
		node.put("productDescription", this.productDescription);
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
			node.put("trials", Trial.toJson(trials));
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

			ArrayNode teamArrayNode = new ArrayNode(JsonNodeFactory.instance);

			if (this.productAdditionalDetail.member1Name != null && this.productAdditionalDetail.member1Name != "") {
				ObjectNode member = Json.newObject();
				member.put("name", this.productAdditionalDetail.member1Name);
				member.put("description", this.productAdditionalDetail.member1Description);
				member.put("title", this.productAdditionalDetail.member1Title);
				if (this.productAdditionalDetail.member1Photo != null) {
					member.put("photo", this.productAdditionalDetail.member1Photo.toJson());
				}
				teamArrayNode.add(member);
			}
			if (this.productAdditionalDetail.member2Name != null && this.productAdditionalDetail.member2Name != "") {
				ObjectNode member = Json.newObject();
				member.put("name", this.productAdditionalDetail.member2Name);
				member.put("description", this.productAdditionalDetail.member2Description);
				member.put("title", this.productAdditionalDetail.member2Title);
				if (this.productAdditionalDetail.member2Photo != null) {
					member.put("photo", this.productAdditionalDetail.member2Photo.toJson());
				}
				teamArrayNode.add(member);
			}
			if (this.productAdditionalDetail.member3Name != null && this.productAdditionalDetail.member3Name != "") {
				ObjectNode member = Json.newObject();
				member.put("name", this.productAdditionalDetail.member3Name);
				member.put("description", this.productAdditionalDetail.member3Description);
				member.put("title", this.productAdditionalDetail.member3Title);
				if (this.productAdditionalDetail.member3Photo != null) {
					member.put("photo", this.productAdditionalDetail.member3Photo.toJson());
				}
				teamArrayNode.add(member);
			}
			if (this.productAdditionalDetail.member4Name != null && this.productAdditionalDetail.member4Name != "") {
				ObjectNode member = Json.newObject();
				member.put("name", this.productAdditionalDetail.member4Name);
				member.put("description", this.productAdditionalDetail.member4Description);
				member.put("title", this.productAdditionalDetail.member4Title);
				if (this.productAdditionalDetail.member4Photo != null) {
					member.put("photo", this.productAdditionalDetail.member4Photo.toJson());
				}
				teamArrayNode.add(member);
			}
			if (this.productAdditionalDetail.member5Name != null && this.productAdditionalDetail.member5Name != "") {
				ObjectNode member = Json.newObject();
				member.put("name", this.productAdditionalDetail.member5Name);
				member.put("description", this.productAdditionalDetail.member5Description);
				member.put("title", this.productAdditionalDetail.member5Title);
				if (this.productAdditionalDetail.member5Photo != null) {
					member.put("photo", this.productAdditionalDetail.member5Photo.toJson());
				}
				teamArrayNode.add(member);
			}
			if (this.productAdditionalDetail.member6Name != null && this.productAdditionalDetail.member6Name != "") {
				ObjectNode member = Json.newObject();
				member.put("name", this.productAdditionalDetail.member6Name);
				member.put("description", this.productAdditionalDetail.member6Description);
				member.put("title", this.productAdditionalDetail.member6Title);
				if (this.productAdditionalDetail.member6Photo != null) {
					member.put("photo", this.productAdditionalDetail.member6Photo.toJson());
				}
				teamArrayNode.add(member);
			}
			node.put("team", teamArrayNode);
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
