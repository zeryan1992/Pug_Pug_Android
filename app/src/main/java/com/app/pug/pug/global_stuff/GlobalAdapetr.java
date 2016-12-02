package com.app.pug.pug.global_stuff;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pug.pug.R;
import com.app.pug.pug.SingleImageView;
import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.one_post_view.IndividualPosts;
import com.app.pug.pug.like_posts.LikeAction;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by zeryan on 3/3/16.
 */
public class GlobalAdapetr extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static HashMap<Integer, Integer> detect = new HashMap<>();
    int times = 0;
    private AppCompatActivity activity;

    public GlobalAdapetr(AppCompatActivity activity) throws JSONException {
        this.activity = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_feed_post_view, parent, false);
            final PostsHolder holder = new PostsHolder(view, new PostsHolder.MyViewclickHolder() {

                @Override
                public void onCardPress(CardView cardView, final int pos) {
                    if (GetglobalFeed.postsGlobal.size() != 0) {
                        ActivityMainDataSet.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

                                try {
                                    if (!GetglobalFeed.postsGlobal.get(pos).isNull("self") && GetglobalFeed.postsGlobal.get(pos).getBoolean("self")) {
                                        ft.replace(R.id.replace, IndividualPosts.newInstance(pos, "glob_header", "global", true, GetglobalFeed.postsGlobal.get(pos).getBoolean("can_reply")));

                                    } else {
                                        ft.replace(R.id.replace, IndividualPosts.newInstance(pos, "glob_header", "global", false, GetglobalFeed.postsGlobal.get(pos).getBoolean("can_reply")));

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ft.addToBackStack(null);
                                ft.commit();

                            }
                        });

                    }

                }

                @Override
                public void onHeartPress(View imgB, int pos) {
                    if (GlobalFeedFragment.integerHashMap.get(pos) == 0) {
                        GlobalFeedFragment.integerHashMap.set(pos, 1);
                        detect.put(pos, 1);
                        notifyItemChanged(pos);
                        LikeAction likeAction = new LikeAction(activity, GetglobalFeed.postsGlobal.get(pos));
                        likeAction.setLikeAction(1);
                        likeAction.execute();
                    }
                }

                @Override
                public void onDownheartPressed(View imB, int pos) {
                    if (GlobalFeedFragment.integerHashMap.get(pos) == 0) {
                        GlobalFeedFragment.integerHashMap.set(pos, -1);
                        detect.put(pos, -1);
                        notifyItemChanged(pos);
                        LikeAction likeAction = new LikeAction(activity, GetglobalFeed.postsGlobal.get(pos));
                        likeAction.setLikeAction(-1);
                        likeAction.execute();
                    }
                }

                @Override
                public void onImagePressed(View v, int adapterPosition) {
                    FragmentTransaction ft=activity.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
                    ft.replace(R.id.replace, SingleImageView.newInstance(adapterPosition, "global"));
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            return holder;
        }
        else if (viewType==0)
        {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.global_no_posts,parent,false);
            NoPosts noPosts=new NoPosts(view);
            return noPosts;
        }
        else
        {
            throw  new RuntimeException("Could not find the correct ViewHolder");
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PostsHolder) {
            PostsHolder posts= (PostsHolder) viewHolder;
            try {
                String[] date = GetglobalFeed.postsGlobal.get(position).getString("post_date").split("-");
                String[] time = GetglobalFeed.postsGlobal.get(position).getString("post_time").split(":");
                DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
                DateTime now = new DateTime();
                Period period = new Period(dateTime, now);
                if (period.getYears() != 0) {
                    times = period.getYears();
                    posts.date.setText("" + period.getYears() + "y");
                } else if (period.getMonths() != 0 && period.getYears() == 0) {
                    times = period.getMonths();
                    posts.date.setText("" + period.getMonths() + "mo");

                } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getWeeks();
                    posts.date.setText("" + period.getWeeks() + "w");

                } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getDays();
                    posts.date.setText("" + period.getDays() + "d");

                } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getHours();

                    posts.date.setText("" + period.getHours() + "h");

                } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getMinutes();

                    posts.date.setText("" + period.getMinutes() + "m");

                } else if (period.getSeconds() >= 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getSeconds();

                    posts.date.setText("" + period.getSeconds() + "s");


                } else {
                    posts.date.setText("0s");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try
            {
                posts.textView.setText(GetglobalFeed.postsGlobal.get(position).getString("text"));
                String location = GetglobalFeed.postsGlobal.get(position).getString("city") + ", " + GetglobalFeed.postsGlobal.get(position).getString("country");
                posts.location.setText(location);


                if (!GetglobalFeed.postsGlobal.get(position).isNull("reply_count")) {
                    posts.count.setText(GetglobalFeed.postsGlobal.get(position).getString("reply_count"));
                }
                if (!GetglobalFeed.postsGlobal.get(position).isNull("img"))
                {

                    Bitmap bitmap=GetglobalFeed.bitmaps.get(position);
                    if (bitmap!=null) {
                        posts.img.setVisibility(View.VISIBLE);
                        posts.img.setImageBitmap(bitmap);
                    }
                    else
                    {
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (GlobalFeedFragment.integerHashMap.get(position) == 1) {
                if (detect.containsKey(position)) {
                    if (detect.get(position) == 1) {
                        try {
                            int oldLike = GetglobalFeed.postsGlobal.get(position).getInt("likes");
                            int newlike = oldLike + 1;
                            posts.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        posts.likes.setText(GetglobalFeed.postsGlobal.get(position).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    posts.heart.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    posts.downHeart.setEnabled(false);
                    posts.heart.setEnabled(false);
                } else {
                    posts.heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    posts.downHeart.setEnabled(false);
                    posts.heart.setEnabled(false);
                }
            } else if (GlobalFeedFragment.integerHashMap.get(position) == -1)
            {
                if (detect.containsKey(position)) {
                    if (detect.get(position) == -1) {
                        try {
                            int oldLike = GetglobalFeed.postsGlobal.get(position).getInt("likes");
                            int newlike = oldLike - 1;
                            posts.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        posts.likes.setText(GetglobalFeed.postsGlobal.get(position).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    posts.heart.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    posts.downHeart.setEnabled(false);
                    posts.heart.setEnabled(false);
                } else {
                    posts.heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    posts.downHeart.setEnabled(false);
                    posts.heart.setEnabled(false);
                }
            } else if (GlobalFeedFragment.integerHashMap.get(position) == 0) {
                try {
                    posts.likes.setText(GetglobalFeed.postsGlobal.get(position).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    posts.heart.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    posts.heart.setEnabled(true);
                    posts.downHeart.setEnabled(true);
                } else {
                    posts.heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    posts.heart.setEnabled(true);
                    posts.downHeart.setEnabled(true);
                }
            }
        }
        else if (viewHolder instanceof NoPosts)
        {
        }


    }

    @Override
    public int getItemCount() {
        if (GetglobalFeed.postsGlobal.isEmpty())
        {
            return 1;
        }
        return GetglobalFeed.postsGlobal.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (GetglobalFeed.postsGlobal.isEmpty())
        {
            return 0;
        }
        return 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    private static class NoPosts extends RecyclerView.ViewHolder {
        CardView noPosts;


        public NoPosts(View itemView) {
            super(itemView);
            noPosts= (CardView) itemView.findViewById(R.id.noPosts_card);

        }

    }

    private static class PostsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView textView;
        TextView likes;
        ImageView heart;
        ImageView downHeart;
        MyViewclickHolder myViewclickHolder;
        ImageView replyImage;
        TextView count;
        TextView date;
        TextView location;
        ImageView img;

        public PostsHolder(View itemView, MyViewclickHolder myViewclickHolder) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.glob_card);
            textView = (TextView) itemView.findViewById(R.id.glob_mainPost);
            likes = (TextView) itemView.findViewById(R.id.glob_likes);
            heart = (ImageView) itemView.findViewById(R.id.glob_heart);
            downHeart = (ImageView) itemView.findViewById(R.id.glob_disheart);
            replyImage = (ImageView) itemView.findViewById(R.id.glob_reply_count);
            count = (TextView) itemView.findViewById(R.id.glob_count);
            date = (TextView) itemView.findViewById(R.id.glob_date);
            location = (TextView) itemView.findViewById(R.id.glob_location);
            img= (ImageView) itemView.findViewById(R.id.img_post);
            this.myViewclickHolder = myViewclickHolder;
            heart.setOnClickListener(this);
            cardView.setOnClickListener(this);
            downHeart.setOnClickListener(this);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.glob_card) {
                myViewclickHolder.onCardPress((CardView) v, getAdapterPosition());
            } else if (v.getId() == R.id.glob_heart) {
                myViewclickHolder.onHeartPress(v, getAdapterPosition());
            } else if (v.getId() == R.id.glob_disheart) {
                myViewclickHolder.onDownheartPressed(v, getAdapterPosition());

            }
            else if (v.getId()==R.id.img_post)
            {
                myViewclickHolder.onImagePressed(v,getAdapterPosition());
            }
        }

        public interface MyViewclickHolder {
            public void onCardPress(CardView cardView, int pos);

            public void onHeartPress(View imgB, int pos);

            public void onDownheartPressed(View imB, int pos);

            public void onImagePressed(View v, int adapterPosition);
        }
    }
}
