package plugins;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.Plugin;

import play.Play;
import play.libs.F;
import play.libs.ws.WSResponse;
import play.libs.ws.*;


/**
 * Created by Apurv on 7/6/15.
 */
public class KloutPlugin extends Plugin {

    private static String api_key = Play.application().configuration().getString("klout.api.key");

    public static Double getKloutScoreFromUser(User influencer) {
        if (influencer.urlTwitter == null) return null;
        String twtScreenName = influencer.urlTwitter.substring(influencer.urlTwitter.lastIndexOf('/') + 1);
        return getScore(getKloutId(twtScreenName));
    }

    /**
     * Retrieves the Klout network id from twitter screen name
     * Returns null when parameters do not correspond to a Klout network id.
     *
     * @param twtName is user's twitter screen name
     * @return String with Klout id of given parameters (or null if id does not exist)
     */
    private static Long getKloutId(String twtName) {
        JsonNode content = playContentBody("http://api.klout.com/v2/identity.json/twitter?screenName="+twtName+"&key="+api_key);
        if (content == null) return null;
        else return content.get("id").asLong();
    }

    /**
     * Retrieves the Klout score of the specified Klout network id.
     * Returns null if passed an invalid Klout network id.
     *
     * @param kloutId Klout id
     * @return Double with score of given id (or null if kloutId does not exist)
     */
    public static Double getScore(Long kloutId) {
        if (kloutId == null) return null;
        JsonNode content = playContentBody("http://api.klout.com/v2/user.json/"+kloutId.toString()+"/score"+"?key="+api_key);
        if (content == null) return null;
        else return content.get("score").asDouble();
    }

    public static JsonNode playContentBody(String url) {
        F.Promise<JsonNode> promise = WS.url(url)
                .setContentType("application/x-www-form-urlencoded")
                .get()
                .map(
                        new F.Function<WSResponse, JsonNode>() {
                            public JsonNode apply(WSResponse response) {
                                if (response.getBody().equals("")) {
                                    return null;
                                }
                                return response.asJson();
                            }
                        }
                );
        try {
            // timeout of up to 10 seconds per call
            long timeout = 10000l;
            return promise.get(timeout);
        } catch (Exception e) {
            return null;
        }
    }
}
