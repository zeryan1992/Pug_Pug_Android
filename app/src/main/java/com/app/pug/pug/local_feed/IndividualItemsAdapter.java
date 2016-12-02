package com.app.pug.pug.local_feed;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pug.pug.R;
import com.app.pug.pug.SingleImageView;
import com.app.pug.pug.one_post_view.IndividualPosts;
import com.app.pug.pug.like_posts.LikeAction;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONException;

import java.util.HashMap;


public class IndividualItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static HashMap<Integer, Integer> detect = new HashMap<>();
    int times = 0;
    private AppCompatActivity activity;
    Fragment fragment;

    public IndividualItemsAdapter(AppCompatActivity activity, Fragment frag) throws JSONException {
        this.activity = activity;
        this.fragment=frag;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_feed_post_view, parent, false);
            final PostsHolder holder = new PostsHolder(view, new PostsHolder.MyViewclickHolder() {

                @Override
                public void onCardPress(CardView cardView, int pos) {
                    if (GetFeed.posts.size() != 0) {
                        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
                        try {
                            if (!GetFeed.posts.get(pos).isNull("self") && GetFeed.posts.get(pos).getBoolean("self") && !GetFeed.posts.get(pos).isNull("can_reply") && GetFeed.posts.get(pos).getBoolean("can_reply")) {
                                Fragment indi = IndividualPosts.newInstance(pos, "local_header", "local", true, GetFeed.posts.get(pos).getBoolean("can_reply"));
                                ft.replace(R.id.replace, indi);
                                ft.addToBackStack(null);
                                ft.commit();


                            } else {
                                Fragment indi = IndividualPosts.newInstance(pos, "local_header", "local", false, GetFeed.posts.get(pos).getBoolean("can_reply"));
                                ft.replace(R.id.replace, indi);
                                ft.addToBackStack(null);
                                ft.show(indi).commit();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }


                @Override
                public void onHeartPress(View imgB, int pos)
                {
                    if (Yammers.integerHashMap.get(pos) == 0) {
                        Yammers.integerHashMap.set(pos, 1);
                        detect.put(pos, 1);
                        notifyItemChanged(pos);
                        final LikeAction likeAction = new LikeAction(activity, GetFeed.posts.get(pos));
                        likeAction.setLikeAction(1);
                        likeAction.execute();
                    }
                }

                @Override
                public void onDownheartPressed(View imB, int pos) {
                    if (Yammers.integerHashMap.get(pos) == 0) {
                        Yammers.integerHashMap.set(pos, -1);
                        detect.put(pos, -1);
                        notifyItemChanged(pos);
                        final LikeAction likeAction = new LikeAction(activity, GetFeed.posts.get(pos));
                        likeAction.setLikeAction(-1);
                        likeAction.execute();
                    }
                }

                @Override
                public void onImagePressed(View imB, int pos) {
                    FragmentTransaction ft=fragment.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
                    ft.replace(R.id.replace, SingleImageView.newInstance(pos,"local"));
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });
            return holder;
        }
        else if (viewType==0)
        {
            View noPosts=LayoutInflater.from(parent.getContext()).inflate(R.layout.no_posts,parent,false);
            RecyclerView.ViewHolder noPostsHolder=new NoPosts(noPosts);
            return noPostsHolder;
        }
        else
        {
            throw new RuntimeException("Error finding the right ViewHolder");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof PostsHolder)
        {
            final PostsHolder postsHolder= (PostsHolder) viewHolder;
            try {

                String[] date = GetFeed.posts.get(position).getString("post_date").split("-");
                String[] time = GetFeed.posts.get(position).getString("post_time").split(":");
                DateTime dateTime = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
                DateTime now = new DateTime();
                Period period = new Period(dateTime, now);
                if (period.getYears() != 0) {
                    times = period.getYears();
                    postsHolder.date.setText("" + period.getYears() + "y");
                } else if (period.getMonths() != 0 && period.getYears() == 0) {
                    times = period.getMonths();
                    postsHolder.date.setText("" + period.getMonths() + "mo");

                } else if (period.getWeeks() != 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getWeeks();
                    postsHolder.date.setText("" + period.getWeeks() + "w");

                } else if (period.getDays() != 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getDays();
                    postsHolder.date.setText("" + period.getDays() + "d");

                } else if (period.getHours() != 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getHours();
                    postsHolder.date.setText("" + period.getHours() + "h");

                } else if (period.getMinutes() != 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getMinutes();
                    postsHolder.date.setText("" + period.getMinutes() + "m");

                } else if (period.getSeconds() >= 0  && period.getMinutes() == 0 && period.getHours() == 0 && period.getDays() == 0 && period.getWeeks() == 0 && period.getMonths() == 0 && period.getYears() == 0) {
                    times = period.getSeconds();
                    postsHolder.date.setText("" + period.getSeconds() + "s");


                } else {
                    postsHolder.date.setText("0s");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (!GetFeed.posts.get(position).isNull("img"))
                {
                    if (GetFeed.bitmaps.containsKey(position))
                    {
                        Bitmap bitmap = GetFeed.bitmaps.get(position);
                        postsHolder.img.setVisibility(View.VISIBLE);
                        postsHolder.img.setImageBitmap(bitmap);
                    }
                }
                else
                {
                    postsHolder.img.setVisibility(View.GONE);
                }
                postsHolder.textView.setText(GetFeed.posts.get(position).getString("text"));
                if (!GetFeed.posts.get(position).isNull("reply_count")) {
                    postsHolder.count.setText(GetFeed.posts.get(position).getString("reply_count"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (Yammers.integerHashMap.get(position) == 1) {
                if (detect.containsKey(position)) {
                    if (detect.get(position) == 1) {
                        try {
                            int oldLike = GetFeed.posts.get(position).getInt("likes");
                            int newlike = oldLike + 1;
                            postsHolder.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        postsHolder.likes.setText(GetFeed.posts.get(position).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    postsHolder.heart.setImageDrawable(activity.getDrawable(R.drawable.up_vote_new));
                    postsHolder.downHeart.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    postsHolder.downHeart.setEnabled(false);
                    postsHolder.heart.setEnabled(false);
                } else {
                    postsHolder.heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.up_vote_new));
                    postsHolder.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    postsHolder.downHeart.setEnabled(false);
                    postsHolder.heart.setEnabled(false);
                }
            } else if (Yammers.integerHashMap.get(position) == -1) {
                if (detect.containsKey(position))
                {
                    if (detect.get(position) == -1)
                    {
                        try {
                            int oldLike = GetFeed.posts.get(position).getInt("likes");
                            int newlike = oldLike - 1;
                            postsHolder.likes.setText("" + newlike);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        postsHolder.likes.setText(GetFeed.posts.get(position).getString("likes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    postsHolder.heart.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    postsHolder.downHeart.setImageDrawable(activity.getDrawable(R.drawable.down_vote_new));
                    postsHolder.downHeart.setEnabled(false);
                    postsHolder.heart.setEnabled(false);
                } else {
                    postsHolder.heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    postsHolder.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.down_vote_new));
                    postsHolder.downHeart.setEnabled(false);
                    postsHolder.heart.setEnabled(false);
                }
            } else if (Yammers.integerHashMap.get(position) == 0) {
                try {
                    postsHolder.likes.setText(GetFeed.posts.get(position).getString("likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    postsHolder.heart.setImageDrawable(activity.getDrawable(R.drawable.gray_up_vote_new));
                    postsHolder.downHeart.setImageDrawable(activity.getDrawable(R.drawable.gray_down_vote_new));
                    postsHolder.heart.setEnabled(true);
                    postsHolder.downHeart.setEnabled(true);
                } else {
                    postsHolder.heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_up_vote_new));
                    postsHolder.downHeart.setImageDrawable(activity.getResources().getDrawable(R.drawable.gray_down_vote_new));
                    postsHolder.heart.setEnabled(true);
                    postsHolder.downHeart.setEnabled(true);
                }
            }
        }
        else if (viewHolder instanceof NoPosts)
        {
        }


    }

    @Override
    public int getItemCount() {
        if (GetFeed.posts.isEmpty())
        {
            return 1;
        }
        return GetFeed.posts.size();

    }

    @Override
    public int getItemViewType(int position) {
        if (GetFeed.posts.isEmpty())
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
        ImageView img;

        public PostsHolder(View itemView, MyViewclickHolder myViewclickHolder) {
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
            } else if (v.getId() == R.id.heart) {
                myViewclickHolder.onHeartPress(v, getAdapterPosition());
            } else if (v.getId() == R.id.disheart) {
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

            public void onImagePressed(View imB,int pos);
        }
    }

}
