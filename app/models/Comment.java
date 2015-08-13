package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

@Entity
public class Comment extends SuperModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static Comment findById(Long id) {
		return find.byId(id);
	}

	public static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class,
			Comment.class);

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	public Long refId;

	public String refType;

	public String body;

	public Comment() {
		// Left empty
	}


	/*
	Add comment to an object identified by refId and refType
	 */
	public static Comment addComment(Long userId, String comment, Long refId, String refType){
		Comment rs = null;

		// find user
		User user = User.find.byId(userId);

		if(user==null){
			return null;
		}

		Logger.debug("addComment: found user");

		// Like not found so we insert
		rs = new Comment();
		rs.user = user;
		rs.body = comment;
		rs.refId = refId;
		rs.refType = refType;
		rs.save();

		return rs;
	}

	/*
	Remove comment from an object identified by refId and refType
	 */
	public static void removeComment(Long commentId, Long refId, String refType) {
		Comment rs = null;

		rs = Comment.find.byId(commentId);

		// if found then delete
		if(rs != null){
			rs.delete();
		}
	}

	/*
	Remove all comments from an object identified by refId and refType
	 */
	public static void removeAllComments(Long refId, String refType) {

		// find all
		List<Comment> items = Comment.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		// delete one by one
		for (Comment item : items) {
			item.delete();
		}
	}

	/*
	Get all comments on an object identified by refId and refType
	 */
	public static List<Comment> getAllComments(Long refId, String refType) {

		// find all
		List<Comment> items = Comment.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		return items;
	}

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
        node.put("id", this.id);
		node.put("body", this.body);
		node.put("createdAt", new DateTime(this.createdAt).toString());

		if(this.user != null) {
			node.put("user", this.user.toJsonShort());
		}

		return node;
	}

	public static ArrayNode toJson(List<Comment> items){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Comment item : items){
			arrayNode.add(item.toJson());
		}
		return arrayNode;
	}
}
