package constants;

import be.objectify.deadbolt.core.models.Role;

public enum UserRole implements Role{
	
	CREATOR,

	CONSUMER,

	VISITOR,
	
	ADMINISTRATOR,
	
	INFLUENCER
	;
	
	 @Override
	    public String getName()
	    {
	        return name();
	    }

}
