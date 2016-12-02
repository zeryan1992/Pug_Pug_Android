package com.app.pug.pug.one_post_view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pug.pug.R;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.get_indi_replies.GetReplies;
import com.app.pug.pug.global_stuff.GetglobalFeed;
import com.app.pug.pug.global_stuff.GlobalAdapetr;
import com.app.pug.pug.global_stuff.GlobalFeedFragment;
import com.app.pug.pug.like_posts.LikeAction;
import com.app.pug.pug.like_reply.LikeReplyAction;
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.local_feed.IndividualItemsAdapter;
import com.app.pug.pug.local_feed.Yammers;
import com.app.pug.pug.pro_posts.MyPostsAdapter;
import com.app.pug.pug.pro_posts.MyPostsFrag;
import com.app.pug.pug.pro_posts.ProfileGetPosts;
import com.app.pug.pug.pro_replies.MyRepliesAdapter;
import com.app.pug.pug.pro_replies.MyRepliesFrag;
import com.app.pug.pug.send_stuff.SendReport;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONException;

import java.util.HashMap;

public class RepliesAdapter extends RecyclerView.Adapter {
    public static HashMap<Integer, Integer> detect = new HashMap<>();
    AppCompatActivity activity;
    private int TYPE_HEADER = 1;
    private int TYPE_REPLIES = 0;
    private int currentPostion;
    private String headerType;
    private int times;
    ProgressDialog proDialog;


