package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.libs.Json;
import utils.Taggable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Post class
 */
@Entity
public class ProductCampaign extends SuperModel implements Taggable {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, ProductCampaign> find = new Finder<Long, ProductCampaign>(
            Long.class, ProductCampaign.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String title;

	public String message;
	public String messageLink;
	public Integer goal;

	@Column(name = "created_at")
	public Date finishAt;

    @Column(columnDefinition = "TEXT")
    public String story;

	public boolean activeFlag = true;

	@Constraints.Required // created, active, finished
	public Integer status;

	@ManyToOne
	public Product product;

    public static ProductCampaign findById(Long id) {
        return find.byId(id);
    }

    private ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
        node.put("title", this.title);
        node.put("story", this.story);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
		node.put("finishAt", new DateTime(this.finishAt).toString());
		List<Tag> tags = this.getAllTags();
		if(tags.size()>0){
			node.put("tags", Tag.toJson(tags));
		}
        return node;
    }

    public static ArrayNode toJson(List<ProductCampaign> posts){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (ProductCampaign post : posts){
            arrayNode.add(post.toJson());
        }
        return arrayNode;
    }

    public List<ProductCampaign> getAll() {
        return find.where().order("id desc").findList();
    }

	/*
	* @required categoryName
	* @required productID	 
	*/
	public List<ProductCampaign> getActivePostsbyCategory(String categoryName, Long productID){
			return (List<ProductCampaign>) find.where()
				.eq("category", categoryName)
				.eq("product_id", productID)
				.eq("active_flag", "1")
				.orderBy("date_time_stamp desc");
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

	public List<ProductCampaign> findAllByTag(String name) {

		List<ProductCampaign> posts = ProductCampaign.find
				.query()
				.setRawSql(Tag.findAllByTagRawSql(name, this.getClass().getName()))
				.where()
				.findList();

		return posts;
	}

}
