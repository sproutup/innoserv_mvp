package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import play.cache.Cache;
import play.data.Form;
import play.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import com.typesafe.plugin.RedisPlugin;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.persistence.PersistenceException;

public class Products extends Controller {

    //public static Result GO_HOME = redirect(routes.Application.index());

    private static final Form<S3File> mediaUploadForm = Form.form(S3File.class);

    //
    // views used by angular routes
    //

    public static Result main(String path) {
        return ok(views.html.product_main.render());
    }

    public static Result list() {
        return ok(views.html.product_list.render());
    }

    public static Result details() {
        return ok(views.html.product_details.render());
    }

    public static Result about() {
        return ok(views.html.product_tabs.about.render());
    }

    public static Result bar() {
        return ok(views.html.product_tabs.experience_bar.render());
    }

    public static Result gallery() {
        return ok(views.html.product_tabs.experience_gallery.render());
    }

    public static Result add() {
        return ok(views.html.product_add.render());
    }

    //
    // views used by play
    //

//    public static Result details1(Long id) {
//        //Product product = new Product().findbyID(id);
//        Product product1 = new Product().getDetailwithMedia(id);
//        return ok(product_item.render(product1, mediaUploadForm));
//    }
//
//    public static Result detailsBySlug(String name) {
//        Product product = new Product().findbySlug(name);
//        return ok(product_item.render(product, mediaUploadForm));
//    }

    // For server-side defined routes that hand off rending to
    // angular.js
//    public static Result angularApp() {
//        Product product = new Product().findbySlug(name);
//        return ok(product_item.render(product, mediaUploadForm));
//    }

    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

    @BodyParser.Of(BodyParser.Json.class)
//    @Cached(key = "http:products:get:product", duration = 300)
    public static Result getProducts()
    {
        ArrayNode node = null;
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        LZ4Factory factory = LZ4Factory.fastestInstance();

        byte[] compressed = (byte[]) play.cache.Cache.get("http:product:get:product:lz4");
        if (compressed != null) {
            Logger.debug("product:lz4: cache hit " + compressed.length);
//            byte[] uncompressed = new byte[compressed.length*10];
            LZ4SafeDecompressor decompressor = factory.safeDecompressor();
            int maxDecompressedLength = compressed.length*10;
            byte[] restored = new byte[maxDecompressedLength];
            restored = decompressor.decompress(compressed, maxDecompressedLength);

            //int decompressedLength = decompressor.decompress(compressed, 0, compressed.length, uncompressed, 0, compressed.length*100);
            Logger.debug("decompressed length " + restored.length);
            try {
                String result = new String(restored, "UTF-8");
                node = (ArrayNode) mapper.readTree(result);
            } catch(Exception e){
                Logger.debug(e.getMessage());
            };
        }
        else{
            Logger.debug("product:lz4: cache miss");
            node =  Product.range();
            String src = node.toString();
            try {
                byte[] data = src.getBytes("UTF-8");
                int decompressedLength = data.length;
                Logger.debug("product:lz4: data len" + data.length);
                // compress data
                LZ4Compressor compressor = factory.fastCompressor();
                int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
                compressed = new byte[maxCompressedLength];
                int compressedLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
                Logger.debug("product:lz4: comp len" + compressedLength);
                byte[] finalCompressedArray = Arrays.copyOf(compressed, compressedLength);
                Logger.debug("product:lz4: final len" + finalCompressedArray.length);

                play.cache.Cache.set("http:product:get:product:lz4", finalCompressedArray, 300); // 5 min
            } catch(Exception e){
                Logger.debug(e.getMessage());
            }
        }

        //j.set()
        //Cache.get("http:products:get:product");

        return ok(node);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProduct(Long id)
    {
        Product item = new Product().findbyID(id);
        return item == null ? notFound("Product not found [" + id + "]") : ok(item.toJson());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProduct(String slug)
    {
        Product item = new Product().findbySlug(slug);
        return item == null ? notFound("Product not found [" + slug + "]") : ok(item.toJson());
    }

    @BodyParser.Of(BodyParser.Json.class)
    @SubjectPresent
    public static Result createProduct()
    {
        Logger.debug("create product ");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                User user = Application.getLocalUser(ctx().session());

                Product prod = new Product();
                prod.productName = json.findPath("name").textValue();
                prod.slug = json.findPath("slug").textValue();
                prod.productDescription = json.findPath("tagline").textValue();
                prod.urlHome = json.findPath("urlHome").textValue();
                prod.urlFacebook = json.findPath("urlFacebook").textValue();
                prod.urlTwitter = json.findPath("urlTwitter").textValue();
                prod.urlCrowdFundingCampaign = json.findPath("urlCrowdFundingCampaign").textValue();

                JsonNode compid = json.path("company").path("id");
                JsonNode compname = json.path("company").path("companyName");

                // check if company exists or needs to be created
                if(user.company==null){
                    Logger.debug("creating company...");
                    if(!compname.isMissingNode()){
                        // create new company
                        Company comp = new Company();
                        comp.companyName = compname.asText();
                        comp.save();

                        // update product with the new company
                        prod.company = comp;

                        // update user with the new company
                        user.company = comp;
                        user.save();
                    }
                    else{
                        return badRequest("Not found [company name]:");
                    }
                }
                else{
                    prod.company = user.company;
                }

                // Save the product
                if (prod.productName != null) {
                    prod.save();
                    return created(prod.toJson());
                } else {
                    return badRequest("Missing parameter [name]");
                }
            }
            catch(PersistenceException e){
                return play.mvc.Results.badRequest(e.getMessage());
            }
        }
    }

//    public static Result updateProduct(Long id)
//    {
//        Product existing = new Product().findbyID(id);
//        if(existing != null) {
//            models.Product updated = Json.fromJson(request().body().asJson(), models.Product.class);
//            if(updated.productName != null) { existing.productName = updated.productName; };
//            if(updated.productEAN != null) { existing.productEAN = updated.productEAN; };
//            if(updated.productDescription != null) { existing.productDescription = updated.productDescription; };
//            if(updated.productLongDescription != null) { existing.productLongDescription = updated.productLongDescription; };
//            if(updated.urlHome != null) { existing.urlHome = updated.urlHome; };
//            if(updated.urlFacebook != null) { existing.urlFacebook = updated.urlFacebook; };
//            if(updated.urlTwitter != null) { existing.urlTwitter = updated.urlTwitter; };
//            existing.save();
//            return ok(existing.toJson());
//        }
//        else{
//            return play.mvc.Results.notFound("Product not found");
//        }
//    }

