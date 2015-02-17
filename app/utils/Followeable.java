package utils;

import models.Follow;

import java.util.List;

/**
 * Created by Nitin on 2/16/15
 * Follow model for following a product or a user
 */
public interface Followeable {
    public void follow(Long userId);
    public void unFollow(Long userId);
    public void removeAllFollowers();
    /*
     * Get all Followers on an object
     */
    public List<Follow> getAllFollowers();
    
    /*
     * Get all Followings by current user
     */
    public List<Follow> getUserFollowings(Long userId);

  

}

