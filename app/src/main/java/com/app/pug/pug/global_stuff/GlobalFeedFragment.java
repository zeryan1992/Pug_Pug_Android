package com.app.pug.pug.global_stuff;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pug.pug.R;
import com.app.pug.pug.local_feed.GetFeed;

import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalFeedFragment extends Fragment {
    public static ArrayList<Integer> integerHashMap;
    private AppCompatActivity activity;
    public static RecyclerView recyclerView;
    private FragmentManager manager;
    public static SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    public static GlobalAdapetr adapter;
    private ImageView loading;
    private Snackbar snackbar;
    private ConnectivityManager connectivityManager;

    public GlobalFeedFragment() {
    }

    public static GlobalFeedFragment newInstance() {

        Bundle args = new Bundle();

        GlobalFeedFragment fragment = new GlobalFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static boolean fillSparse() {
        integerHashMap = new ArrayList<>();
        if (GetglobalFeed.postsGlobal != null) {
            for (int i = 0; i < GetglobalFeed.postsGlobal.size(); i++) {
                try {
                    if (!GetglobalFeed.postsGlobal.get(i).isNull("like_res") && GetglobalFeed.postsGlobal.get(i).getInt("like_res") == 1) {
                        integerHashMap.add(i, 1);
                    } else if (!GetglobalFeed.postsGlobal.get(i).isNull("like_res") && GetglobalFeed.postsGlobal.get(i).getInt("like_res") == -1) {
                        integerHashMap.add(i, -1);
                    } else {
                        integerHashMap.add(i, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return true;

        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        manager = getChildFragmentManager();
        onRefreshListener = new UpdatePostsListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.global_feed_frag, container, false);
        snackbar= Snackbar.make(view, "Uh Ohh! There is no internet connection!", Snackbar.LENGTH_LONG);
        TextView snackText= (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.YELLOW);
        View snck=snackbar.getView();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams) snck.getLayoutParams();
        params.gravity= Gravity.TOP;
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.glob_refresher);
        refreshLayout.setDistanceToTriggerSync(10);
        refreshLayout.setColorSchemeColors(R.color.mainTextBrightBlue);
        refreshLayout.setOnRefreshListener(onRefreshListener);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.black, R.color.mainTextBrightBlue);
        recyclerView = (RecyclerView) view.findViewById(R.id.glob_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext());
        loading= (ImageView) view.findViewById(R.id.glob_progr);
        recyclerView.setLayoutManager(layoutManager);
        try {
            adapter = new GlobalAdapetr(activity);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isConnectedtotheInternet()) {
            if (GetglobalFeed.postsGlobal.isEmpty() && GetglobalFeed.bitmaps.isEmpty()) {

                GetglobalFeed get = new GetglobalFeed(activity, loading, false);
                get.execute();
            } else {
                recyclerView.setAdapter(adapter);
            }
        }
        else
        {
            recyclerView.setAdapter(adapter);
            snackbar.show();
        }

    }

    public class UpdatePostsListener implements SwipeRefreshLayout.OnRefreshListener {


        @Override
        public void onRefresh() {
            if (isConnectedtotheInternet()) {
                integerHashMap.clear();
                GlobalAdapetr.detect.clear();
                GetglobalFeed.postsGlobal.clear();
                GetglobalFeed.bitmaps.clear();
                refreshLayout.setRefreshing(true);
                GetglobalFeed bo = new GetglobalFeed(activity, loading, true);
                bo.execute();
            }
            else
            {
                refreshLayout.setRefreshing(false);
                snackbar.show();
                if (recyclerView.getAdapter()==null) {
                    recyclerView.setAdapter(adapter);
                }
            }


        }
    }
    public boolean isConnectedtotheInternet() {
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
}
