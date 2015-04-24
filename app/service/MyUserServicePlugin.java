package service;

import models.User;
import play.Application;
import play.Logger;
import play.mvc.Http.Context;
import providers.MyUsernamePasswordAuthProvider;

import com.amazonaws.services.opsworks.model.App;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.service.UserServicePlugin;

public class MyUserServicePlugin extends UserServicePlugin {

	
	public MyUserServicePlugin(final Application app) {
		super(app);
	}

	@Override
	public Object save(final AuthUser authUser) {
		final boolean isLinked = User.existsByAuthUserIdentity(authUser);
		if (!isLinked) {
			Logger.debug("MyUserServicePlugin > save");
			 User usr = User.create(authUser);
			 //TODO replace this with message queue way of sending email
			 //send welcome email
			 if(authUser instanceof FacebookAuthUser) {
				 MyUsernamePasswordAuthProvider prv = new MyUsernamePasswordAuthProvider(super.getApplication());
				 prv.sendWelcomeMessageMailing(usr, null);
			 }
			 return usr.id;
		} else {
			// we have this user already, so return null
			return null;
		}
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final User u = User.findByAuthUserIdentity(identity);
		if(u != null) {
			return u.id;
		} else {
			return null;
		}
	}

	@Override
	public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
		if (!oldUser.equals(newUser)) {
			User.merge(oldUser, newUser);
		}
		return oldUser;
	}

	@Override
	public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
		User.addLinkedAccount(oldUser, newUser);
		return newUser;
	}
	
	@Override
	public AuthUser update(final AuthUser knownUser) {
		// User logged in again, bump last login date
		User.setLastLoginDate(knownUser);
		return knownUser;
	}

}
