package com.app.pug.pug.send_stuff;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.get_indi_replies.GetReplies;
import com.app.pug.pug.one_post_view.IndividualPosts;
import com.app.pug.pug.one_post_view.RepliesAdapter;
import com.app.pug.pug.pro_posts.ProfileGetPosts;
import com.app.pug.pug.activities.ActivitySplashScreen;
import com.app.pug.pug.global_stuff.GetglobalFeed;
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.login_signup.LoginSignup;

import org.joda.time.DateTime;
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
 * Created by zeryan on 2/23/16.
 */
public class SendAreply extends AsyncTask<Void, Void, Boolean> {
    private AppCompatActivity activity;
    private EditText editText;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    private String url_address = ActivityMainDataSet.url_address;
    private int current;
    String from;
    ProgressDialog progressDialog;

    public SendAreply(EditText editText, AppCompatActivity activity, int pos, String from, ProgressDialog sendLoading) {
        this.activity = activity;
        this.editText = editText;
        this.current = pos;
        this.from=from;
        this.progressDialog=sendLoading;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Posting a reply...");
        progressDialog.show();

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if (sendReply()) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean sendReply() throws JSONException, IOException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject sendReplyJS = new JSONObject();
            sendReplyJS.put("action", "post_reply");
            sendReplyJS.put("user_id", userID);
            sendReplyJS.put("app_id", dID);
            if (from.equals("local")) {
                sendReplyJS.put("post_id", GetFeed.posts.get(current).get("post_id"));
            }
            else if (from.equals("global"))
            {
                sendReplyJS.put("post_id", GetglobalFeed.postsGlobal.get(current).get("post_id"));
            }
            else if (from.equals("my_posts"))
            {
                sendReplyJS.put("post_id", ProfileGetPosts.posts.get(current).get("post_id"));

            }
            else if (from.equals("my_replies"))
            {
                sendReplyJS.put("post_id", ProfileGetPosts.posts.get(current).get("post_id"));

            }
            sendReplyJS.put("lat", location.getLatitude());
            sendReplyJS.put("log", location.getLongitude());
            sendReplyJS.put("reply_text", editText.getText().toString());
            DateTime date=new DateTime();
            StringBuffer dateJ=new StringBuffer();
            dateJ.append(date.getYear() + "-" + date.getMonthOfYear() + "-" + date.getDayOfMonth());
            sendReplyJS.put("date", dateJ.toString());
            StringBuffer time=new StringBuffer();
            time.append(date.getHourOfDay() + ":" + date.getMinuteOfHour() + ":" + date.getSecondOfMinute());
            sendReplyJS.put("time", time.toString());

            URL url = new URL(url_address);
            HttpsURLConnection loginConnection = (HttpsURLConnection) url.openConnection();
            loginConnection.setRequestProperty("Accept-Charset", "UTF-8");
            loginConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            loginConnection.setDoOutput(true);
            PrintWriter loginWriter = new PrintWriter(loginConnection.getOutputStream());
            BufferedWriter bufLOGINWriter = new BufferedWriter(loginWriter);
            bufLOGINWriter.write(sendReplyJS.toString());
            bufLOGINWriter.flush();
            BufferedReader bufLOGINReader = new BufferedReader(new InputStreamReader(loginConnection.getInputStream()));
            String line = null;
            StringBuffer bufResponse = new StringBuffer();
            while ((line = bufLOGINReader.readLine()) != null) {
                bufResponse.append(line);
            }
            if (!bufResponse.toString().equals("")) {
                final JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("reply_res").equals("success"))
                {
                    JSONObject object=resJSON.getJSONObject("reply");
                    GetReplies.replies.add(GetReplies.replies.size(), object);
                    IndividualPosts.integerHashMap.clear();
                    RepliesAdapter.detect.clear();
                    if (IndividualPosts.fillReplySparse())
                    {

                        bufLOGINReader.close();
                        bufLOGINWriter.close();
                        loginConnection.disconnect();
                        return true;

                    }
                    bufLOGINReader.close();
                    bufLOGINWriter.close();
                    loginConnection.disconnect();

                }
                bufLOGINReader.close();
                bufLOGINWriter.close();
                loginConnection.disconnect();
            }

        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressDialog.dismiss();
        editText.setText("");
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
