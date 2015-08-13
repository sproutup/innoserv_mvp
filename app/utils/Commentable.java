package utils;

import models.Comment;

import java.util.List;

/**
 * Created by peter on 2/4/15.
 */
public interface Commentable {
    public void addComment(Long userId, String comment);
    public void removeComment(Long commentId);
    public void removeAllComments();
    public List<Comment> getAllComments();
    }

/*
Example of a Commentable interface implementation

    @Override
    public void addComment(Long userId, String comment) {
        Comment.addComment(id, comment, this.getClass().getName());
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
