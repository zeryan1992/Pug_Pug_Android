package com.app.pug.pug.public_map_view;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.pug.pug.R;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.global_stuff.GetglobalFeedForEarth;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicMaps extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    SupportMapFragment googleMap;
    View view;
    public static GoogleMap theMap;
    Snackbar snackbar;
    private ConnectivityManager connectivityManager;
    private Snackbar snacInt;


    public PublicMaps() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainDataSet.handler.post(new Runnable() {
            @Override
            public void run() {
                googleMap = SupportMapFragment.newInstance();
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction().replace(R.id.map_id, googleMap, "map");
                googleMap.getMapAsync(PublicMaps.this);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_public_maps,container,false);
        snackbar=Snackbar.make(view, "No body's post got 5 votes and up. Try refreshing!", Snackbar.LENGTH_LONG);
        TextView snackText= (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.YELLOW);
        View snck=snackbar.getView();
        FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) snck.getLayoutParams();
        params.gravity= Gravity.TOP;
        snacInt=Snackbar.make(view, "No body's post got 5 votes and up. Try refreshing!", Snackbar.LENGTH_LONG);
        TextView textInt= (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textInt.setTextColor(Color.YELLOW);
        View sncTex=snackbar.getView();
        FrameLayout.LayoutParams paramInt= (FrameLayout.LayoutParams) sncTex.getLayoutParams();
        paramInt.gravity= Gravity.TOP;
        final FloatingActionButton fab= (FloatingActionButton) view.findViewById(R.id.recharge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.startAnimation(new RotateAnimation(0, 180, fab.getPivotX(), fab.getPivotY()));
                fab.getAnimation().setDuration(2000);
                if (isConnectedtotheInternet()) {
                    GetglobalFeedForEarth.postsGlobal.clear();
                    GetglobalFeedForEarth getglobalFeedForEarth = new GetglobalFeedForEarth(PublicMaps.this, getActivity(), true, snackbar);
                    getglobalFeedForEarth.execute();
                }
                else
                {
                    snacInt.show();
                }
            }
        });

        return view;

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("marker", marker.getId());
        for (Map.Entry<String,Integer> entry:GetglobalFeedForEarth.markers.entrySet())
        {
            if (entry.getKey().equals(marker.getId()))
            {
                break;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isConnectedtotheInternet()) {
            if (GetglobalFeedForEarth.postsGlobal.isEmpty()) {
                GetglobalFeedForEarth get = new GetglobalFeedForEarth(PublicMaps.this, getActivity(), false, snackbar);
                get.execute();
            }
        }
        else
        {
            snacInt.show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.theMap=googleMap;


    }
    public boolean isConnectedtotheInternet() {
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }


}