    public RepliesAdapter(AppCompatActivity activity, int currentPostion, String headerType) {
        this.activity = activity;
        this.currentPostion = currentPostion;
        this.headerType = headerType;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_REPLIES)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_replies_view, parent, false);
            ReplyViewHolder replyViewHolder = new ReplyViewHolder(view, new ReplyViewHolder.HandleOnClicks() {
                @Override
                public void onCardPressed(CardView mainCard, final int pos) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    proDialog=new ProgressDialog(activity);
                    try {
                        if (GetReplies.replies.get(pos-1).getBoolean("self"))
                        {
                            builder.setTitle("Delete");
                            builder.setMessage("Want to delete this reply?");
                            builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    SendReport sendReport=new SendReport(activity,pos-1,null,null,null,"delete_reply", proDialog,"delete_reply");
                                    Log.e("true","reply");
                                    sendReport.execute();
                                }
                            });
                            builder.show();
                        }
                        else if (!GetReplies.replies.get(pos-1).getBoolean("self"))
                        {
                            builder.setTitle("Report");
                            builder.setMessage("Want to report this reply?");
                            builder.setPositiveButton("report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SendReport sendReport=new SendReport(activity,pos-1,null,"reply","mid","report", proDialog, "delete_reply");
                                    sendReport.execute();
                                    GetReplies.replies.remove(pos-1);
                                    notifyItemRemoved(pos-1);
                                }
                            });
                            builder.show();
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUpPressed(ImageView img, int pos)
                    {
                        if (IndividualPosts.integerHashMap.get(pos - 1) == 0) {
                            IndividualPosts.integerHashMap.put(pos - 1, 1);
                            detect.put(pos - 1, 1);
                            notifyItemChanged(pos);
                            final LikeReplyAction likeAction = new LikeReplyAction(activity, GetReplies.replies.get(pos - 1));
                            ActivityMainDataSet.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    likeAction.setLikeAction(1);
                                    likeAction.execute();
                                }
                            });
                        }
                    }

                    @Override
                    public void onDownPressed(ImageView img, int pos) {
                        if (IndividualPosts.integerHashMap.get(pos - 1) == 0) {
                            IndividualPosts.integerHashMap.put(pos - 1, -1);
                            detect.put(pos - 1, -1);
                            notifyItemChanged(pos);
                            final LikeReplyAction likeAction = new LikeReplyAction(activity, GetReplies.replies.get(pos - 1));
                            ActivityMainDataSet.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    likeAction.setLikeAction(-1);
                                    likeAction.execute();
                                }
                            });


                        }
                    }
                });
                return replyViewHolder;

        }
        else if (viewType == TYPE_HEADER)
        {
            if (headerType != null)
            {
                if (headerType.equals("my_replies"))
                {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_header, parent, false);
                    HeaderAreaMyReply headerArea = new HeaderAreaMyReply(view, new HeaderAreaMyReply.HandleOnClicks()
                    {
                        @Override
                        public void onUpPressed(ImageView img, int pos)
                        {
                            if (MyRepliesFrag.integerHashMap.get(currentPostion) == 0) {
                                MyRepliesFrag.integerHashMap.set(currentPostion, 1);
                                MyRepliesAdapter.detect.put(currentPostion, 1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, ProfileGetPosts.posts.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                });


                            }
                        }

                        @Override
                        public void onDownPressed(ImageView img, int pos) {
                            if (MyRepliesFrag.integerHashMap.get(currentPostion) == 0) {
                                MyRepliesFrag.integerHashMap.set(currentPostion, -1);
                                MyRepliesAdapter.detect.put(currentPostion, -1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, ProfileGetPosts.posts.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                });


                            }
                        }
                    });
                    return headerArea;
                }
                else if (headerType.equals("local_header"))
                {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_header, parent, false);
                    HeaderArea headerArea = new HeaderArea(view, new HeaderArea.HandleOnClicks() {

                        @Override
                        public void onUpPressed(ImageView img, int pos) {

                            if (Yammers.integerHashMap.get(currentPostion) == 0) {
                                Yammers.integerHashMap.set(currentPostion, 1);
                                IndividualItemsAdapter.detect.put(currentPostion, 1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, GetFeed.posts.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                });


                            }
                        }

                        @Override
                        public void onDownPressed(ImageView img, int pos) {
                            if (Yammers.integerHashMap.get(currentPostion) == 0) {
                                Yammers.integerHashMap.set(currentPostion, -1);
                                IndividualItemsAdapter.detect.put(currentPostion, -1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, GetFeed.posts.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                });


                            }

                        }
                    });
                    return headerArea;
                }
                else if (headerType.equals("glob_header"))
                {
                    Log.e("header","Global");
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.globe_header_post, parent, false);
                    ViewHolderHeader glob = new ViewHolderHeader(view, new ViewHolderHeader.MyViewclickHolder() {
                        @Override
                        public void onHeartPress(View imgB, int pos) {
                            Log.e("pressed","heart");
                            if (GlobalFeedFragment.integerHashMap.get(currentPostion) == 0)
                            {
                                GlobalFeedFragment.integerHashMap.set(currentPostion, 1);
                                GlobalAdapetr.detect.put(currentPostion, 1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, GetglobalFeed.postsGlobal.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onDownheartPressed(View imB, int pos) {
                            Log.e("pressed","heart");

                            if (GlobalFeedFragment.integerHashMap.get(currentPostion) == 0) {
                                GlobalFeedFragment.integerHashMap.set(currentPostion, -1);
                                GlobalAdapetr.detect.put(currentPostion, -1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, GetglobalFeed.postsGlobal.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                });


                            }

                        }
                    });
                    Log.e("Enabled",""+glob.heart.isEnabled());
                    return glob;
                }
                else if (headerType.equals("my_posts"))
                {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_header, parent, false);
                    HeaderAreaMyPosts headerArea = new HeaderAreaMyPosts(view, new HeaderAreaMyPosts.HandleOnClicks() {

                        @Override
                        public void onUpPressed(ImageView img, int pos) {

                            if (MyPostsFrag.integerHashMap.get(currentPostion) == 0) {
                                MyPostsFrag.integerHashMap.set(currentPostion, 1);
                                MyPostsAdapter.detect.put(currentPostion, 1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, ProfileGetPosts.posts.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onDownPressed(ImageView img, int pos) {
                            if (MyPostsFrag.integerHashMap.get(currentPostion) == 0) {
                                MyPostsFrag.integerHashMap.set(currentPostion, -1);
                                MyPostsAdapter.detect.put(currentPostion, -1);
                                notifyItemChanged(pos);
                                final LikeAction likeAction = new LikeAction(activity, ProfileGetPosts.posts.get(currentPostion));
                                ActivityMainDataSet.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                });


                            }

                        }
                    });
                    return headerArea;
                }
            }
        }
        throw new RuntimeException("We were unable to find a viewtype" + viewType + " make sure that you are using it correctly");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReplyViewHolder)
        {
            ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
            String[] date = new String[0];
            String[] time = new String[0];
            try {
                date = GetReplies.replies.get(position-1).getString("post_date").split("-");
                time = GetReplies.replies.get(position-1).getString("post_time").split(":");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            DateTime now = new DateTime();
            Period period = new Period(dateTime, now);
            if (period.getYears() != 0) {
                times = period.getYears();
                replyViewHolder.date.setText("" + period.getYears() + "y");
            } else if (period.getMonths() != 0 && period.getYears() == 0) {
                times = period.getMonths();
                replyViewHolder.date.setText("" + period.getMonths() + "mo");

            } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                times = period.getWeeks();
                replyViewHolder.date.setText("" + period.getWeeks() + "w");

            } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                times = period.getDays();
                replyViewHolder.date.setText("" + period.getDays() + "d");

            } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                times = period.getHours();
                replyViewHolder.date.setText("" + period.getHours() + "h");

            } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                times = period.getMinutes();
                replyViewHolder.date.setText("" + period.getMinutes() + "m");

            } else if (period.getSeconds() >= 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                times = period.getSeconds();
                replyViewHolder.date.setText("" + period.getSeconds() + "s");


            } else {
                replyViewHolder.date.setText("0s");
            }

            if (!GetReplies.replies.isEmpty())
            {
                if (!GetReplies.replies.get(position-1).isNull("icon"))
                {
                    try {
                        byte[] bytes= Base64.decode(GetReplies.replies.get(position-1).getString("icon"),Base64.DEFAULT);
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        replyViewHolder.pro_pic.setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                replyViewHolder.textView.setText(GetReplies.replies.get(position - 1).getString("text"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (IndividualPosts.integerHashMap.get(position - 1) == 1) {

                if (!detect.isEmpty()&&detect.containsKey(position-1) && detect.get(position - 1) == 1) {
                    try {
                        int oldLike = GetReplies.replies.get(position - 1).getInt("likes");
                        int newlike = oldLike + 1;
                        replyViewHolder.likes.setText("" + newlike);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        replyViewHolder.likes.setText(GetReplies.replies.get(position - 1).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    replyViewHolder.upVote.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    replyViewHolder.downVote.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    replyViewHolder.upVote.setEnabled(false);
                    replyViewHolder.downVote.setEnabled(false);

                }
                else
                {
                    replyViewHolder.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.up_vote_new));
                    replyViewHolder.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    replyViewHolder.upVote.setEnabled(false);
                    replyViewHolder.downVote.setEnabled(false);
                }
            }
            else if (IndividualPosts.integerHashMap.get(position - 1) == -1) {
                if (!detect.isEmpty() &&detect.containsKey(position-1) && detect.get(position - 1) == -1) {
                    try {
                        int oldLike = GetReplies.replies.get(position - 1).getInt("likes");
                        int newlike = oldLike - 1;
                        replyViewHolder.likes.setText("" + newlike);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        replyViewHolder.likes.setText(GetReplies.replies.get(position - 1).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    replyViewHolder.downVote.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    replyViewHolder.upVote.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    replyViewHolder.upVote.setEnabled(false);
                    replyViewHolder.downVote.setEnabled(false);
                }
                else
                {
                    replyViewHolder.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    replyViewHolder.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    replyViewHolder.upVote.setEnabled(false);
                    replyViewHolder.downVote.setEnabled(false);
                }
            }
            else if (IndividualPosts.integerHashMap.get(position - 1) == 0)
            {
                Log.e("add", "is Zero");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    replyViewHolder.downVote.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    replyViewHolder.upVote.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));

                }
                else
                {
                    replyViewHolder.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    replyViewHolder.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                }


                try {
                    replyViewHolder.likes.setText(GetReplies.replies.get(position - 1).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                replyViewHolder.upVote.setEnabled(true);
                replyViewHolder.downVote.setEnabled(true);

            }
        }
        else if (holder instanceof HeaderArea)
        {
            HeaderArea header = (HeaderArea) holder;
            String[] date = new String[0];
            try {
                date = GetFeed.posts.get(currentPostion).getString("post_date").split("-");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] time = new String[0];
            try {
                time = GetFeed.posts.get(currentPostion).getString("post_time").split(":");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            DateTime now = new DateTime();
            Period period = new Period(dateTime, now);
            if (period.getYears() != 0) {
                header.date_header.setText("" + period.getYears() + "y");
            } else if (period.getMonths() != 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getMonths() + "mo");

            } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getWeeks() + "w");

            } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getDays() + "d");

            } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getHours() + "h");

            } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getMinutes() + "m");

            } else if (period.getSeconds() < 0 || period.getSeconds() != 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getSeconds() + "s");


            } else {
                header.date_header.setText("now");
            }
            try {
                header.textView.setText(GetFeed.posts.get(currentPostion).getString("text"));
                header.count.setText(GetFeed.posts.get(currentPostion).getString("reply_count"));
                if (!GetFeed.posts.get(currentPostion).isNull("img"))
                {
                    if (GetFeed.bitmaps.containsKey(currentPostion))
                    {
                        header.img.setVisibility(View.VISIBLE);
                        header.img.setImageBitmap(GetFeed.bitmaps.get(currentPostion));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (Yammers.integerHashMap.get(currentPostion) == 1) {
                if (IndividualItemsAdapter.detect.containsKey(currentPostion)) {
                    if (IndividualItemsAdapter.detect.get(currentPostion) == 1) {
                        try {
                            int oldLike = GetFeed.posts.get(currentPostion).getInt("likes");
                            int newlike = oldLike + 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        header.likes.setText(GetFeed.posts.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.upVote.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    header.downVote.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    header.downVote.setEnabled(false);
                    header.upVote.setEnabled(false);
                }
                else
                {
                    header.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.up_vote_new));
                    header.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    header.downVote.setEnabled(false);
                    header.upVote.setEnabled(false);
                }
            } else if (Yammers.integerHashMap.get(currentPostion) == -1) {
                if (IndividualItemsAdapter.detect.containsKey(currentPostion)) {
                    Log.e("Dislike", "dislike detected");
                    if (IndividualItemsAdapter.detect.get(currentPostion) == -1) {
                        try {
                            int oldLike = GetFeed.posts.get(currentPostion).getInt("likes");
                            int newlike = oldLike - 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        header.likes.setText(GetFeed.posts.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.upVote.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    header.downVote.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    header.upVote.setEnabled(false);
                    header.downVote.setEnabled(false);
                }
                else
                {
                    header.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    header.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    header.upVote.setEnabled(false);
                    header.downVote.setEnabled(false);
                }
            } else if (Yammers.integerHashMap.get(currentPostion) == 0) {
                try {
                    header.likes.setText(GetFeed.posts.get(currentPostion).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.upVote.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    header.downVote.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    header.upVote.setEnabled(true);
                    header.downVote.setEnabled(true);
                }
                else
                {
                    header.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    header.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    header.upVote.setEnabled(true);
                    header.downVote.setEnabled(true);
                }
            }
        }
        else if (holder instanceof HeaderAreaMyPosts)
        {
            HeaderAreaMyPosts header= (HeaderAreaMyPosts) holder;
            String[] date = new String[0];
            try {
                date = ProfileGetPosts.posts.get(currentPostion).getString("post_date").split("-");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] time = new String[0];
            try {
                time = ProfileGetPosts.posts.get(currentPostion).getString("post_time").split(":");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            DateTime now = new DateTime();
            Period period = new Period(dateTime, now);
            if (period.getYears() != 0) {
                header.date_header.setText("" + period.getYears() + "y");
            } else if (period.getMonths() != 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getMonths() + "mo");

            } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getWeeks() + "w");

            } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getDays() + "d");

            } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getHours() + "h");

            } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getMinutes() + "m");

            } else if (period.getSeconds() < 0 || period.getSeconds() != 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getSeconds() + "s");


            } else {
                header.date_header.setText("now");
            }
            try {
                header.textView.setText(ProfileGetPosts.posts.get(currentPostion).getString("text"));
                header.count.setText(ProfileGetPosts.posts.get(currentPostion).getString("reply_count"));
                if (!ProfileGetPosts.posts.get(currentPostion).isNull("img"))
                {
                    if (ProfileGetPosts.bitmaps.containsKey(currentPostion))
                    {
                        header.img.setVisibility(View.VISIBLE);
                        header.img.setImageBitmap(ProfileGetPosts.bitmaps.get(currentPostion));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (MyPostsFrag.integerHashMap.get(currentPostion) == 1)
            {
                if (MyPostsAdapter.detect.containsKey(currentPostion)) {
                    if (MyPostsAdapter.detect.get(currentPostion) == 1) {
                        try {
                            int oldLike = ProfileGetPosts.posts.get(currentPostion).getInt("likes");
                            int newlike = oldLike + 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        header.likes.setText(ProfileGetPosts.posts.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.upVote.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    header.downVote.setEnabled(false);
                    header.upVote.setEnabled(false);
                }
                else
                {
                    header.upVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.up_vote_new));
                    header.downVote.setEnabled(false);
                    header.upVote.setEnabled(false);
                }
            }
            else if (MyPostsFrag.integerHashMap.get(currentPostion) == -1)
            {
                if (MyPostsAdapter.detect.containsKey(currentPostion)) {
                    Log.e("Dislike", "dislike detected");
                    if (MyPostsAdapter.detect.get(currentPostion) == -1) {
                        try {
                            int oldLike = ProfileGetPosts.posts.get(currentPostion).getInt("likes");
                            int newlike = oldLike - 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        header.likes.setText(ProfileGetPosts.posts.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.downVote.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    header.upVote.setEnabled(false);
                    header.downVote.setEnabled(false);
                }
                else
                {
                    header.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    header.upVote.setEnabled(false);
                    header.downVote.setEnabled(false);
                }
            } else if (MyPostsFrag.integerHashMap.get(currentPostion) == 0) {
                try {
                    header.likes.setText(ProfileGetPosts.posts.get(currentPostion).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                header.upVote.setEnabled(true);
                header.downVote.setEnabled(true);

            }

        }
        else if (holder instanceof HeaderAreaMyReply)
        {
            HeaderAreaMyReply header= (HeaderAreaMyReply) holder;
            String[] date = new String[0];
            try {
                date = ProfileGetPosts.posts.get(currentPostion).getString("post_date").split("-");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] time = new String[0];
            try {
                time = ProfileGetPosts.posts.get(currentPostion).getString("post_time").split(":");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            DateTime now = new DateTime();
            Period period = new Period(dateTime, now);
            if (period.getYears() != 0) {
                header.date_header.setText("" + period.getYears() + "y");
            } else if (period.getMonths() != 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getMonths() + "mo");

            } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getWeeks() + "w");

            } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date_header.setText("" + period.getDays() + "d");

            } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getHours() + "h");

            } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getMinutes() + "m");

            } else if (period.getSeconds() < 0 || period.getSeconds() != 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date_header.setText("" + period.getSeconds() + "s");


            } else {
                header.date_header.setText("now");
            }
            try {
                header.textView.setText(ProfileGetPosts.posts.get(currentPostion).getString("text"));
                header.count.setText(ProfileGetPosts.posts.get(currentPostion).getString("reply_count"));
                if (!ProfileGetPosts.posts.get(currentPostion).isNull("img"))
                {
                    if (ProfileGetPosts.bitmaps.containsKey(currentPostion))
                    {
                        header.img.setVisibility(View.VISIBLE);
                        header.img.setImageBitmap(ProfileGetPosts.bitmaps.get(currentPostion));
                    }
                    else
                    {
                        header.img.setVisibility(View.GONE);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (MyRepliesFrag.integerHashMap.get(currentPostion) == 1)
            {
                if (MyRepliesAdapter.detect.containsKey(currentPostion)) {
                    if (MyRepliesAdapter.detect.get(currentPostion) == 1) {
                        try {
                            int oldLike = ProfileGetPosts.posts.get(currentPostion).getInt("likes");
                            int newlike = oldLike + 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        header.likes.setText(ProfileGetPosts.posts.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.upVote.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    header.downVote.setEnabled(false);
                    header.upVote.setEnabled(false);
                }
            }
            else if (MyRepliesFrag.integerHashMap.get(currentPostion) == -1)
            {
                if (MyRepliesAdapter.detect.containsKey(currentPostion)) {
                    if (MyRepliesAdapter.detect.get(currentPostion) == -1) {
                        try {
                            int oldLike = ProfileGetPosts.posts.get(currentPostion).getInt("likes");
                            int newlike = oldLike - 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        header.likes.setText(ProfileGetPosts.posts.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.downVote.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    header.upVote.setEnabled(false);
                    header.downVote.setEnabled(false);
                }
                else
                {
                    header.downVote.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    header.upVote.setEnabled(false);
                    header.downVote.setEnabled(false);
                }
            } else if (MyRepliesFrag.integerHashMap.get(currentPostion) == 0) {
                try {
                    header.likes.setText(ProfileGetPosts.posts.get(currentPostion).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                header.upVote.setEnabled(true);
                header.downVote.setEnabled(true);

            }

        }

        else if (holder instanceof ViewHolderHeader)
        {
            ViewHolderHeader header = (ViewHolderHeader) holder;
            Log.e("header","Global");

            try {

                header.globLocation.setText(GetglobalFeed.postsGlobal.get(currentPostion).get("city")+" "+GetglobalFeed.postsGlobal.get(currentPostion).get("country"));
                header.count.setText(GetglobalFeed.postsGlobal.get(currentPostion).getString("reply_count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] date = new String[0];
            try {
                date = GetglobalFeed.postsGlobal.get(currentPostion).getString("post_date").split("-");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] time = new String[0];
            try {
                time = GetglobalFeed.postsGlobal.get(currentPostion).getString("post_time").split(":");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            DateTime now = new DateTime();
            Period period = new Period(dateTime, now);
            if (period.getYears() != 0) {
                header.date.setText("" + period.getYears() + "y");
            } else if (period.getMonths() != 0 && period.getYears() == 0) {
                header.date.setText("" + period.getMonths() + "mo");

            } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date.setText("" + period.getWeeks() + "w");

            } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                header.date.setText("" + period.getDays() + "d");

            } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date.setText("" + period.getHours() + "h");

            } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date.setText("" + period.getMinutes() + "m");

            } else if (period.getSeconds() < 0 || period.getSeconds() != 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {

                header.date.setText("" + period.getSeconds() + "s");


            } else {
                header.date.setText("now");
            }
            try {
                header.textView.setText(GetglobalFeed.postsGlobal.get(currentPostion).getString("text"));
                header.count.setText(GetglobalFeed.postsGlobal.get(currentPostion).getString("reply_count"));
                if (!GetglobalFeed.postsGlobal.get(currentPostion).isNull("img"))
                {
                    if (GetglobalFeed.bitmaps.containsKey(currentPostion))
                    {
                        header.img.setVisibility(View.VISIBLE);
                        header.img.setImageBitmap(GetglobalFeed.bitmaps.get(currentPostion));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (GlobalFeedFragment.integerHashMap.get(currentPostion) == 1) {
                Log.e("header","1");

                if (GlobalAdapetr.detect.containsKey(currentPostion)) {
                    if (GlobalAdapetr.detect.get(currentPostion) == 1) {
                        try {
                            int oldLike = GetglobalFeed.postsGlobal.get(currentPostion).getInt("likes");
                            int newlike = oldLike + 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        header.likes.setText(GetglobalFeed.postsGlobal.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.heart.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    header.downHeart.setEnabled(false);
                    header.heart.setEnabled(false);
                }
            }
            else if (GlobalFeedFragment.integerHashMap.get(currentPostion) == -1) {
                Log.e("header","-1");

                if (GlobalAdapetr.detect.containsKey(currentPostion)) {
                    if (GlobalAdapetr.detect.get(currentPostion) == -1) {
                        try {
                            int oldLike = GetglobalFeed.postsGlobal.get(currentPostion).getInt("likes");
                            int newlike = oldLike - 1;
                            header.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {

                    try {
                        header.likes.setText(GetglobalFeed.postsGlobal.get(currentPostion).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    header.downHeart.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    header.heart.setEnabled(false);
                    header.downHeart.setEnabled(false);
                }
                else
                {
                    header.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    header.heart.setEnabled(false);
                    header.downHeart.setEnabled(false);
                }
            }
            else if (GlobalFeedFragment.integerHashMap.get(currentPostion) == 0) {
                Log.e("header", "0");

                try {

                    header.likes.setText(GetglobalFeed.postsGlobal.get(currentPostion).getString("likes"));
                    header.heart.setEnabled(true);
                    header.downHeart.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


        }
    }


    @Override
    public int getItemCount() {
        return GetReplies.replies.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPostionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_REPLIES;
        }
    }

    public boolean isPostionHeader(int postion) {
        return postion == 0;
    }


    public static class ReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView textView;
        TextView likes;
        ImageView upVote;
        ImageView downVote;
        HandleOnClicks handleOnClicks;
        ImageView pro_pic;
        TextView date;
        TextView count;

        public ReplyViewHolder(View itemView, HandleOnClicks handleOnClicks) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.individual_reply_main_card);
            textView = (TextView) itemView.findViewById(R.id.individual_reply_main_text);
            likes = (TextView) itemView.findViewById(R.id.individual_reply_likes);
            upVote = (ImageView) itemView.findViewById(R.id.individual_reply_up);
            downVote = (ImageView) itemView.findViewById(R.id.individual_reply_down);
            pro_pic= (ImageView) itemView.findViewById(R.id.pro_pic);
            date= (TextView) itemView.findViewById(R.id.date);
            count= (TextView) itemView.findViewById(R.id.count_local);
            this.handleOnClicks = handleOnClicks;
            upVote.setOnClickListener(this);
            downVote.setOnClickListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.individual_reply_main_card) {
                Log.e("MainCard", "is Pressed");
                handleOnClicks.onCardPressed((CardView) v, getAdapterPosition());
            } else if (v.getId() == R.id.individual_reply_up) {
                Log.e("UpVote", "is Pressed");

                handleOnClicks.onUpPressed((ImageView) v, getAdapterPosition());
            } else if (v.getId() == R.id.individual_reply_down) {
                Log.e("DownVote", "is Pressed");

                handleOnClicks.onDownPressed((ImageView) v, getAdapterPosition());
            }

        }

        public interface HandleOnClicks {
            public void onCardPressed(CardView mainCard, int pos);

            public void onUpPressed(ImageView img, int pos);

            public void onDownPressed(ImageView img, int pos);
        }
    }
    public static class HeaderAreaMyPosts extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView count;
        TextView textView;
        TextView likes;
        ImageView upVote;
        ImageView downVote;
        HandleOnClicks handleOnClicks;
        TextView date_header;
        ImageView img;


        public HeaderAreaMyPosts(View itemView, HandleOnClicks handleOnClicks) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.indi_main_text);
            likes = (TextView) itemView.findViewById(R.id.indi_likes);
            upVote = (ImageView) itemView.findViewById(R.id.indi_up);
            downVote = (ImageView) itemView.findViewById(R.id.indi_down);
            date_header = (TextView) itemView.findViewById(R.id.date_header);
            count= (TextView) itemView.findViewById(R.id.count_local);
            img= (ImageView) itemView.findViewById(R.id.indi_img);
            this.handleOnClicks = handleOnClicks;
            upVote.setOnClickListener(this);
            downVote.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.indi_up) {
                Log.e("UpVote", "is Pressed");

                handleOnClicks.onUpPressed((ImageView) v, getAdapterPosition());
            } else if (v.getId() == R.id.indi_down) {
                Log.e("DownVote", "is Pressed");

                handleOnClicks.onDownPressed((ImageView) v, getAdapterPosition());
            }

        }

        public interface HandleOnClicks {
            public void onUpPressed(ImageView img, int pos);

            public void onDownPressed(ImageView img, int pos);
        }
    }

    public static class HeaderArea extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView count;
        TextView textView;
        TextView likes;
        ImageView upVote;
        ImageView downVote;
        HandleOnClicks handleOnClicks;
        TextView date_header;
        ImageView img;


        public HeaderArea(View itemView, HandleOnClicks handleOnClicks) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.indi_main_text);
            likes = (TextView) itemView.findViewById(R.id.indi_likes);
            upVote = (ImageView) itemView.findViewById(R.id.indi_up);
            downVote = (ImageView) itemView.findViewById(R.id.indi_down);
            date_header = (TextView) itemView.findViewById(R.id.date_header);
            count= (TextView) itemView.findViewById(R.id.count_local);
            img= (ImageView) itemView.findViewById(R.id.indi_img);

            this.handleOnClicks = handleOnClicks;
            upVote.setOnClickListener(this);
            downVote.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.indi_up) {
                Log.e("UpVote", "is Pressed");

                handleOnClicks.onUpPressed((ImageView) v, getAdapterPosition());
            } else if (v.getId() == R.id.indi_down) {
                Log.e("DownVote", "is Pressed");

                handleOnClicks.onDownPressed((ImageView) v, getAdapterPosition());
            }

        }

        public interface HandleOnClicks {
            public void onUpPressed(ImageView img, int pos);

            public void onDownPressed(ImageView img, int pos);
        }
    }
    private static class HeaderAreaMyReply extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView count;
        ImageView pro_pic;
        TextView textView;
        TextView likes;
        ImageView upVote;
        ImageView downVote;
        HandleOnClicks handleOnClicks;
        TextView date_header;
        ImageView img;


        public HeaderAreaMyReply(View itemView, HandleOnClicks handleOnClicks) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.indi_main_text);
            likes = (TextView) itemView.findViewById(R.id.indi_likes);
            upVote = (ImageView) itemView.findViewById(R.id.indi_up);
            downVote = (ImageView) itemView.findViewById(R.id.indi_down);
            date_header = (TextView) itemView.findViewById(R.id.date_header);
            pro_pic= (ImageView) itemView.findViewById(R.id.pro_pic);
            count= (TextView) itemView.findViewById(R.id.count_local);
            img= (ImageView) itemView.findViewById(R.id.indi_img);

            this.handleOnClicks = handleOnClicks;
            upVote.setOnClickListener(this);
            downVote.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.indi_up) {

                handleOnClicks.onUpPressed((ImageView) v, getAdapterPosition());
            } else if (v.getId() == R.id.indi_down) {

                handleOnClicks.onDownPressed((ImageView) v, getAdapterPosition());
            }

        }

        public interface HandleOnClicks {
            public void onUpPressed(ImageView img, int pos);

            public void onDownPressed(ImageView img, int pos);
        }
    }

    private static class ViewHolderHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView heart;
        ImageView downHeart;
        TextView date_header;
        TextView textView;
        TextView likes;
        MyViewclickHolder myViewclickHolder;
        TextView count;
        TextView globLocation;
        TextView date;
        ImageView img;

        public ViewHolderHeader(View itemView, MyViewclickHolder myViewclickHolder) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.indi_main_text);
            likes = (TextView) itemView.findViewById(R.id.indi_likes);
            heart = (ImageView) itemView.findViewById(R.id.indi_up);
            downHeart = (ImageView) itemView.findViewById(R.id.indi_down);
            date_header = (TextView) itemView.findViewById(R.id.date_header);
            date = (TextView) itemView.findViewById(R.id.date_header);
            count= (TextView) itemView.findViewById(R.id.glob_count);
            globLocation= (TextView) itemView.findViewById(R.id.glob_location);
            img= (ImageView) itemView.findViewById(R.id.indi_img);
            this.myViewclickHolder = myViewclickHolder;
            heart.setOnClickListener(this);
            downHeart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.indi_up) {
                Log.e("pressed","babe");
                myViewclickHolder.onHeartPress(v, getAdapterPosition());
            } else if (v.getId() == R.id.indi_down) {
                myViewclickHolder.onDownheartPressed(v, getAdapterPosition());
                Log.e("pressed", "babe");


            }
        }

        public interface MyViewclickHolder {
            public void onHeartPress(View imgB, int pos);


            public void onDownheartPressed(View imB, int pos);
        }
    }


}