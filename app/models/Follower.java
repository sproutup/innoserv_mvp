package models;

import java.util.List;

import javax.persistence.*;

@Entity
public class Follower {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	/*
	 * NJ: I intentionally left out bi-directional mapping;
	 * as tables being created are more complicated than needed;
	 * we will manage relationships through our code;
	 */


	//Id of user who the user is following
	public Long followerId;
	
	//Id of user who is being followed
	public Long followingId;
	
	
	
}
