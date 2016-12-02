package com.app.pug.pug.pro_replies;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.app.pug.pug.R;
import com.app.pug.pug.pro_posts.ProfileGetPosts;

import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRepliesFrag extends Fragment {


    public static ArrayList<Integer> integerHashMap=new ArrayList<>();
    private AppCompatActivity activity;
    public static RecyclerView recyclerView;
    public static SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    public static MyRepliesAdapter adapter;
    ImageView loading;
    private Toast snackbar;
    private ConnectivityManager connectivityManager;

    public MyRepliesFrag() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        onRefreshListener = new UpdatePostsListener();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts_replies, container, false);
        snackbar= Toast.makeText(getContext(), "Uh Ohh! There is no internet connection!", Toast.LENGTH_LONG);
        recyclerView = (RecyclerView) view.findViewById(R.id.indi_my_stuff_recycler);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.indi_my_stuff_refresh);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.black, R.color.mainTextBrightBlue);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        loading= (ImageView) view.findViewById(R.id.my_replies_progre);
        try {
            adapter = new MyRepliesAdapter((AppCompatActivity) getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refreshLayout.setOnRefreshListener(onRefreshListener);
        ImageView back= (ImageView) view.findViewById(R.id.backbut);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        if (ProfileGetPosts.from!=null&&ProfileGetPosts.from.equals("my_posts"))
        {
            recyclerView.setAdapter(null);
        }
        if (recyclerView.getAdapter()==null) {
            if (isConnectedtotheInternet()) {
                ProfileGetPosts profileGetPosts = new ProfileGetPosts(activity, loading, null, "my_replies", false);
                profileGetPosts.execute();
            }
            else
            {
                recyclerView.setAdapter(adapter);
                snackbar.show();
            }
        }
    }

    public class UpdatePostsListener implements SwipeRefreshLayout.OnRefreshListener
    {
        @Override
        public void onRefresh()
        {
            if (isConnectedtotheInternet()) {
                ProfileGetPosts bo = new ProfileGetPosts(activity, loading, null, "my_replies", true);
                bo.execute();
            }
            else
            {
                if (recyclerView.getAdapter()==null)
                {
                    recyclerView.setAdapter(adapter);
                }
                snackbar.show();
            }



        }
    }
    public static boolean fillSparse() {
        if (ProfileGetPosts.posts != null) {
            for (int i = 0; i < ProfileGetPosts.posts.size(); i++) {
                try {
                    if (!ProfileGetPosts.posts.get(i).isNull("like_res") && ProfileGetPosts.posts.get(i).getInt("like_res") == 1) {
                        integerHashMap.add(i, 1);
                    } else if (!ProfileGetPosts.posts.get(i).isNull("like_res") && ProfileGetPosts.posts.get(i).getInt("like_res") == -1) {
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
    public void onPause() {
        super.onPause();
    }
    public boolean isConnectedtotheInternet() {
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
}
