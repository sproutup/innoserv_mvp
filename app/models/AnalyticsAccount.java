package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.libs.Json;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;

@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class AnalyticsAccount extends TimeStampModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	public String provider;
	public String accessToken;
	public String refreshToken;
	public String scope;

	@Column(name = "expires_at")
	public Date expiresAt;

	public boolean googleAnalyticsAPI;
	public boolean youtubeAnalyticsAPI;
    public byte isValid;
    public String username;
    public String errorMessage;

	@OneToMany
	public List<AnalyticsAccountSummary> summaries;

	public static final Finder<Long, AnalyticsAccount> find = new Finder<Long, AnalyticsAccount>(
			Long.class, AnalyticsAccount.class);

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("id", this.id);
		node.put("googleAnalyticsAPI", this.googleAnalyticsAPI);
		node.put("youtubeAnalyticsAPI", this.youtubeAnalyticsAPI);
		if(this.summaries != null){
			node.put("summaries", AnalyticsAccountSummary.toJson(this.summaries));
		}
		node.put("createdAt", new DateTime(this.createdAt).toString());
		node.put("updatedAt", new DateTime(this.updatedAt).toString());
		return node;
	}

	public static ArrayNode toJson(List<AnalyticsAccount> items){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (AnalyticsAccount item : items){
			arrayNode.add(item.toJson());
		}
		return arrayNode;
	}
}
