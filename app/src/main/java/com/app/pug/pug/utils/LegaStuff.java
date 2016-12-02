package com.app.pug.pug.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by zeryan on 3/15/16.
 */
public class LegaStuff  {
    Context context;
    SharedPreferences sharedPrefs;
    public LegaStuff(Context context)
    {
        this.context=context;
        sharedPrefs=context.getSharedPreferences("agree",Context.MODE_PRIVATE);
    }
    public void setAgreement(int num)
    {
        SharedPreferences.Editor editor=sharedPrefs.edit();
        editor.putInt("ag",num);
        editor.commit();
    }
    public void setimageUri(String uri)
    {
        Log.e("set is called","yay");
        SharedPreferences.Editor editor=sharedPrefs.edit();
        editor.putString("uri", uri);
        editor.commit();
    }
    public String getUri()
    {
        return sharedPrefs.getString("uri",null);
    }
    public int  getAgreement()
    {
        return sharedPrefs.getInt("ag",0);
    }
}
