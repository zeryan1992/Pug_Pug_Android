package com.app.pug.pug.local_feed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.pug.pug.R;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.activities.ActivitySplashScreen;
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
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zeryan on 2/14/16.
 */
public class GetFeed extends AsyncTask<Void, Void, Boolean> {
    public static ArrayList<JSONObject> posts = new ArrayList<>();
    private AppCompatActivity context;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    private String url_address = ActivityMainDataSet.url_address;
    public static HashMap<Integer,Bitmap> bitmaps=new HashMap<>();
    boolean is_refresh;
    private ImageView loading;
    private Animation animation;


    public GetFeed(AppCompatActivity context, ImageView loadingProgress, boolean is_refresh) {
        this.context = context;
        this.is_refresh=is_refresh;
        this.loading=loadingProgress;
        animation= AnimationUtils.loadAnimation(context, R.anim.anim);
        animation.setDuration(1000);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (is_refresh)
        {

            if (loading.getVisibility()==View.VISIBLE)
            {
                loading.setVisibility(View.GONE);
            }
        }
        else
        {
            loading.startAnimation(animation);
            loading.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (getFeed()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (is_refresh)
        {
            if (Yammers.fillSparse()) {
                Yammers.adapter.notifyDataSetChanged();
                Yammers.refreshLayout.setRefreshing(false);
                Yammers.recyclerView.setAdapter(Yammers.adapter);
            }
        }
        else
        {
            if (Yammers.recyclerView.getAdapter()==null)
            {
                if (Yammers.fillSparse()) {
                        //loading.getAnimation().cancel();
                        loading.setAnimation(null);
                        loading.setVisibility(View.INVISIBLE);
                        Yammers.recyclerView.setAdapter(Yammers.adapter);


                }
            }
            else if (loading.getVisibility()==View.VISIBLE)
            {
                loading.setAnimation(null);
                loading.setVisibility(View.INVISIBLE);
            }

        }
    }

    public boolean getFeed() throws IOException, JSONException, ExecutionException, InterruptedException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject loginJSON = new JSONObject();
            loginJSON.put("action", "retrieve");
            loginJSON.put("user_id", userID);
            loginJSON.put("app_id", dID);
            loginJSON.put("lat", location.getLatitude());
            loginJSON.put("log", location.getLongitude());
            URL url = new URL(url_address);
            HttpsURLConnection loginConnection = (HttpsURLConnection) url.openConnection();
            loginConnection.setDoOutput(true);
            PrintWriter loginWriter = new PrintWriter(loginConnection.getOutputStream());
            BufferedWriter bufLOGINWriter = new BufferedWriter(loginWriter);
            bufLOGINWriter.write(loginJSON.toString());
            bufLOGINWriter.flush();
            BufferedReader bufLOGINReader = new BufferedReader(new InputStreamReader(loginConnection.getInputStream()));
            String line = null;
            StringBuffer bufResponse = new StringBuffer();
            while ((line = bufLOGINReader.readLine()) != null) {
                bufResponse.append(line);
            }
            if (!bufResponse.toString().equals(""))
            {

                final JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("retrieve_res").equals("success")) {
                    JSONArray postsJSON = resJSON.getJSONArray("posts");
                    for (int i = 0; i < postsJSON.length(); i++) {
                        posts.add(i, postsJSON.getJSONObject(i));
                    }
                    Log.e("ret","succ");
                    bitmaps.clear();
                    DisplayMetrics display=new DisplayMetrics();
                    context.getWindowManager().getDefaultDisplay().getMetrics(display);
                    int width=display.widthPixels;
                    int height=100;
                    for (int i=0;i<GetFeed.posts.size();i++)
                    {
                        if (!GetFeed.posts.get(i).isNull("img"))
                        {
                            try {
                                byte[] bytes = Base64.decode(GetFeed.posts.get(i).getString("img"), Base64.DEFAULT);
                                BitmapFactory.Options opts=new BitmapFactory.Options();
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                                opts.inSampleSize=getBestSampleSize(opts,height,width);
                                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                                bitmaps.put(i,bitmap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    bufLOGINReader.close();
                    bufLOGINWriter.close();
                    loginConnection.disconnect();


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
