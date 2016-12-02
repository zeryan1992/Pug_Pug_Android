package com.app.pug.pug.send_stuff;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.activities.ActivitySplashScreen;
import com.app.pug.pug.get_indi_replies.GetReplies;
import com.app.pug.pug.global_stuff.GetglobalFeed;
import com.app.pug.pug.global_stuff.GetglobalFeedForEarth;
import com.app.pug.pug.global_stuff.GlobalAdapetr;
import com.app.pug.pug.global_stuff.GlobalFeedFragment;
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.local_feed.IndividualItemsAdapter;
import com.app.pug.pug.local_feed.Yammers;
import com.app.pug.pug.login_signup.LoginSignup;
import com.app.pug.pug.one_post_view.IndividualPosts;
import com.app.pug.pug.pro_posts.MyPostsAdapter;
import com.app.pug.pug.pro_posts.MyPostsFrag;
import com.app.pug.pug.pro_posts.ProfileGetPosts;
import com.app.pug.pug.pro_replies.MyRepliesAdapter;
import com.app.pug.pug.pro_replies.MyRepliesFrag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zeryan on 3/10/16.
 */
public class SendReport extends AsyncTask<Void,Void,Boolean> {

    private  AppCompatActivity context;
    private  File fileID;
    private int postion;
    String from;
    String type;
    String level;
    String action;
    ProgressDialog proDilog;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    String rep_rep;


