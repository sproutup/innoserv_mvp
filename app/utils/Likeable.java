package utils;

import models.Likes;

import java.util.List;

/**
 * Created by peter on 2/4/15.
 */
public interface Likeable {
    public void addLike(Long userId);
    public void removeLike(Long userId);
    public void removeAllLikes();
    public List<Likes> getAllLikes();
    }

/*
Example of a Likeable interface implementation

@Override

 */
