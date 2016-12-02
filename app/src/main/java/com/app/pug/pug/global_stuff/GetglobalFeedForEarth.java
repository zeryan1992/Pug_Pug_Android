package com.app.pug.pug.global_stuff;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.activities.ActivitySplashScreen;
import com.app.pug.pug.login_signup.LoginSignup;
import com.app.pug.pug.public_map_view.PublicMaps;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
 * Created by zeryan on 3/2/16.
 */
public class GetglobalFeedForEarth extends AsyncTask<Void, Void, Boolean> {
    public static ArrayList<JSONObject> postsGlobal = new ArrayList<>();
    private final PublicMaps publicMaps;
    private AppCompatActivity context;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    private String url_address = ActivityMainDataSet.url_address;
    public static HashMap<String,Integer> markers=new HashMap<>();
    boolean is_refresh;
    private Snackbar view;

    public GetglobalFeedForEarth(PublicMaps publicMaps, Context context, boolean is_refresh, Snackbar view) {
        this.publicMaps = publicMaps;
        this.context= (AppCompatActivity) context;
        this.is_refresh=is_refresh;
        this.view=view;
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
        }
        return false;

    }


    public boolean getFeed() throws IOException, JSONException {
        String userID = LoginSignup.readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject loginJSON = new JSONObject();
            loginJSON.put("action", "global_retrieve");
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

            if (!bufResponse.toString().equals("")) {
                final JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("retrieve_glob").equals("success")) {
                    JSONArray postsJSON = resJSON.getJSONArray("posts");
                    postsGlobal.clear();
                    for (int i = 0; i < postsJSON.length(); i++) {
                        postsGlobal.add(i, postsJSON.getJSONObject(i));
                    }
                    bufLOGINReader.close();
                    bufLOGINWriter.close();
                    loginConnection.disconnect();
                    return true;


                } else if (resJSON.get("retrieve_glob").equals("fail")) {
                }
            }

        }


        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean)
        {
            if (!GetglobalFeedForEarth.postsGlobal.isEmpty())
            {
                PublicMaps.theMap.clear();
                for (int i = 0; i < GetglobalFeedForEarth.postsGlobal.size(); i++)
                {
                    try {
                        Marker marker = PublicMaps.theMap.addMarker(new MarkerOptions().title(postsGlobal.get(i).getString("text")).position(new LatLng(postsGlobal.get(i).getDouble("lat"), postsGlobal.get(i).getDouble("log"))));
                        markers.put(marker.getId(), i);
                        PublicMaps.theMap.setOnMarkerClickListener(publicMaps);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                view.show();
            }



        }
    }
}
