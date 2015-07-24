package models;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.libs.Json;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class AnalyticsAccountSummary extends TimeStampModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	public String name;
	public String gaId;
	public String kind;
	public Long viewCount;
	public Long followerCount;
    public byte isValid;
    public String errorMessage;

	@ManyToOne
	public AnalyticsAccount analyticsAccount;

	public static final Finder<Long, AnalyticsAccountSummary> find = new Finder<Long, AnalyticsAccountSummary>(
			Long.class, AnalyticsAccountSummary.class);

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("id", this.id);
		node.put("name", this.name);
		node.put("kind", this.kind);
		node.put("gaId", this.gaId);
		node.put("viewCount", this.viewCount);
		node.put("followerCount", this.followerCount);
		node.put("isValid", this.isValid);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
		return node;
	}

	public static ArrayNode toJson(List<AnalyticsAccountSummary> items){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (AnalyticsAccountSummary item : items){
			arrayNode.add(item.toJson());
		}
		return arrayNode;
	}
}
