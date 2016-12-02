package com.app.pug.pug.pro_posts;

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
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.one_post_view.IndividualPosts;
import com.app.pug.pug.like_posts.LikeAction;
import com.app.pug.pug.local_feed.Yammers;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MyPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static HashMap<Integer, Integer> detect = new HashMap<>();
    int times = 0;
    private AppCompatActivity activity;

    public MyPostsAdapter(AppCompatActivity activity) throws JSONException {
        this.activity = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_feed_post_view, parent, false);
            final Posts holder = new Posts(view, new Posts.MyViewclickHolder() {
                @Override
                public void onCardPress(CardView cardView, final int pos) {
                    if (ProfileGetPosts.posts.size() != 0) {
                        ActivityMainDataSet.handler.postAtFrontOfQueue(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

                                try {
                                    if (!ProfileGetPosts.posts.get(pos).isNull("self") && ProfileGetPosts.posts.get(pos).getBoolean("self")) {
                                        ft.replace(R.id.my_posts, IndividualPosts.newInstance(pos, "my_posts", "my_posts", true, ProfileGetPosts.posts.get(pos).getBoolean("can_reply")));

                                    } else {
                                        ft.replace(R.id.my_posts, IndividualPosts.newInstance(pos, "my_posts", "my_posts", false, ProfileGetPosts.posts.get(pos).getBoolean("can_reply")));

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
                    Log.e("YammersPos", "" + Yammers.integerHashMap.get(pos) + " at postion: " + pos);
                    if (MyPostsFrag.integerHashMap.get(pos) == 0) {
                        Log.e("YammersPos", "" + Yammers.integerHashMap.get(pos) + " at postion: " + pos);
                        MyPostsFrag.integerHashMap.set(pos, 1);
                        detect.put(pos, 1);
                        notifyItemChanged(pos);
                        final LikeAction likeAction = new LikeAction(activity, ProfileGetPosts.posts.get(pos));
                        ActivityMainDataSet.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                likeAction.setLikeAction(1);
                                try {
                                    if (likeAction.execute().get()) {
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }

                }

                @Override
                public void onDownheartPressed(View imB, int pos) {
                    if (MyPostsFrag.integerHashMap.get(pos) == 0) {
                        MyPostsFrag.integerHashMap.set(pos, -1);
                        detect.put(pos, -1);
                        notifyItemChanged(pos);

                        final LikeAction likeAction = new LikeAction(activity, ProfileGetPosts.posts.get(pos));
                        ActivityMainDataSet.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                likeAction.setLikeAction(-1);
                                try {
                                    if (likeAction.execute().get()) {
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }
                }

                @Override
                public void onImagePressed(View v, int pos) {
                    FragmentTransaction ft=activity.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
                    ft.replace(R.id.my_posts, SingleImageView.newInstance(pos, "my_posts"));
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            return holder;
        }
        else if (viewType==0)
        {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.myposts_no_post,parent,false);
            NoPosts noPosts=new NoPosts(view);
            return noPosts;
        }
        else
        {
            throw  new RuntimeException("Error finding a view type make sure you are handling them correctly");
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof Posts) {
            Posts posts= (Posts) viewHolder;
            try {
                String[] date = ProfileGetPosts.posts.get(position).getString("post_date").split("-");
                String[] time = ProfileGetPosts.posts.get(position).getString("post_time").split(":");
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

                } else if (period.getSeconds() != 0 && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getSeconds();

                    posts.date.setText("" + period.getSeconds() + "s");


                } else {
                    posts.date.setText("0s");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                posts.textView.setText(ProfileGetPosts.posts.get(position).getString("text"));
                if (!ProfileGetPosts.posts.get(position).isNull("reply_count")) {
                    posts.count.setText(ProfileGetPosts.posts.get(position).getString("reply_count"));
                }
                if (!ProfileGetPosts.posts.get(position).isNull("img"))
                {
                    if (ProfileGetPosts.bitmaps.containsKey(position)) {
                        posts.img.setVisibility(View.VISIBLE);
                        Bitmap bitmap = ProfileGetPosts.bitmaps.get(position);
                        posts.img.setImageBitmap(bitmap);
                    }
                }
                else
                {
                    posts.img.setVisibility(View.GONE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (MyPostsFrag.integerHashMap.get(position) == 1) {
                if (detect.containsKey(position)) {
                    if (detect.get(position) == 1) {
                        try {
                            int oldLike = ProfileGetPosts.posts.get(position).getInt("likes");
                            int newlike = oldLike + 1;
                            posts.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        posts.likes.setText(ProfileGetPosts.posts.get(position).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    posts.heart.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    posts.downHeart.setEnabled(false);
                    posts.heart.setEnabled(false);
                }
            } else if (MyPostsFrag.integerHashMap.get(position) == -1) {
                if (detect.containsKey(position)) {
                    if (detect.get(position) == -1) {
                        try {
                            int oldLike = ProfileGetPosts.posts.get(position).getInt("likes");
                            int newlike = oldLike - 1;
                            posts.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        posts.likes.setText(ProfileGetPosts.posts.get(position).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    posts.heart.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    posts.downHeart.setEnabled(false);
                    posts.heart.setEnabled(false);
                }
            } else if (MyPostsFrag.integerHashMap.get(position) == 0) {
                try {
                    posts.likes.setText(ProfileGetPosts.posts.get(position).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    posts.heart.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    posts.downHeart.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
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
        if (ProfileGetPosts.posts.isEmpty())
        {
            return 1;
        }
        return ProfileGetPosts.posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (ProfileGetPosts.posts.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    private static class Posts extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView textView;
        TextView likes;
        ImageView heart;
        ImageView downHeart;
        MyViewclickHolder myViewclickHolder;
        ImageView replyImage;
        TextView count;
        TextView date;
        ImageView img;

        public Posts(View itemView, MyViewclickHolder myViewclickHolder) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card);
            textView = (TextView) itemView.findViewById(R.id.mainPost);
            likes = (TextView) itemView.findViewById(R.id.likes);
            heart = (ImageView) itemView.findViewById(R.id.heart);
            downHeart = (ImageView) itemView.findViewById(R.id.disheart);
            replyImage = (ImageView) itemView.findViewById(R.id.reply_count);
            count = (TextView) itemView.findViewById(R.id.count);
            date = (TextView) itemView.findViewById(R.id.date);
            img= (ImageView) itemView.findViewById(R.id.img_post);
            this.myViewclickHolder = myViewclickHolder;
            heart.setOnClickListener(this);
            cardView.setOnClickListener(this);
            downHeart.setOnClickListener(this);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.card) {
                myViewclickHolder.onCardPress((CardView) v, getAdapterPosition());
            }
            else if (v.getId() == R.id.heart) {
                myViewclickHolder.onHeartPress(v, getAdapterPosition());
            }
            else if (v.getId() == R.id.disheart) {

                myViewclickHolder.onDownheartPressed(v, getAdapterPosition());

            }
            else if(v.getId()==R.id.img_post)
            {
                myViewclickHolder.onImagePressed(v,getAdapterPosition());
            }
        }

        public interface MyViewclickHolder {
            public void onCardPress(CardView cardView, int pos);

            public void onHeartPress(View imgB, int pos);

            public void onDownheartPressed(View imB, int pos);

            public void onImagePressed(View v, int pos);
        }
    }
    private static class NoPosts extends RecyclerView.ViewHolder {
        CardView noPosts;


        public NoPosts(View itemView) {
            super(itemView);
            noPosts= (CardView) itemView.findViewById(R.id.noPosts_card);

        }

    }

}
