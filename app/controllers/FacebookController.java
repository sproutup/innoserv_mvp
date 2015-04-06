package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import models.Product;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by peter on 3/27/15.
 */
public class FacebookController extends Controller {
    public static final String FACEBOOK_APP_ID = "facebook.app.id";
    public static final String FACEBOOK_APP_SECRET = "facebook.app.secret";
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

//    public static Promise<Result> index() {
//        Promise<Integer> promiseOfInt = Promise.promise(
//                new Function0<Integer>() {
//                    public Integer apply() {
//                        return intensiveComputation();
//                    }
//                }
//        );
//        return promiseOfInt.map(
//                new Function<Integer, Result>() {
//                    public Result apply(Integer i) {
//                        return ok("Got result: " + i);
//                    }
//                }
//        );
//    }

    public static Promise<Result> getApi(final String endpoint) {
        String app_id = Play.application().configuration().getString(FACEBOOK_APP_ID);
        String app_secret = Play.application().configuration().getString(FACEBOOK_APP_SECRET);

        Logger.debug("facebook api: " + endpoint);
        byte[] bytes = (byte[]) Cache.get("facebook:" + endpoint);
        if(bytes != null) {
            Logger.debug("facebook api: cache hit");
            // Reading...
            // Create an immutable reader
            final ObjectReader reader = mapper.reader();

            // Use the reader for thread safe access
            final JsonNode newNode;
            try {
                newNode = reader.readTree(new ByteArrayInputStream(bytes));
                Promise<Result> resultPromise = Promise.pure((Result)ok(newNode));
                return resultPromise;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        else {
            Logger.debug("facebook api: cache miss");
            final Promise<Result> resultPromise = WS.url("https://graph.facebook.com/v2.3/" + endpoint)
                    .setQueryParameter("access_token", "427426070766217" + "|" + "sSaCqojAxd-bzko2gs9-_Zrdbeo")
                    .get()
                    .map(
                        new Function<WSResponse, Result>() {
                            public Result apply(WSResponse response) {
                                try {
                                    // Writing...
                                    // Create an immutable writer (in this case using the default settings)
                                    final ObjectWriter writer = mapper.writer();
                                    // Use the writer for thread safe access.
                                    final byte[] bytes;
                                    bytes = writer.writeValueAsBytes(response.asJson());
                                    // Cache for 15 minutes
                                    Cache.set("facebook:" + endpoint, bytes, 60 * 15);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                                return ok(response.asJson());
                            }
                        }
                    );
            return resultPromise;
        }
    }


    public static Promise<Result> getPosts(Long product_id) {
        Product prod = Product.findbyID(product_id);
        // if product is not found return
        Logger.debug("facebook api > get posts");
        if(prod == null || prod.urlFacebook == null || prod.urlFacebook.length() < 1){
            Logger.debug("facebook api > get posts > not found");
            return Promise.pure((Result)notFound());
        }
        Logger.debug("facebook api > get posts > found > ", prod.urlFacebook);
        String endpoint = prod.urlFacebook.substring(prod.urlFacebook.lastIndexOf(".com/")+5);
        return getApi(endpoint + "/posts");
    }

    public static Promise<Result> getAccessToken() {
        String app_id = Play.application().configuration().getString(FACEBOOK_APP_ID);
        String app_secret = Play.application().configuration().getString(FACEBOOK_APP_SECRET);

        final Promise<Result> resultPromise = WS.url("https://graph.facebook.com/oauth/access_token")
                .setQueryParameter("client_id", app_id)
                .setQueryParameter("client_secret", app_secret)
                .setQueryParameter("grant_type", "client_credentials")
                .get()
                .map(
                    new Function<WSResponse, Result>() {
                        public Result apply(WSResponse response) {
                            return ok(response.asJson());
                        }
                    }
                );
        return resultPromise;
    }
}