    public SendReport(AppCompatActivity context, int postion, String from, String type, String level, String action, ProgressDialog proDilog, String delete_reply) {
        this.context = context;
        fileID = new File(context.getFilesDir().getPath() + "/id.txt");
        this.postion=postion;
        this.from=from;
        this.type=type;
        this.level=level;
        this.action=action;
        this.proDilog=proDilog;
        this.rep_rep=delete_reply;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        proDilog.show();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (action.equals("report")) {
            try {
                if (sendReport()) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (action.equals("delete"))
        {
            try {
                if (delete())
                {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (action.equals("delete_reply"))
        {
            try {
                if(deleteReply())
                {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean)
        {
            if (!rep_rep.equals("delete_reply"))
            {
                if (action.equals("delete")) {
                    if (from.equals("local")) {
                        Log.e("pos", GetFeed.posts.get(postion).toString());

                        GetFeed.posts.remove(postion);
                        Log.e("d", "delete local");
                        if (IndividualItemsAdapter.detect.containsKey(postion)) {
                            IndividualItemsAdapter.detect.remove(postion);
                        }
                        if (GetFeed.bitmaps.containsKey(postion)) {
                            GetFeed.bitmaps.remove(postion);
                        }
                        if (Yammers.integerHashMap.contains(postion)) {
                            Yammers.integerHashMap.remove(postion);
                        }
                        Yammers.adapter.notifyItemRemoved(postion);
                        Yammers.adapter.notifyDataSetChanged();
                        Yammers.recyclerView.clearAnimation();
                        Yammers.recyclerView.setAnimation(null);
                        Log.e("d", "delete local 2");

                    } else if (from.equals("global")) {
                        GetglobalFeed.postsGlobal.remove(postion);
                        if (GlobalAdapetr.detect.containsKey(postion)) {
                            GlobalAdapetr.detect.remove(postion);
                        }

                        if (GlobalFeedFragment.integerHashMap.contains(postion)) {
                            GlobalFeedFragment.integerHashMap.remove(postion);
                        }
                        if (GetglobalFeed.bitmaps.containsKey(postion)) {
                            GetglobalFeed.bitmaps.remove(postion);
                        }
                        GlobalFeedFragment.adapter.notifyItemRemoved(postion);
                        GlobalFeedFragment.adapter.notifyDataSetChanged();
                        GlobalFeedFragment.recyclerView.clearAnimation();
                        GlobalFeedFragment.recyclerView.setAnimation(null);
                    } else if (from.equals("my_posts")) {
                        ProfileGetPosts.posts.remove(postion);
                        if (MyPostsAdapter.detect.containsKey(postion)) {
                            MyPostsAdapter.detect.remove(postion);
                        }
                        if (MyPostsFrag.integerHashMap.contains(postion)) {
                            MyPostsFrag.integerHashMap.remove(postion);
                        }
                        if (ProfileGetPosts.bitmaps.containsKey(postion)) {
                            ProfileGetPosts.bitmaps.remove(postion);
                        }
                        MyPostsFrag.adapter.notifyItemRemoved(postion);
                        MyPostsFrag.adapter.notifyDataSetChanged();
                        MyPostsFrag.recyclerView.clearAnimation();
                        MyPostsFrag.recyclerView.setAnimation(null);

                    } else if (from.equals("my_replies")) {
                        ProfileGetPosts.posts.remove(postion);
                        if (MyRepliesAdapter.detect.containsKey(postion)) {
                            MyRepliesAdapter.detect.remove(postion);
                        }
                        if (MyRepliesFrag.integerHashMap.contains(postion)) {
                            MyRepliesFrag.integerHashMap.remove(postion);
                        }
                        if (ProfileGetPosts.bitmaps.containsKey(postion)) {
                            ProfileGetPosts.bitmaps.remove(postion);
                        }
                        MyRepliesFrag.adapter.notifyItemRemoved(postion);
                        MyRepliesFrag.adapter.notifyDataSetChanged();
                        MyRepliesFrag.recyclerView.clearAnimation();
                        MyRepliesFrag.recyclerView.setAnimation(null);
                    }

                } else if (action.equals("report")) {
                    if (from.equals("local")) {
                        Log.e("pos", GetFeed.posts.get(postion).toString());
                        GetFeed.posts.remove(postion);
                        if (Yammers.integerHashMap.contains(postion)) {
                            Yammers.integerHashMap.remove(postion);
                        }
                        if (GetFeed.bitmaps.containsKey(postion)) {
                            GetFeed.bitmaps.remove(postion);
                        }
                        Yammers.adapter.notifyItemRemoved(postion);
                        Yammers.adapter.notifyDataSetChanged();
                        Yammers.recyclerView.clearAnimation();
                        Yammers.recyclerView.setAnimation(null);
                    } else if (from.equals("global")) {
                        GetglobalFeed.postsGlobal.remove(postion);
                        if (GlobalFeedFragment.integerHashMap.contains(postion)) {
                            GlobalFeedFragment.integerHashMap.remove(postion);
                        }
                        if (GetglobalFeed.bitmaps.containsKey(postion)) {
                            GetglobalFeed.bitmaps.remove(postion);
                        }
                        GlobalFeedFragment.adapter.notifyItemRemoved(postion);
                        GlobalFeedFragment.adapter.notifyDataSetChanged();
                        GlobalFeedFragment.recyclerView.clearAnimation();
                        GlobalFeedFragment.recyclerView.setAnimation(null);
                    } else if (from.equals("my_posts")) {
                        ProfileGetPosts.posts.remove(postion);
                        if (MyPostsFrag.integerHashMap.contains(postion)) {
                            MyPostsFrag.integerHashMap.remove(postion);
                        }
                        if (ProfileGetPosts.bitmaps.containsKey(postion)) {
                            ProfileGetPosts.bitmaps.remove(postion);
                        }
                        MyPostsFrag.adapter.notifyItemRemoved(postion);
                        MyPostsFrag.adapter.notifyDataSetChanged();
                        MyPostsFrag.recyclerView.clearAnimation();
                        MyPostsFrag.recyclerView.setAnimation(null);

                    } else if (from.equals("my_replies")) {
                        ProfileGetPosts.posts.remove(postion);
                        if (MyRepliesFrag.integerHashMap.contains(postion)) {
                            MyRepliesFrag.integerHashMap.remove(postion);
                        }
                        if (ProfileGetPosts.bitmaps.containsKey(postion)) {
                            ProfileGetPosts.bitmaps.remove(postion);
                        }
                        MyRepliesFrag.adapter.notifyItemRemoved(postion);
                        MyRepliesFrag.adapter.notifyDataSetChanged();
                        MyRepliesFrag.recyclerView.clearAnimation();
                        MyRepliesFrag.recyclerView.setAnimation(null);
                    }

                }
            }
            else
            {
                IndividualPosts.adapter.notifyItemRemoved(postion);

            }
        }
        proDilog.dismiss();
    }

    public boolean deleteReply() throws JSONException, IOException {
        Log.e("pos",""+postion);
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject sendReplyJS = new JSONObject();
            sendReplyJS.put("action", action);
            sendReplyJS.put("user_id", userID);
            sendReplyJS.put("app_id", dID);
            sendReplyJS.put("lat",location.getLatitude());
            sendReplyJS.put("log",location.getLongitude());
            sendReplyJS.put("reply_id", GetReplies.replies.get(postion).getString("reply_id"));
            sendReplyJS.put("post_id",GetReplies.replies.get(postion).getString("post_id"));
            URL url = new URL(ActivityMainDataSet.url_address);
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
                Log.e("buf",bufResponse.toString());
                final JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("delete_res").equals("success"))
                {
                    GetReplies.replies.remove(postion);
                    return true;

                } else if (resJSON.get("delete_res").equals("fail")) {
                    return false;
                }
            }

        }
        return false;
    }
    public boolean delete() throws JSONException, IOException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject sendReplyJS = new JSONObject();
            sendReplyJS.put("action", action);
            sendReplyJS.put("user_id", userID);
            sendReplyJS.put("app_id", dID);
            sendReplyJS.put("lat",location.getLatitude());
            sendReplyJS.put("log",location.getLongitude());
            if (from.equals("local")) {
                sendReplyJS.put("post_id", GetFeed.posts.get(postion).get("post_id"));
            }
            else if (from.equals("global"))
            {
                sendReplyJS.put("post_id", GetglobalFeed.postsGlobal.get(postion).get("post_id"));
            }
            else if (from.equals("my_posts"))
            {
                sendReplyJS.put("post_id", ProfileGetPosts.posts.get(postion).get("post_id"));

            }
            else if (from.equals("my_replies"))
            {
                sendReplyJS.put("post_id", ProfileGetPosts.posts.get(postion).get("post_id"));

            }
            else if (from==null)
            {
                sendReplyJS.put("post_id",GetReplies.replies.get(postion).get("reply_id"));
            }

            URL url = new URL(ActivityMainDataSet.url_address);
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
                Log.e("buf",bufResponse.toString());
                final JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("delete_res").equals("success"))
                {
                    return true;

                } else if (resJSON.get("delete_res").equals("fail")) {
                    return false;
                }
            }

        }
        return false;
    }
    public boolean sendReport() throws IOException, JSONException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject reportJson = new JSONObject();
            reportJson.put("action", action);
            reportJson.put("user_id", userID);
            reportJson.put("app_id", dID);
            if (from!=null&&from.equals("my_posts")) {
                reportJson.put("post_id", ProfileGetPosts.posts.get(postion).getString("post_id"));
            }
            else if (from!=null&&from.equals("local")) {
                reportJson.put("post_id", GetFeed.posts.get(postion).getString("post_id"));
            }
            else if (from!=null&&from.equals("global")) {
                reportJson.put("post_id", GetglobalFeed.postsGlobal.get(postion).getString("post_id"));
            }
            else if (from!=null&&from.equals("my_replies"))
            {
                reportJson.put("post_id", ProfileGetPosts.posts.get(postion).getString("post_id"));

            }
            else
            {
                reportJson.put("post_id",GetReplies.replies.get(postion).getString("reply_id"));
            }
            reportJson.put("lat", ActivitySplashScreen.locationHandler.getLastLocation().getLatitude());
            reportJson.put("log", ActivitySplashScreen.locationHandler.getLastLocation().getLongitude());

            reportJson.put("type",type);
            reportJson.put("level",level);
            URL url = new URL(ActivityMainDataSet.url_address);
            HttpsURLConnection loginConnection = (HttpsURLConnection) url.openConnection();
            loginConnection.setDoOutput(true);
            PrintWriter loginWriter = new PrintWriter(loginConnection.getOutputStream());
            BufferedWriter bufLOGINWriter = new BufferedWriter(loginWriter);
            bufLOGINWriter.write(reportJson.toString());
            bufLOGINWriter.flush();
            BufferedReader bufLOGINReader = new BufferedReader(new InputStreamReader(loginConnection.getInputStream()));
            String line = null;
            StringBuffer bufResponse = new StringBuffer();
            while ((line = bufLOGINReader.readLine()) != null) {
                bufResponse.append(line);
            }
            if (!bufResponse.toString().equals("")) {
                JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("report_res").equals("success")) {
                    bufLOGINReader.close();
                    bufLOGINWriter.close();
                    loginConnection.disconnect();
                    return true;
                }
            }

        }
        return false;
    }

}
