package constants;


public interface AppConstants {

	public final String BELLEDS_URL_AS_STRING = "https://qtrial2013.qualtrics.com/SE/?SID=SV_cYZoWLaHJFAUujj";
	
	public final String FORUM_TYPE_SUGGESTION = "suggestion";
	public final String FORUM_TYPE_COMPLIMENT = "compliment";
	public final String FORUM_TYPE_QUESTION = "question";
	
	/**
	 * @Restrict tag requires value for annotation attribute group.value to be a constant expression
	public static final String CREATOR = UserRole.CREATOR.name();
	public static final String CONSUMER = UserRole.CONSUMER.toString();
	public static final String VISITOR = UserRole.VISITOR.name();
	public static final String ADMINISTRATOR = UserRole.ADMINISTRATOR.name();
	*/
	
	public static final String CREATOR = "CREATOR";
	public static final String CONSUMER = "CONSUMER";
	public static final String VISITOR = "VISITOR";
	public static final String ADMINISTRATOR = "ADMINISTRATOR";
	public static final String INFLUENCER = "INFLUENCER";
}
