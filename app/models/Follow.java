package models;

import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.libs.Json;

@Entity
public class Follow extends SuperModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	//Id of user who is taking a follow action
	@ManyToOne
	public User user;

	/**
	 * Type of subject: user, product 
	 */
	public String refType;

	/**
	 * Id of subject (user or a product) who the user is following
	 */
	public Long refId;
	
	public Follow() {
		// Left empty
	}
	
	public String follow(User user, Long followingId) {
        String result = "failure";
//                boolean added = person.addFriend(userAuth.getUserId(), userSearched
//                                .getUserId());
//                result = "success";
        return "success";

	}
	
	public static Follow findById(Long id) {
		return find.byId(id);
	}

	public static Finder<Long, Follow> find = new Finder<Long, Follow>(Long.class,
			Follow.class);

	/*
	Follow an object identified by refId and refType
	 */
	public static Follow addFollow(Long userId, Long refId, String refType){
		Follow rs = null;

		// find user
		User user = User.find.byId(userId);

		if(user==null){
			return null;
		}

		Logger.debug("addFollow: found user");

		// see if user already following the object
		rs = Follow.find.where()
				.eq("user", user)
				.eq("refId", refId)
				.eq("refType", refType)
				.findUnique();

		if(rs == null) {
			// Follow not found so we insert
			rs = new Follow();
			rs.user = user;
			rs.refId = refId;
			rs.refType = refType;
			rs.save();
		}

		return rs;
	}

	/*
	Unfollow from an object identified by refId and refType
	 */
	public static void removeFollow(Long userId, Long refId, String refType) {
		Follow rs = null;

		// find user
		User user = User.find.byId(userId);

		// if user is found then find follow
		if(user != null) {
			rs = Follow.find.where()
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
	Remove all Followers from an object identified by refId and refType
	 */
	public static void removeAllFollowers(Long refId, String refType) {

		// find all followers
		List<Follow> followers = Follow.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		// delete followers one by one
		for (Follow follower : followers) {
			follower.delete();
		}
	}

	/*
	Get all Followers on an object identified by refId and refType
	 */
	public static List<Follow> getAllFollowers(Long refId, String refType) {

		// find all followers
		List<Follow> followers = Follow.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		return followers;
	}
	
	/*
	Get all Followings by current user
	 */
	public static List<Follow> getAllFollowings(Long userId) {
		// find user
		User user = User.find.byId(userId);
		// find all object user is following
		List<Follow> followings = Follow.find.where()
				.eq("user", user)
				.findList();

		return followings;
	}
	


	public ObjectNode toJson(){
		ObjectNode node = Json.newObject();
		node.put("name", this.user.name);
		node.put("createdAt", new DateTime(this.createdAt).toString());
		return node;
	}

	public static ArrayNode toJson(List<Follow> follows){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Follow follow : follows){
			arrayNode.add(follow.toJson());
		}
		return arrayNode;
	}

	
}
