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

    public static Result detailsAboutBySlug(String name) {
        Product product = new Product().findbySlug(name);
        return ok(product_mock_1.render(product, mediaUploadForm));
    }

    public static Result detailsBarBySlug(String name) {
        Product product = new Product().findbySlug(name);
        return ok(product_mock_2.render(product, mediaUploadForm));
    }

    public static Result detailsGalleryBySlug(String name) {
        Product product = new Product().findbySlug(name);
        return ok(product_mock_3.render(product, mediaUploadForm));
    }

    public static Result login() {
        return ok(login_mock.render());
    }

    public static Result resetPassword() {
        return ok(reset_password_mock.render());
    }

    public static Result userProfile() {
        return ok(user_profile_mock.render());
    }

    public static Result creatorDashboard_1() {
        return ok(creator_dashboard_mock_1.render());
    }

    public static Result creatorDashboard_2() {
        return ok(creator_dashboard_mock_2.render());
    }

    public static Result creatorDashboard_3() {
        return ok(creator_dashboard_mock_3.render());
    }

    public static Result forCreator() {
        return ok(for_creator_mock.render());
    }

    public static Result productList () {
        return ok(product_list_mock.render());
    }

    public static Result userSettings() {
        return ok(user_settings_mock.render());
    }
}
