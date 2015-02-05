package utils;

import models.Tag;
import play.db.ebean.Model;

import java.util.List;

/**
 * Created by peter on 2/4/15.
 */
public interface Taggable {
    public void addTag(String name);
    public void removeTag(String name);
    public void removeAllTags();
    public List<Tag> getAllTags();
    }

/*
Example of a Taggable interface implementation

@Override
public void addTag(String name) {
    Tag.addTag(name, id, this.getClass().getName());
    }
    @Override
    public void removeTag(String name) {
        Tag.removeTag(name, id, this.getClass().getName());
    }
    @Override
    public void removeAllTags() {
        Tag.removeAllTags(id, this.getClass().getName());
    }
    @Override
    public List<Tag> getAllTags() {
        return Tag.getAllTags(id, this.getClass().getName());
    }
    public List<Product> findAllByTag(String name) {

        List<Product> products = Product.find
                .query()
                .setRawSql(Tag.findAllByTagRawSql(name, this.getClass().getName()))
                .where()
                .findList();

        return products;
    }

 */
