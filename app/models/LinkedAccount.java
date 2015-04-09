package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.feth.play.module.pa.providers.oauth1.twitter.TwitterAuthUser;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import play.Logger;
import play.db.ebean.Model;

import com.feth.play.module.pa.user.AuthUser;

@Entity
public class LinkedAccount extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	public User user;

	public String providerUserId;
	public String providerUserName;
	public String providerUserImageUrl;
	public String providerKey;

	public static final Finder<Long, LinkedAccount> find = new Finder<Long, LinkedAccount>(
			Long.class, LinkedAccount.class);

	public static LinkedAccount findByProviderKey(final User user, String key) {
		return find.where().eq("user", user).eq("providerKey", key)
				.findUnique();
	}

	public static LinkedAccount create(final AuthUser authUser) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update(authUser);
		return ret;
	}
	
	public void update(final AuthUser authUser) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
		if(authUser instanceof TwitterAuthUser) {
			final TwitterAuthUser twitter = (TwitterAuthUser) authUser;
			providerUserImageUrl = twitter.getPicture().replace("_normal","");
			providerUserName = twitter.getScreenName();
		}
		if(authUser instanceof FacebookAuthUser) {
			final FacebookAuthUser facebook = (FacebookAuthUser) authUser;
			providerUserImageUrl = facebook.getPicture();
			providerUserName = facebook.getEmail();
		}
	}

	public static LinkedAccount create(final LinkedAccount acc) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;

		return ret;
	}
}