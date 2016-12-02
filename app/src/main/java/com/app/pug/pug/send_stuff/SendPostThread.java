package com.app.pug.pug.send_stuff;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.activities.ActivitySplashScreen;
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.local_feed.IndividualItemsAdapter;
import com.app.pug.pug.local_feed.Yammers;
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

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * Created by zeryan on 2/13/16.
 */
public class SendPostThread extends AsyncTask<Void, Void, Boolean> {
    EditText text;
    private AppCompatActivity context;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    private String url_address = ActivityMainDataSet.url_address;
    private boolean is_public;
    private ProgressDialog loading;
    boolean img;
    String data;
    JSONObject newPost;

    public SendPostThread(EditText text, AppCompatActivity context, boolean b, IndividualItemsAdapter adapter, ProgressDialog loading, boolean img, String data) {
        this.context = context;
        this.text = text;
        this.is_public = b;
        this.loading=loading;
        this.img=img;
        this.data=data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (sendText()) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendText() throws JSONException, IOException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject loginJSON = new JSONObject();
            loginJSON.put("action", "post");
            loginJSON.put("user_id", userID);
            loginJSON.put("app_id", dID);
            loginJSON.put("lat", location.getLatitude());
            loginJSON.put("log", location.getLongitude());
            loginJSON.put("text", text.getText());
            loginJSON.put("is_public", is_public);
            if (data!=null&&img)
            {
                loginJSON.put("is_image",img);
                loginJSON.put("img",data);
            }
            else
            {
                loginJSON.put("is_image",img);
            }
            DateTime date=new DateTime();
            StringBuffer dateJ=new StringBuffer();
            dateJ.append(date.getYear()+"-"+date.getMonthOfYear()+"-"+date.getDayOfMonth());
            loginJSON.put("date", dateJ.toString());
            StringBuffer time=new StringBuffer();
            time.append(date.getHourOfDay()+":"+date.getMinuteOfHour()+":"+date.getSecondOfMinute());
            loginJSON.put("time", time.toString());
            Log.e("post",loginJSON.toString());
            URL url = new URL(url_address);
            HttpsURLConnection loginConnection = (HttpsURLConnection) url.openConnection();
            loginConnection.setRequestProperty("Accept-Charset", "UTF-8");
            loginConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
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
                if (resJSON.get("post_res").equals("success"))
                {
                    newPost = resJSON.getJSONObject("post");

                    bufLOGINReader.close();
                    bufLOGINWriter.close();
                    loginConnection.disconnect();

                    return true;
                }

            }
            bufLOGINReader.close();
            bufLOGINWriter.close();
            loginConnection.disconnect();

        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            text.setText("");
            if (newPost!=null) {
                int pos = 0;
                if (GetFeed.posts.isEmpty())
                {
                    pos=1;
                }
                GetFeed.posts.add(0, newPost);
                Yammers.integerHashMap.add(0, 0);
                if (!GetFeed.posts.isEmpty())
                {
                    if (!GetFeed.posts.get(0).isNull("img")) {
                        DisplayMetrics display = new DisplayMetrics();
                        context.getWindowManager().getDefaultDisplay().getMetrics(display);
                        int width = display.widthPixels;
                        int height = 100;
                        GetFeed.bitmaps.clear();
                        for (int i = 0; i < GetFeed.posts.size(); i++) {
                            if (!GetFeed.posts.get(i).isNull("img")) {
                                try {
                                    byte[] bytes = Base64.decode(GetFeed.posts.get(i).getString("img"), Base64.DEFAULT);
                                    BitmapFactory.Options opts = new BitmapFactory.Options();
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                                    opts.inSampleSize = getBestSampleSize(opts, height, width);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                                    GetFeed.bitmaps.put(i, bitmap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
                loading.dismiss();
                Yammers.recyclerView.setItemAnimator(new SlideInDownAnimator(new OvershootInterpolator(1f)));
                Yammers.recyclerView.getItemAnimator().setAddDuration(1000);
                Yammers.adapter.notifyItemInserted(pos);
                if (Yammers.layoutManager.findFirstCompletelyVisibleItemPosition() == pos) {
                    Yammers.layoutManager.scrollToPosition(pos);
                } else {
                    Yammers.layoutManager.smoothScrollToPosition(Yammers.recyclerView, null, pos);
                }
                Yammers.recyclerView.clearAnimation();
                Yammers.recyclerView.setAnimation(null);
                Log.e("ani","Is called");
            }

        }
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
                sampleSize *=2;
            }
        }
        return sampleSize;
    }
}
