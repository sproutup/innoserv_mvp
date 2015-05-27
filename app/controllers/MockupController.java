package controllers;

// These are just used for mockup pages

import models.S3File;
import models.Product;
import play.data.Form;
import play.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import java.util.List;
import javax.persistence.PersistenceException;
import views.html.mockups.*;

public class MockupController extends Controller {

    private static final Form<S3File> mediaUploadForm = Form.form(S3File.class);
    private static final Boolean mockups_enabled = Boolean.parseBoolean(Play.application().configuration().getString("mockups.enabled"));

    public static Result detailsAboutBySlug(String name) {
        if(!mockups_enabled){return notFound();};

        Product product = new Product().findbySlug(name);
        return ok(product_mock_1.render(product, mediaUploadForm));
    }

    public static Result detailsBarBySlug(String name) {
        if(!mockups_enabled){return notFound();};

        Product product = new Product().findbySlug(name);
        return ok(product_mock_2.render(product, mediaUploadForm));
    }

    public static Result detailsGalleryBySlug(String name) {
        if(!mockups_enabled){return notFound();};

        Product product = new Product().findbySlug(name);
        return ok(product_mock_3.render(product, mediaUploadForm));
    }

    public static Result login() {
        if(!mockups_enabled){return notFound();};

        return ok(login_mock.render());
    }

    public static Result resetPassword() {
        if(!mockups_enabled){return notFound();};

        return ok(reset_password_mock.render());
    }

    public static Result userProfile() {
        if(!mockups_enabled){return notFound();};

        return ok(user_profile_mock.render());
    }

    public static Result creatorDashboard() {
        if(!mockups_enabled){return notFound();};

        return ok(dashboard_mock.render());
    }

    public static Result forCreator() {
        if(!mockups_enabled){return notFound();};

        return ok(for_creator_mock.render());
    }

    public static Result productList () {
        if(!mockups_enabled){return notFound();};

        return ok(product_list_mock.render());
    }

    public static Result userSettings() {
        if(!mockups_enabled){return notFound();};

        return ok(user_settings_mock.render());
    }

    public static Result addNewProduct() {
        if(!mockups_enabled){return notFound();};

        return ok(add_new_product_mock.render());
    }

    public static Result wizard() {
        if(!mockups_enabled){return notFound();};

        return ok(wizard.render());
    }

    public static Result tryout() {
        if(!mockups_enabled){return notFound();};

        return ok(tryout.render());
    }
}
