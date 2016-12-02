package com.app.pug.pug.like_reply;

import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.activities.ActivitySplashScreen;
import com.app.pug.pug.login_signup.LoginSignup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zeryan on 2/24/16.
 */
public class LikeReplyAction extends AsyncTask<Void, Void, Boolean> {
    private AppCompatActivity context;
    private JSONObject passedJson;
    private String url_address = ActivityMainDataSet.url_address;
    private int likeAction;

    public LikeReplyAction(AppCompatActivity context, JSONObject likeJson) {
        this.context = context;
        this.passedJson = likeJson;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (likePost(passedJson)) {

                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean likePost(JSONObject passedJson) throws IOException, JSONException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject likeJSON = new JSONObject();
            likeJSON.put("action", "like_reply");
            likeJSON.put("user_id", userID);
            likeJSON.put("app_id", dID);
            likeJSON.put("lat", ActivitySplashScreen.locationHandler.getLastLocation().getLatitude());
            likeJSON.put("log", ActivitySplashScreen.locationHandler.getLastLocation().getLongitude());
            String post_id = passedJson.getString("post_id");
            String replyID = passedJson.getString("reply_id");
            likeJSON.put("reply_id", replyID);
            likeJSON.put("post_id", post_id);
            likeJSON.put("like", likeAction);
            URL url = new URL(url_address);
            HttpsURLConnection loginConnection = (HttpsURLConnection) url.openConnection();
            loginConnection.setDoOutput(true);
            PrintWriter likeWriter = new PrintWriter(loginConnection.getOutputStream());
            BufferedWriter bufLike = new BufferedWriter(likeWriter);
            bufLike.write(likeJSON.toString());
            bufLike.flush();
            BufferedReader bufLikeReader = new BufferedReader(new InputStreamReader(loginConnection.getInputStream()));
            String line = null;
            StringBuffer bufResponse = new StringBuffer();
            while ((line = bufLikeReader.readLine()) != null) {
                bufResponse.append(line);
            }
            if (!bufResponse.toString().equals("")) {
                JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("like_res").equals("success")) {
                    bufLikeReader.close();
                    bufLike.close();
                    loginConnection.disconnect();
                    return true;
                }
            }

        }
        return false;
    }

    public void setLikeAction(int likeAction) {
        this.likeAction = likeAction;
    }
}
