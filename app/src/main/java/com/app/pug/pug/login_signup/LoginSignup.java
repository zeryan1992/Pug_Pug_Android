package com.app.pug.pug.login_signup;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.activities.ActivitySplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by macbook on 1/23/16.
 */
public class LoginSignup extends AsyncTask<Void, Void, Boolean> {
    static String user_id;
    private static File fileID;
    private AppCompatActivity context;
    private Location location = ActivitySplashScreen.locationHandler.getLastLocation();
    private String url_address = ActivityMainDataSet.url_address;


    public LoginSignup(AppCompatActivity context) {
        this.context = context;
        fileID = new File(context.getFilesDir().getPath() + "/id.txt");
    }

    public static boolean checkFile() {
        if (fileID.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static String readFile() throws IOException {
        FileReader fReader = new FileReader(fileID);
        BufferedReader bufReader = new BufferedReader(fReader);
        String line = null;
        StringBuffer stBuffer = new StringBuffer();
        while ((line = bufReader.readLine()) != null) {
            stBuffer.append(line);
        }
        bufReader.close();
        fReader.close();
        return stBuffer.toString();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (!checkFile()) {
                if (checkId()) {
                    if (writeFile()) {
                        if (signUP()) {
                            if (logIn()) {
                                return true;
                            }
                        }
                    }
                }
            }
            else
            {
                if (logIn())
                {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            ActivityMainDataSet.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, ActivityMainDataSet.class);
                    context.startActivity(intent);
                    context.finish();
                }
            }, 1000);
        }


    }

    public boolean checkId() throws JSONException, IOException, ClassNotFoundException {
        boolean checker = false;
        URL url = new URL(url_address);
        JSONObject userIDobject = new JSONObject();
        userIDobject.put("action", "check");
        HttpsURLConnection checkIDconnection = (HttpsURLConnection) url.openConnection();
        checkIDconnection.setDoOutput(true);
        checkIDconnection.setRequestMethod("POST");
        BufferedWriter sendID = new BufferedWriter(new PrintWriter(checkIDconnection.getOutputStream()));
        sendID.write(userIDobject.toString());
        sendID.flush();
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(checkIDconnection.getInputStream()));
        String lineReader = null;
        StringBuilder responseCache = new StringBuilder();
        while ((lineReader = responseReader.readLine()) != null) {
            responseCache.append(lineReader);
        }
        if (!responseCache.toString().equals("")) {
            JSONObject cacheJSON = new JSONObject(responseCache.toString());
            if (cacheJSON.get("check_response").equals("success")) {
                user_id = (String) cacheJSON.get("_id");
                responseReader.close();
                sendID.close();
                checkIDconnection.disconnect();
                checker = true;

            }


        }
        responseReader.close();
        sendID.close();
        checkIDconnection.disconnect();
        return checker;
    }

    public boolean writeFile() throws IOException {
        PrintWriter writer = new PrintWriter(fileID);
        BufferedWriter bufWriter = new BufferedWriter(writer);
        bufWriter.write(user_id);
        bufWriter.close();
        if (fileID.exists()) {
            writer.close();
            return true;
        }
        else {
            writer.close();
            return false;
        }
    }

    public boolean signUP() throws JSONException, IOException {
        String readID = readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        JSONObject signUPData = new JSONObject();
        signUPData.put("action", "sign_up");
        signUPData.put("sign_id", readID);
        signUPData.put("app_id", dID);
        URL url = new URL(url_address);
        HttpsURLConnection signUPConnection = (HttpsURLConnection) url.openConnection();
        signUPConnection.setDoOutput(true);
        signUPConnection.setRequestMethod("POST");
        PrintWriter writeSign = new PrintWriter(signUPConnection.getOutputStream());
        BufferedWriter bufWriter = new BufferedWriter(writeSign);
        bufWriter.write(signUPData.toString());
        bufWriter.flush();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(signUPConnection.getInputStream()));
        String line = null;
        StringBuffer stBuffer = new StringBuffer();
        while ((line = bufReader.readLine()) != null) {
            stBuffer.append(line);
        }
        if (!stBuffer.toString().equals("")) {
            JSONObject responseJSON = new JSONObject(stBuffer.toString());
            if (responseJSON.get("sign_res").equals("success")) {
                writeFile();
                bufReader.close();
                bufWriter.close();
                signUPConnection.disconnect();
                return true;
            }
        }
        bufReader.close();
        bufWriter.close();
        signUPConnection.disconnect();
        return false;
    }

    public boolean logIn() throws IOException, JSONException {
        String userID = readFile();
        String dID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userID != null || !userID.equals("")) {
            JSONObject loginJSON = new JSONObject();
            loginJSON.put("action", "login");
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
                JSONObject resJSON = new JSONObject(bufResponse.toString());
                if (resJSON.get("login_res").equals("success")) {
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




}
