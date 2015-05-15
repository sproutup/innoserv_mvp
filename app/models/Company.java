package models;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.db.ebean.Model.Finder;
import play.libs.Json;

@Entity
public class Company extends TimeStampModel{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	public String companyName;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="company")
	public List<Product> products;

	
	public static Finder<Long, Company> find = new Finder<Long, Company>(
			Long.class, Company.class);

	@OneToMany
	public List<User> users;

	public static List<Company> getAll() {
		return find.all();
	}

	public static Company findbyID(Long id) {
		return find.byId(id);
	}

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("id", this.id);
		node.put("name", this.companyName);
		node.put("products", Product.toJson(this.products));
		return node;
	}

	public static ArrayNode toJson(List<Company> companies){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Company company : companies){
			arrayNode.add(company.toJson());
		}
		return arrayNode;
	}
}
