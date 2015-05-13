package models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.providers.oauth1.twitter.TwitterAuthUser;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;

import constants.UserRole;
import controllers.Application;
import models.TokenAction.Type;

import org.joda.time.DateTime;

import play.Logger;
import play.api.data.validation.Constraint;
import play.api.mvc.Session;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Http;

import javax.persistence.*;

import java.util.*;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "users")
public class User extends Model implements Subject {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@Constraints.Email
	// if you make this unique, keep in mind that users *must* merge/link their
	// accounts then on signup with additional providers
	// @Column(unique = true)
	public String email;

	public String name;

	public String nickname;

	public String firstName;

	public String lastName;

	public String phoneNumber;

    @Constraints.MaxLength(1024)
    public String description;

    public String urlFacebook;
    public String urlTwitter;
    public String urlPinterest;
    public String urlBlog;

    /*
	 * ship address
	 */
	public String streetAddress1;
	public String streetAddress2;
	public String city;
	public String state;
	public String zipcode;

	public String gender;

	public Date dateofbirth;

	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date lastLogin;

	@Column(nullable=false)
	public boolean active;

	public boolean emailValidated;

	/**
	 * An external user e.g. twitter post, FB post someone who hasn't signed up on SproutUp yet
	 */
	public boolean external;
	
	/**
	 * e.g. TWT, FB, IG, Trial signup
	 */
	public String externalType;

	@OneToOne
	public File avatar;


	@ManyToMany
	public List<SecurityRole> roles;

	@OneToMany(cascade = CascadeType.ALL)
	public List<LinkedAccount> linkedAccounts;

	@ManyToMany
	public List<UserPermission> permissions;

	//user submitted media 
	@OneToMany(cascade = CascadeType.ALL)
	//@OneToMany(mappedBy="user")
	public List<Media> mediaList;

	@OneToMany
	public List<Post> posts;

	@ManyToOne
	public Company company;

    @OneToMany
    public List<File> files;
    
    @Transient
    public boolean randomNumberflag= false;
    

	public static final Finder<Long, User> find = new Finder<Long, User>(
			Long.class, User.class);

	@Override
	public String getIdentifier()
	{
		return Long.toString(id);
	}

	@Override
	public List<? extends Role> getRoles() {
		return roles;
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return permissions;
	}

