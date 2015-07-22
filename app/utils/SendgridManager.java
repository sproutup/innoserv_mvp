package utils;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import controllers.SendgridController;
import models.User;
import play.Play;

import java.util.List;

/**
 * Created by Apurv on 7/22/15.
 */
public class SendgridManager implements Runnable {
    @Override
    public void run() {
        List<User> userList = User.find.all();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < userList.size(); i++) {
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("name", userList.get(i).name);
                userDetails.put("email", userList.get(i).email);
                userDetails.put("active", userList.get(i).active);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(userDetails);
        }
        SendgridController.pushToQueue("verifyAll", Play.application().configuration().getString("sendgrid.list"), jsonArray.toString());
    }
}
