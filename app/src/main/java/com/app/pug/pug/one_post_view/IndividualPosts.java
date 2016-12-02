package com.app.pug.pug.one_post_view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.pug.pug.R;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.get_indi_replies.GetReplies;
import com.app.pug.pug.send_stuff.SendAreply;
import com.app.pug.pug.send_stuff.SendReport;

import org.json.JSONException;

import java.util.HashMap;


public class IndividualPosts extends Fragment {
    public static HashMap<Integer, Integer> integerHashMap = new HashMap<>();
    private static int currentPostion;
    private static String typeHeader;
    private static String from;
    public static RecyclerView recyclerView;
    public static RepliesAdapter adapter;
    public static SwipeRefreshLayout refreshLayout;
    private ImageView trash;
    private static boolean delete;
    ImageView back;
    private ProgressDialog sendLoading;
    Runnable runnable;
    ImageView flag;
    static boolean can_replyM;
    ImageView loading;
    ProgressDialog proDilog;
    private Toast snackbar;
    private ConnectivityManager connectivityManager;


    public IndividualPosts() {
    }

    public static IndividualPosts newInstance(int current, String type, String fromF, boolean deleteIt, boolean can_reply) {
        IndividualPosts fragment = new IndividualPosts();
        currentPostion = current;
        typeHeader = type;
        from = fromF;
        delete=deleteIt;
        can_replyM=can_reply;
        return fragment;
    }

    public static boolean fillReplySparse() {
        if (GetReplies.replies != null) {
            for (int i = 0; i < GetReplies.replies.size(); i++) {
                try {
                    if (!GetReplies.replies.get(i).isNull("like_res") && GetReplies.replies.get(i).getInt("like_res") == 1) {
                        integerHashMap.put(i, 1);
                    } else if (!GetReplies.replies.get(i).isNull("like_res") && GetReplies.replies.get(i).getInt("like_res") == -1) {
                        integerHashMap.put(i, -1);
                    } else {
                        integerHashMap.put(i, 0);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.individual_posts_fragment, container, false);
        snackbar= Toast.makeText(getContext(), "Uh Ohh! There is no internet connection!", Toast.LENGTH_LONG);
        final EditText replyText = (EditText) view.findViewById(R.id.indi_reply_text);
        TextView sendButt = (TextView) view.findViewById(R.id.indi_reply_send);
        final InputMethodManager manager= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        trash= (ImageView) view.findViewById(R.id.delete);
        flag= (ImageView) view.findViewById(R.id.flag);
        proDilog=new ProgressDialog(getContext());
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.sendBox);
        if (can_replyM)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            linearLayout.setVisibility(View.GONE);

        }
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
        if (delete)
        {
            trash.setVisibility(View.VISIBLE);
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.setTitle("Delete:-");
                    alertDialog.setMessage("Are you sure you want to delete this post?");
                    alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (isConnectedtotheInternet()) {
                                SendReport sendReport = new SendReport((AppCompatActivity) getActivity(), currentPostion, from, null, null, "delete", proDilog, "null");
                                sendReport.execute();
                                if (from.equals("local")) {
                                    dialog.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } else if (from.equals("global")) {
                                    dialog.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else if (from.equals("my_replies")) {
                                    dialog.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else if (from.equals("my_posts")) {
                                    dialog.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            }
                            else
                            {
                                snackbar.show();
                            }

                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
        }
        else
        {
            alertDialog.setView(R.layout.report);
            flag.setVisibility(View.VISIBLE);
            final AlertDialog flagAlert=alertDialog.create();
            flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flagAlert.setTitle("Report a post:-");
                    flagAlert.show();
                    final CardView threat= (CardView) flagAlert.findViewById(R.id.threat);
                    assert threat != null;
                    threat.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            if (isConnectedtotheInternet()) {
                                SendReport sendReport = new SendReport((AppCompatActivity) getActivity(), currentPostion, from, "post", "threat", "report", proDilog, "null");
                                sendReport.execute();
                                if (from.equals("local")) {
                                    Log.e("report", "Success");
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } else if (from.equals("global")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();


                                } else if (from.equals("my_replies")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();


                                }
                            }
                            else
                            {
                                snackbar.show();
                            }
                        }
                    });
                    final CardView target= (CardView) flagAlert.findViewById(R.id.target);
                    assert target != null;
                    target.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (isConnectedtotheInternet()) {
                                SendReport sendReport = new SendReport((AppCompatActivity) getActivity(), currentPostion, from, "post", "target", "report", proDilog, "null");
                                sendReport.execute();
                                if (from.equals("local")) {
                                    Log.e("report", "Success");
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();


                                } else if (from.equals("global")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } else if (from.equals("my_replies")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                }
                            }
                            else
                            {
                                snackbar.show();
                            }
                        }
                    });
                    final CardView spam= (CardView) flagAlert.findViewById(R.id.spam);
                    assert spam != null;
                    spam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if (isConnectedtotheInternet()) {
                                SendReport sendReport = new SendReport((AppCompatActivity) getActivity(), currentPostion, from, "post", "spam", "report", proDilog, "null");
                                sendReport.execute();
                                if (from.equals("local")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();


                                } else if (from.equals("global")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } else if (from.equals("my_replies")) {
                                    flagAlert.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            }
                            else
                            {
                                snackbar.show();
                            }
                        }
                    });

                }
            });


        }
        sendLoading= new ProgressDialog(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.indi_replies);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RepliesAdapter((AppCompatActivity) getActivity(), currentPostion, typeHeader);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.indi_refresh);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.black, R.color.mainTextBrightBlue);
        refreshLayout.setDistanceToTriggerSync(2);
        RefreshListener refreshListener = new RefreshListener();
        refreshLayout.setOnRefreshListener(refreshListener);
        loading= (ImageView) view.findViewById(R.id.loading);
        FrameLayout frameLayout= (FrameLayout) view.findViewById(R.id.framePosts);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(frameLayout.getLayoutParams());
        if (linearLayout.getVisibility()==View.GONE)
        {
            params.height= FrameLayout.LayoutParams.MATCH_PARENT;

            frameLayout.setLayoutParams(params);
        }
        back = (ImageView) view.findViewById(R.id.backbut);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityMainDataSet.handler.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getSupportFragmentManager().popBackStack();
                        manager.hideSoftInputFromWindow(replyText.getWindowToken(), 0);
                    }
                });


            }
        });
        sendButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectedtotheInternet()) {
                    if (!replyText.getText().equals("")) {
                        final SendAreply sendAreply = new SendAreply(replyText, (AppCompatActivity) getActivity(), currentPostion, from, sendLoading);
                        ActivityMainDataSet.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendAreply.execute();

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getContext(),"You have to write something",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    snackbar.show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        runnable = new Runnable() {
            @Override
            public void run() {
                GetReplies getReplies=new GetReplies((AppCompatActivity) getActivity(), currentPostion, from, loading, false);
                getReplies.execute();
            }
        };

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onPause() {
        super.onPause();
        GetReplies.replies.clear();
        integerHashMap.clear();
        ActivityMainDataSet.handler.removeCallbacks(runnable);


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isConnectedtotheInternet()) {
            ActivityMainDataSet.handler.post(runnable);
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





    public class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (isConnectedtotheInternet()) {
                ActivityMainDataSet.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        GetReplies.replies.clear();
                        RepliesAdapter.detect.clear();
                        integerHashMap.clear();
                        GetReplies re = new GetReplies((AppCompatActivity) getActivity(), currentPostion, from, loading, true);
                        re.execute();
                    }
                });
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
    public boolean isConnectedtotheInternet() {
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
}