	public static boolean existsByAuthUserIdentity(
			final AuthUserIdentity identity) {
		final ExpressionList<User> exp;
		if (identity instanceof UsernamePasswordAuthUser) {
			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
		} else {
			exp = getAuthUserFind(identity);
		}
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<User> getAuthUserFind(
			final AuthUserIdentity identity) {
		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity).findUnique();
		}
	}

	public static User findByUsernamePasswordIdentity(
			final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<User> getUsernamePasswordAuthUserFind(
			final UsernamePasswordAuthUser identity) {
		return getEmailUserFind(identity.getEmail()).eq(
				"linkedAccounts.providerKey", identity.getProvider());
	}

	public void merge(final User otherUser) {
		for (final LinkedAccount acc : otherUser.linkedAccounts) {
			this.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save(Arrays.asList(new User[] { otherUser, this }));
	}

	/**
	 * Default method for creating user with Consumer Role
	 * @param authUser
	 * @return
	 */
	
	public static User create(final AuthUser authUser) {
		
		return create (authUser, UserRole.CONSUMER.name());
	}	
	
	public static User create(final AuthUser authUser, String role) {
		Logger.debug("user > create");
		final User user = new User();
		user.roles = Collections.singletonList(SecurityRole
				.findByRoleName(role));
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList(LinkedAccount
				.create(authUser));
		
		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.emailValidated = false;
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				user.name = name;
			}
		}

		if (authUser instanceof FirstLastNameIdentity) {
		  final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
		  final String firstName = identity.getFirstName();
		  final String lastName = identity.getLastName();
		  if (firstName != null) {
		    user.firstName = firstName;
		  }
		  if (lastName != null) {
		    user.lastName = lastName;
		  }

		}

		if(authUser instanceof TwitterAuthUser) {
			final TwitterAuthUser twitter = (TwitterAuthUser) authUser;
			user.urlTwitter = "https://twitter.com/" + twitter.getScreenName();
		}

		if(authUser instanceof FacebookAuthUser) {
			final FacebookAuthUser facebook = (FacebookAuthUser) authUser;
			user.urlFacebook = facebook.getProfileLink();
		}
		
		// \W = Anything that isn't a word character (including punctuation etc)
		//user.nickname = user.name.toLowerCase().replaceAll("\\W","");
		user.nickname = user.generateUniqueUserNickName();

		
		user.save();
		user.saveManyToManyAssociations("roles");
		// user.saveManyToManyAssociations("permissions");
		return user;
	}

	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		User.findByAuthUserIdentity(oldUser).merge(
				User.findByAuthUserIdentity(newUser));
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				linkedAccounts.size());
		for (final LinkedAccount acc : linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final User u = User.findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}

	public static void setLastLoginDate(final AuthUser knownUser) {
		final User u = User.findByAuthUserIdentity(knownUser);
		u.lastLogin = new Date();
		u.save();
	}

	public static User findByEmail(final String email) {
		return getEmailUserFind(email).findUnique();
	}

	private static ExpressionList<User> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this, providerKey);
	}

	public static void verify(final User unverified) {
		// You might want to wrap this into a transaction
		unverified.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
	}

	public void changePassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
		if (a == null) {
			if (create) {
				a = LinkedAccount.create(authUser);
				a.user = this;
			} else {
				throw new RuntimeException(
						"Account not enabled for password usage");
			}
		}
		a.providerUserId = authUser.getHashedPassword();
		a.save();
	}

	public void resetPassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		// You might want to wrap this into a transaction
		this.changePassword(authUser, create);
		TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
	}

    public ObjectNode toJson(){
        ObjectNode node = Json.newObject();
        node.put("id", this.id);
		node.put("name", this.name);
		node.put("nickname", this.nickname);
        node.put("email", this.email);
        node.put("firstname", this.firstName);
        node.put("lastname", this.lastName);
        node.put("description", this.description);
        node.put("urlFacebook", this.urlFacebook);
        node.put("urlTwitter", this.urlTwitter);
        node.put("urlPinterest", this.urlPinterest);
        node.put("urlBlog", this.urlBlog);
        node.put("zipcode", this.zipcode);
        node.put("roles", Json.toJson(this.getRoles()));
        if (this.getRoles()!=null && this.getRoles().size()>0){
        	node.put("role", this.getRoles().get(0).getName().toLowerCase());
        } else {
        	node.put("role", "external");
        }
        node.put("permissions", Json.toJson(this.getPermissions()));
        node.put("lastLogin", new DateTime(this.lastLogin).toString());
        node.put("avatarUrl", getAvatar());

		if(this.company!=null){
			node.put("company", this.company.toJson());
		}

		// todo should be based on either tags or role
        ArrayNode badges = node.putArray("badges");
        badges.add("Member");
        return node;
    }

    public String getAvatar(){
		if(avatar != null){
			return avatar.getURL();
		}
		else
		if(getProviders().contains("facebook")){
			return getAccountByProvider("facebook").providerUserImageUrl;
			//return "http://graph.facebook.com/" + facebookId + "/picture/?type=large";
		}
		else
		if(getProviders().contains("twitter")){
			return getAccountByProvider("twitter").providerUserImageUrl;
			//return "http://graph.facebook.com/" + facebookId + "/picture/?type=large";
		}
        else{
            return "assets/images/general-profile-icon.png";
        }
    }

	public void setAvatar(File file){
		// set the avatar url
		this.avatar = file;
	}
    
    public String getFirstName(){
    	if (firstName == null || firstName == "") {
            //fetch it from the fullname
    		String[] bits = name.split(" ");
    		firstName = bits[0];
        }
    	return firstName;
    }
    
    public String getLastName(){
    	if (lastName == null || lastName == "") {
            //fetch it from the fullname
    		if (name != null && name.length() > 0) {
	    		String[] bits = name.split(" ");
	    		if (bits.length==2){//name with no middle name
	    			lastName = bits[1];
	    		} else if (bits.length==3){//name with Middle Name
	    			lastName = bits[2];
	    		}
    		}
        }
    	return lastName;

    }
    
    /*
     *  The username may be claimed by a suspended or deactivated account. 
     *  Suspended and deactivated usernames are not immediately available for use, so we are not checking if user is active or not.
     */
    public boolean isNickNameUnique(String nickname) {
    	
    	User usr = find.where().eq("nickname", nickname).findUnique();
    	if (usr==null || usr.nickname==null || usr.nickname==""){
    		return true;
    	}
    	return false;
    }
    
    public String generateUniqueUserNickName() {
        
    	ArrayList<String> list = generateListOfUserNickNames();
    	
        //iterate through the list while we get the unique name
    	if(!randomNumberflag){	
	        for (String temp: list){
	        	if(isNickNameUnique(temp)){
	            	nickname = temp;
	        		return nickname;
	    	    }
	        }
        }
        /**
		 * Unique name didn't work without numbers
		 * generate nick name suffix with random numbers
		 */
        
	    randomNumberflag= true;
    	Random generator = new Random();
        /**
		 * generate random number
		 */
		int numberGen = generator.nextInt(99);
	     for (String temp: list){
	        	if(isNickNameUnique(temp+numberGen)){
	            	nickname = temp+numberGen;
	        		return nickname;
	    	    }
	        }
        return generateUniqueUserNickName();
        
    }
    
    
    /**
     * Policy - Extracted and enhanced from Twitter
     * 
     * Username cannot be longer than 15 characters. Though Full name can be longer (20 characters); 
     * Usernames are kept shorter for the sake of ease. Minimum size is 3 characters
     * A username can only contain alphanumeric characters (letters A-Z, numbers 0-9) with the exception of underscores (No symbols, dashes, or spaces).  
     * @return
     */
    
    public ArrayList<String> generateListOfUserNickNames() {
    	
    	ArrayList<String> usernames = new ArrayList<String>();
      		
        lastName = getLastName();
        
        String lName = null;
        String fName = null;
        
        if (lastName!=null){
        	lName = lastName.replaceAll("[^\\w\\s\\_]", "");
        }
        
        fName = getFirstName().replaceAll("[^\\w\\s\\_]", "");
        
        if (email != null && email.length() > 0) {
            usernames.add(email.substring(0, email.indexOf("@")).toLowerCase());
            //usernames.add(email.substring(0, email.indexOf("@")).toLowerCase() + numberGen);
        }
        
        if (fName != null && fName.length() > 0 && lName != null && lName.length() > 0) {
        	usernames.add((fName + lName).toLowerCase());
        	usernames.add((lName + fName).toLowerCase());
        	usernames.add((fName.charAt(0) + lName).toLowerCase());
            usernames.add((fName.charAt(0) + "." + lName).toLowerCase());
            usernames.add((lName + fName.charAt(0)).toLowerCase());
            //usernames.add((lName + "." + fName.charAt(0) + Integer.toString(numberGen)).toLowerCase());
            //usernames.add((fName + lName + Integer.toString(numberGen)).toLowerCase());
            usernames.add((fName + "." + lName).toLowerCase());
            usernames.add((fName + lName.charAt(0)).toLowerCase());
            usernames.add((fName + "." + lName.charAt(0)).toLowerCase());
            usernames.add((lName.charAt(0) + fName).toLowerCase());
            usernames.add((lName.charAt(0) + "." + fName).toLowerCase());
            //usernames.add((lName.charAt(0) + fName.charAt(0)) + Integer.toString(numberGen).toLowerCase());
            //usernames.add((lName.charAt(0) + "." + fName.charAt(0)).toLowerCase()+Integer.toString(numberGen));
            //usernames.add((fName.charAt(0) + lName.charAt(0) + Integer.toString(numberGen)).toLowerCase());
            //usernames.add((fName.charAt(0) + "." + lName.charAt(0) + Integer.toString(numberGen)).toLowerCase());
            
            if (lName!=null && lName.length() > 4) {
                usernames.add((fName + lName.substring(0, 4)).toLowerCase());
                usernames.add((fName + "." + lName.substring(0, 4)).toLowerCase());
            }
            if (lName!=null && lName.length() > 5) {
                usernames.add((fName + lName.substring(0, 5)).toLowerCase());
                usernames.add((fName + "." + lName.substring(0, 5)).toLowerCase());
            }
            if (lName!=null && lName.length() > 6) {
                usernames.add((fName + lName.substring(0, 6)).toLowerCase());
                usernames.add((fName + "." + lName.substring(0, 6)).toLowerCase());
            }
  
        }
        
        if (fName != null && fName.length() > 2 && (lName == null || lName.length() == 0)){
        	usernames.add((fName).toLowerCase());
        }
        
        return usernames;
    }

    
    
    public static ArrayNode toJson(List<User> users){
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (User user : users){
            arrayNode.add(user.toJson());
        }
        return arrayNode;
    }
    

}
