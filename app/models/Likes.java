package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;
import play.Logger;

@Entity
public class Likes extends SuperModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static Likes findById(Long id) {
		return find.byId(id);
	}

	public static Finder<Long, Likes> find = new Finder<Long, Likes>(Long.class,
			Likes.class);

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	public Long refId;

	public String refType;

	public Likes() {
		// Left empty
	}


	/*
	Add like to an object identified by refId and refType
	 */
	public static Likes addLike(Long userId, Long refId, String refType){
		Likes rs = null;

		// find user
		User user = User.find.byId(userId);

		if(user==null){
			return null;
		}

		Logger.debug("addLikes: found user");

		// see if like exists already
		rs = Likes.find.where()
				.eq("user", user)
				.eq("refId", refId)
				.eq("refType", refType)
				.findUnique();

		if(rs == null) {
			// Like not found so we insert
			rs = new Likes();
			rs.user = user;
			rs.refId = refId;
			rs.refType = refType;
			rs.save();
		}

		return rs;
	}

	/*
	Remove like from an object identified by refId and refType
	 */
	public static void removeLike(Long userId, Long refId, String refType) {
		Likes rs = null;

		// find user
		User user = User.find.byId(userId);

		// if user is found then find like
		if(user != null) {
			rs = Likes.find.where()
					.eq("user", user)
					.eq("refId", refId)
					.eq("refType", refType)
					.findUnique();
		}

		// if link is found then delete
		if(rs != null){
			rs.delete();
		}
	}

	/*
	Remove all likes from an object identified by refId and refType
	 */
	public static void removeAllLikes(Long refId, String refType) {

		// find all likes
		List<Likes> likes = Likes.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		// delete likes one by one
		for (Likes like : likes) {
			like.delete();
		}
	}

	/*
	Get all likes on an object identified by refId and refType
	 */
	public static List<Likes> getAllLikes(Long refId, String refType) {

		// find all likes
		List<Likes> likes = Likes.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		return likes;
	}

	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("name", this.user.name);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		return node;
	}

	public static ArrayNode toJson(List<Likes> likes){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Likes like : likes){
			arrayNode.add(like.toJson());
		}
		return arrayNode;
	}
}