    @SubjectPresent
    public static Result updateProduct(String slug)
    {
        User user = Application.getLocalUser(ctx().session());
        Product prod = user.hasProduct(slug);
        if(prod != null) {
            JsonNode json = request().body().asJson();
            prod.productName = json.findPath("name").textValue();
            prod.slug = json.findPath("slug").textValue();
            prod.productDescription = json.findPath("tagline").textValue();
            prod.urlHome = json.findPath("urlHome").textValue();
            prod.urlFacebook = json.findPath("urlFacebook").textValue();
            prod.urlTwitter = json.findPath("urlTwitter").textValue();
            prod.urlCrowdFundingCampaign = json.findPath("urlCrowdFundingCampaign").textValue();
            prod.productLongDescription = json.findPath("description").textValue();
            prod.featureList = json.findPath("features").textValue();
            prod.missionStatement = json.findPath("mission").textValue();
            prod.productStory = json.findPath("story").textValue();
            prod.save();
            return ok(prod.toJson());
        }
        else{
            return play.mvc.Results.notFound("Product not found");
        }
    }

    @SubjectPresent
    public static Result deleteProduct(Long id)
    {
        Product del = new Product().findbyID(id);
        if(del != null) {
            del.delete();
            return ok();
        }
        else{
            return play.mvc.Results.notFound("Product not found");
        }
    }

/*
	public static Result save() {
	Form<Product> boundForm = productForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
      return badRequest(details.render(boundForm));
    }

    Product product = boundForm.get();

    List<Tag> tags = new ArrayList<Tag>();
    for (Tag tag : product.tags) {
      if (tag.id != null) {
        tags.add(Tag.findById(tag.id));
      }
    }
    product.tags = tags;

    product.save();
    flash("success",
        String.format("Successfully added product %s", product));

    return redirect(routes.Products.list(1));
	}*/

}
