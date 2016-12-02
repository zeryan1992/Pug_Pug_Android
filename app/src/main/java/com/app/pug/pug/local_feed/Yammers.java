package com.app.pug.pug.local_feed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pug.pug.R;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.send_stuff.SendPostThread;
import com.app.pug.pug.utils.FloatingBehavior;
import com.app.pug.pug.utils.LegaStuff;

import org.json.JSONException;

import java.util.ArrayList;


public class Yammers extends Fragment {
    public static ArrayList<Integer> integerHashMap=new ArrayList<>();
    private AppCompatActivity activity;
    public static RecyclerView recyclerView;

    public static SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    public static IndividualItemsAdapter adapter;
    private AlertDialog dialogMe;
    private ProgressDialog loading;
    private GetFeed local;
    private ImageView loadingProgress;
    public static LinearLayoutManager layoutManager;
    private Snackbar snackbar;
    private ConnectivityManager connectivityManager;
    View view;


    public Yammers() {
    }

    public static Yammers newInstance() {
        Yammers fragment = new Yammers();
        return fragment;
    }

    public static boolean fillSparse() {
        if (GetFeed.posts != null) {
            for (int i = 0; i < GetFeed.posts.size(); i++) {
                try {
                    if (!GetFeed.posts.get(i).isNull("like_res") && GetFeed.posts.get(i).getInt("like_res") == 1) {
                        integerHashMap.add(i, 1);
                    } else if (!GetFeed.posts.get(i).isNull("like_res") && GetFeed.posts.get(i).getInt("like_res") == -1) {
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
        onRefreshListener = new UpdatePostsListener();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.yammer_fragment_feed, container, false);
        snackbar= Snackbar.make(view, "Uh Ohh! There is no internet connection!", Snackbar.LENGTH_LONG);
        TextView snackText= (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.YELLOW);
        View snck=snackbar.getView();
        FrameLayout.LayoutParams paramss=(FrameLayout.LayoutParams) snck.getLayoutParams();
        paramss.gravity= Gravity.TOP;
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        refreshLayout.setDistanceToTriggerSync(10);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.black, R.color.mainTextBrightBlue);
        refreshLayout.setOnRefreshListener(onRefreshListener);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        dialogMe = new AlertDialog.Builder(getContext()).setTitle("What is on your mind?").setView(R.layout.send_post_fragment_layout).create();
        loading= new ProgressDialog(getActivity());
        loadingProgress= (ImageView) view.findViewById(R.id.yam_progr);
        try {
            adapter = new IndividualItemsAdapter(activity,this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final FloatingActionButton theFloat = (FloatingActionButton) view.findViewById(R.id.floatingButton);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) theFloat.getLayoutParams();
        params.setAnchorId(R.id.recycler);
        params.setBehavior(new FloatingBehavior());
        params.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
        loading.setMessage("wait please");
        theFloat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final LegaStuff is_=new LegaStuff(getContext());
                if (is_.getAgreement()==0)
                {
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
                    
                    alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            is_.setAgreement(1);
                            dialog.dismiss();
                            dialogMe.getWindow().getAttributes().windowAnimations = R.style.send_post_anim;
                            dialogMe.show();
                            Button send = (Button) dialogMe.findViewById(R.id.send);
                            Button cancel = (Button) dialogMe.findViewById(R.id.cancel);
                            final EditText sendText = (EditText) dialogMe.findViewById(R.id.sendText);
                            final CheckBox is_public = (CheckBox) dialogMe.findViewById(R.id.is_public);
                            final TextView counter = (TextView) dialogMe.findViewById(R.id.counter);
                            if (sendText != null) {
                                sendText.addTextChangedListener(new TextWatcher()
                                {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        counter.setVisibility(View.VISIBLE);
                                        if (s.length() <= 200) {
                                            counter.setTextColor(getResources().getColor(R.color.overall));
                                            counter.setText("" + s.length() + "/200");
                                        } else {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                counter.setTextColor(getResources().getColor(R.color.red, null));
                                            }

                                            counter.setText("-" + s.length() + "/200");
                                        }

                                    }
                                });
                            }
                            dialogMe.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                    sendText.setText("");
                                    if (is_public.isChecked()) {
                                        is_public.setChecked(false);
                                    }
                                    counter.setVisibility(View.GONE);
                                }
                            });
                            if (send != null) {
                                send.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        if (isConnectedtotheInternet()) {

                                            if (!sendText.getText().equals("") && sendText.getText().length() > 2 && sendText.getText().length() <= 200) {
                                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(sendText.getWindowToken(), 0);
                                                dialogMe.dismiss();
                                                SendPostThread sendPostThread = null;
                                                if (is_public.isChecked()) {
                                                    sendPostThread = new SendPostThread(sendText, (AppCompatActivity) getActivity(), true, adapter, loading, false, null);
                                                } else {
                                                    sendPostThread = new SendPostThread(sendText, (AppCompatActivity) getActivity(), false, adapter, loading, false, null);
                                                }
                                                sendPostThread.execute();
                                                dialogMe.cancel();
                                            } else {
                                                Snackbar.make(view, "Type something please!", Snackbar.LENGTH_LONG).show();
                                            }
                                            if (sendText.getText().length() > 200) {
                                                Snackbar.make(view, "You must not exceed 200 letters ", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                        else
                                        {
                                            snackbar.show();
                                        }

                                    }
                                });
                            }
                            assert cancel != null;
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    dialogMe.cancel();
                                }
                            });
                        }
                    });
                    alertDialog.setView(R.layout.legal);
                    AlertDialog dig=alertDialog.create();
                    dig.show();
                }
                else
                {
                    dialogMe.getWindow().getAttributes().windowAnimations=R.style.send_post_anim;
                    dialogMe.show();
                    Button send = (Button) dialogMe.findViewById(R.id.send);
                    Button cancel = (Button) dialogMe.findViewById(R.id.cancel);
                    final EditText sendText = (EditText) dialogMe.findViewById(R.id.sendText);
                    final CheckBox is_public = (CheckBox) dialogMe.findViewById(R.id.is_public);
                    final TextView counter = (TextView) dialogMe.findViewById(R.id.counter);
                    if (sendText != null) {
                        sendText.addTextChangedListener(new TextWatcher()
                        {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {


                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                counter.setVisibility(View.VISIBLE);
                                if (s.length() <= 200) {
                                    counter.setTextColor(getResources().getColor(R.color.overall));
                                    counter.setText("" + s.length() + "/200");
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        counter.setTextColor(getResources().getColor(R.color.red, null));
                                    }

                                    counter.setText("-" + s.length() + "/200");
                                }

                            }
                        });
                    }
                    dialogMe.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            sendText.setText("");
                            if (is_public.isChecked()) {
                                is_public.setChecked(false);
                            }
                            counter.setVisibility(View.GONE);
                        }
                    });
                    if (send != null) {
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if (isConnectedtotheInternet()) {
                                    if (!sendText.getText().equals("") && sendText.getText().length() > 2 && sendText.getText().length() <= 200) {
                                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(sendText.getWindowToken(), 0);
                                        loading.show();
                                        dialogMe.dismiss();

                                        ActivityMainDataSet.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                SendPostThread sendPostThread = null;
                                                if (is_public.isChecked()) {

                                                    sendPostThread = new SendPostThread(sendText, (AppCompatActivity) getActivity(), true, adapter, loading, false, null);

                                                } else {
                                                    sendPostThread = new SendPostThread(sendText, (AppCompatActivity) getActivity(), false, adapter, loading, false, null);
                                                }
                                                sendPostThread.execute();
                                            }
                                        });

                                    } else {
                                        Snackbar.make(view, "Type something please!", Snackbar.LENGTH_LONG).show();
                                    }
                                    if (sendText.getText().length() > 200) {
                                        Snackbar.make(view, "You must not exceed 200 letters ", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    snackbar.show();
                                }

                            }
                        });
                    }
                    if (cancel != null)
                    {
                        cancel.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                dialogMe.cancel();
                            }
                        });
                    }
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (dialogMe!=null)
        {
            if (dialogMe.isShowing())
            {
                dialogMe.cancel();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isConnectedtotheInternet()) {
            if (GetFeed.posts.isEmpty() && GetFeed.bitmaps.isEmpty()) {
                local = new GetFeed((AppCompatActivity) getActivity(), loadingProgress, false);
                local.execute();
            } else {
                recyclerView.setAdapter(adapter);
            }
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



    private class UpdatePostsListener implements SwipeRefreshLayout.OnRefreshListener {


        @Override
        public void onRefresh() {
            if (isConnectedtotheInternet()) {
                ActivityMainDataSet.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        GetFeed.posts.clear();
                        integerHashMap.clear();
                        recyclerView.clearAnimation();
                        recyclerView.setItemAnimator(null);
                        IndividualItemsAdapter.detect.clear();
                        GetFeed bo = new GetFeed(activity, loadingProgress, true);
                        bo.execute();
                    }
                });
            }
            else
            {
                refreshLayout.setRefreshing(false);
                snackbar.show();
                if (recyclerView.getAdapter()==null)
                {
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

