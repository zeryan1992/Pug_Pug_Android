package com.app.pug.pug.get_indi_replies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.pug.pug.R;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.one_post_view.IndividualPosts;
import com.app.pug.pug.one_post_view.RepliesAdapter;
import com.app.pug.pug.pro_posts.ProfileGetPosts;
import com.app.pug.pug.activities.ActivitySplashScreen;
import com.app.pug.pug.global_stuff.GetglobalFeed;
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.login_signup.LoginSignup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zeryan on 2/24/16.
 */
public class GetReplies extends AsyncTask<Void, Void, Boolean> {
    public static ArrayList<JSONObject> replies = new ArrayList<>();
    private final Animation animation;
    private int currentPos;
    private AppCompatActivity context;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    private String url_address = ActivityMainDataSet.url_address;
    private String from;
    private ImageView loading;
    boolean refresh;
    HashMap<Integer,Bitmap> bitmaps=new HashMap<>();
    public GetReplies(AppCompatActivity context, int postion, String from, ImageView loading, boolean refresh) {
        this.context = context;
        this.currentPos = postion;
        this.from = from;
        this.loading=loading;
        this.refresh=refresh;
        animation= AnimationUtils.loadAnimation(context, R.anim.anim);
        animation.setDuration(1000);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (refresh)
        {
            IndividualPosts.refreshLayout.setRefreshing(true);
        }
        else {
            loading.startAnimation(animation);
            loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (getReplies()) {
                if (!replies.isEmpty()) {

                }
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

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (refresh)
        {
            IndividualPosts.refreshLayout.setRefreshing(false);
            IndividualPosts.recyclerView.setAdapter(IndividualPosts.adapter);

        }
        else
        {
            animation.cancel();
            loading.setAnimation(null);
            loading.setVisibility(View.GONE);
            IndividualPosts.recyclerView.setAdapter(IndividualPosts.adapter);

        }
    }

    public boolean getReplies() throws IOException, JSONException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {

            JSONObject getRepliesReq = new JSONObject();
            getRepliesReq.put("action", "retrieve_replies");
            getRepliesReq.put("user_id", userID);
            getRepliesReq.put("app_id", dID);
            getRepliesReq.put("lat", location.getLatitude());
            getRepliesReq.put("log", location.getLongitude());
            if (from.equals("my_posts")) {
                getRepliesReq.put("post_id", ProfileGetPosts.posts.get(currentPos).getString("post_id"));
            }
            else if (from.equals("local")) {
                getRepliesReq.put("post_id", GetFeed.posts.get(currentPos).getString("post_id"));
            }
            else if (from.equals("global")) {
                getRepliesReq.put("post_id", GetglobalFeed.postsGlobal.get(currentPos).getString("post_id"));
            }
            else if (from.equals("my_replies"))
            {
                getRepliesReq.put("post_id", ProfileGetPosts.posts.get(currentPos).getString("post_id"));

            }
            URL url = new URL(url_address);
            HttpsURLConnection loginConnection = (HttpsURLConnection) url.openConnection();
            loginConnection.setRequestProperty("Accept-Charset", "UTF-8");
            loginConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            loginConnection.setDoOutput(true);
            PrintWriter loginWriter = new PrintWriter(loginConnection.getOutputStream());
            BufferedWriter bufLOGINWriter = new BufferedWriter(loginWriter);
            bufLOGINWriter.write(getRepliesReq.toString());
            bufLOGINWriter.flush();
            BufferedReader bufLOGINReader = new BufferedReader(new InputStreamReader(loginConnection.getInputStream()));
            String line = null;
            StringBuffer bufResponse = new StringBuffer();
            while ((line = bufLOGINReader.readLine()) != null) {
                bufResponse.append(line);
            }

            if (!bufResponse.toString().equals("")) {
                final JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("retrieve_res").equals("success"))
                {
                    JSONArray repliesJSON = resJSON.getJSONArray("replies");
                    if (!replies.isEmpty())
                    {
                        replies.clear();
                    }
                    if (!RepliesAdapter.detect.isEmpty())
                    {
                        RepliesAdapter.detect.clear();
                    }
                    for (int i = 0; i < repliesJSON.length(); i++)
                    {
                        replies.add(i, repliesJSON.getJSONObject(i));
                    }
                    if(IndividualPosts.fillReplySparse())
                    {
                        bufLOGINReader.close();
                        bufLOGINWriter.close();
                        loginConnection.disconnect();

                        return true;
                    }


                } else if (resJSON.get("retrieve_res").equals("fail")) {
                }
            }

        }


        return false;
    }
    public int getBestSampleSize(BitmapFactory.Options opts, int reqHeight,int reqWidth)
    {
        int height=opts.outHeight;
        int width=opts.outWidth;
        int sampleSize=1;
        if (height>reqHeight||width>reqWidth)
        {
            final int halfHeight=height/2;
            final int halfWidth=width/2;
            while (halfHeight/sampleSize>reqHeight&&halfWidth/sampleSize>reqWidth)
            {
                sampleSize *=4;
            }
        }
        return sampleSize;
    }
}
