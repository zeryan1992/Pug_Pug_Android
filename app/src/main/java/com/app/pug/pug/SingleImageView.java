package com.app.pug.pug;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pug.pug.global_stuff.GetglobalFeed;
import com.app.pug.pug.global_stuff.GlobalAdapetr;
import com.app.pug.pug.global_stuff.GlobalFeedFragment;
import com.app.pug.pug.like_posts.LikeAction;
import com.app.pug.pug.local_feed.GetFeed;
import com.app.pug.pug.local_feed.IndividualItemsAdapter;
import com.app.pug.pug.local_feed.Yammers;
import com.app.pug.pug.pro_posts.MyPostsAdapter;
import com.app.pug.pug.pro_posts.MyPostsFrag;
import com.app.pug.pug.pro_posts.ProfileGetPosts;
import com.app.pug.pug.pro_replies.MyRepliesAdapter;
import com.app.pug.pug.pro_replies.MyRepliesFrag;

import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleImageView extends Fragment {
    static int post;
    static  String fromD;


    public SingleImageView() {
    }

    public static SingleImageView newInstance(int pos, String from) {
        SingleImageView fragment = new SingleImageView();
        post=pos;
        fromD=from;

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_single_image_view, container, false);
        ImageView imageView= (ImageView) view.findViewById(R.id.img);
        TextView ind= (TextView) view.findViewById(R.id.indi_main_text);
        final TextView likes= (TextView) view.findViewById(R.id.indi_likes);
        final ImageView like= (ImageView) view.findViewById(R.id.indi_up);
        final ImageView down= (ImageView) view.findViewById(R.id.indi_down);
        ImageView back= (ImageView) view.findViewById(R.id.backbut);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        if (fromD.equals("local"))
        {
            try {
                if (!GetFeed.posts.get(post).isNull("img"))
                {
                    String data = GetFeed.posts.get(post).getString("img");
                    byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                    ind.setText(GetFeed.posts.get(post).getString("text"));
                    likes.setText(GetFeed.posts.get(post).getString("likes"));


                    if (IndividualItemsAdapter.detect.containsKey(post)) {
                        if (IndividualItemsAdapter.detect.get(post) == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        } else if (IndividualItemsAdapter.detect.get(post) == -1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        }
                    } else {
                        if (Yammers.integerHashMap.get(post) == 0) {
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Yammers.integerHashMap.get(post) == 0) {
                                        Yammers.integerHashMap.set(post, 1);
                                        IndividualItemsAdapter.detect.put(post, 1);
                                        if (IndividualItemsAdapter.detect.get(post) == 1) {
                                            try {
                                                int oldLike = GetFeed.posts.get(post).getInt("likes");
                                                int newlike = oldLike + 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Yammers.adapter.notifyItemChanged(post);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), GetFeed.posts.get(post));
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                    } else {
                                        like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);

                                }
                            });
                            down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Yammers.integerHashMap.get(post) == 0) {
                                        Yammers.integerHashMap.set(post, -1);
                                        IndividualItemsAdapter.detect.put(post, -1);
                                        if (IndividualItemsAdapter.detect.get(post) == -1) {
                                            try {
                                                int oldLike = GetFeed.posts.get(post).getInt("likes");
                                                int newlike = oldLike - 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Yammers.adapter.notifyItemChanged(post);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), GetFeed.posts.get(post));
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                    } else {
                                        down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);
                                }
                            });
                        } else {
                            if (Yammers.integerHashMap.get(post) == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                }
                                like.setEnabled(false);
                                down.setEnabled(false);
                            } else if (Yammers.integerHashMap.get(post) == -1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                }
                                like.setEnabled(false);
                                down.setEnabled(false);
                            } else if (Yammers.integerHashMap.get(post) == 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    down.setImageDrawable(getActivity().getDrawable(R.drawable.gray_down_vote_new));
                                    like.setImageDrawable(getActivity().getDrawable(R.drawable.gray_up_vote_new));
                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.gray_up_vote_new));
                                    down.setImageDrawable(getResources().getDrawable(R.drawable.gray_down_vote_new));

                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (fromD.equals("global"))
        {
            try {
                if (!GetglobalFeed.postsGlobal.get(post).isNull("img")) {
                    String data = GetglobalFeed.postsGlobal.get(post).getString("img");
                    byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                    ind.setText(GetglobalFeed.postsGlobal.get(post).getString("text"));
                    likes.setText(GetglobalFeed.postsGlobal.get(post).getString("likes"));
                    if (GlobalAdapetr.detect.containsKey(post)) {
                        if (GlobalAdapetr.detect.get(post) == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        } else if (GlobalAdapetr.detect.get(post) == -1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                            } else {
                                down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        }
                    } else {
                        if (GlobalFeedFragment.integerHashMap.get(post) == 0) {
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (GlobalFeedFragment.integerHashMap.get(post) == 0) {
                                        GlobalFeedFragment.integerHashMap.set(post, 1);
                                        GlobalAdapetr.detect.put(post, 1);
                                        if (GlobalAdapetr.detect.get(post) == 1) {
                                            try {
                                                int oldLike = GetglobalFeed.postsGlobal.get(post).getInt("likes");
                                                int newlike = oldLike + 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        GlobalFeedFragment.adapter.notifyItemChanged(post);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), GetglobalFeed.postsGlobal.get(post));
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                    } else {
                                        like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);

                                }
                            });
                            down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (GlobalFeedFragment.integerHashMap.get(post) == 0) {
                                        GlobalFeedFragment.integerHashMap.set(post, -1);
                                        GlobalAdapetr.detect.put(post, -1);
                                        if (GlobalAdapetr.detect.get(post) == -1) {
                                            try {
                                                int oldLike = GetglobalFeed.postsGlobal.get(post).getInt("likes");
                                                int newlike = oldLike - 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        GlobalFeedFragment.adapter.notifyItemChanged(post);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), GetglobalFeed.postsGlobal.get(post));
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                    } else {
                                        down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);
                                }
                            });
                        } else if (GlobalFeedFragment.integerHashMap.get(post) == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        } else if (GlobalFeedFragment.integerHashMap.get(post) == -1) {
                            Log.e("post", "image");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                            } else {
                                down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        }


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (fromD.equals("my_posts"))
        {
            try {
                if (!ProfileGetPosts.posts.get(post).isNull("img")) {
                    String data = ProfileGetPosts.posts.get(post).getString("img");
                    byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);

                    ind.setText(ProfileGetPosts.posts.get(post).getString("text"));
                    likes.setText(ProfileGetPosts.posts.get(post).getString("likes"));
                    if (MyPostsAdapter.detect.containsKey(post)) {
                        if (MyPostsAdapter.detect.get(post) == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        } else if (MyPostsAdapter.detect.get(post) == -1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                            } else {
                                down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        }
                    } else {
                        if (MyPostsFrag.integerHashMap.get(post) == 0) {
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (MyPostsFrag.integerHashMap.get(post) == 0) {
                                        MyPostsFrag.integerHashMap.set(post, 1);
                                        MyPostsAdapter.detect.put(post, 1);
                                        if (MyPostsAdapter.detect.get(post) == 1) {
                                            try {
                                                int oldLike = ProfileGetPosts.posts.get(post).getInt("likes");
                                                int newlike = oldLike + 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        MyPostsFrag.adapter.notifyItemChanged(post);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), ProfileGetPosts.posts.get(post));
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                    } else {
                                        like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);

                                }
                            });
                            down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (MyPostsFrag.integerHashMap.get(post) == 0) {
                                        MyPostsFrag.integerHashMap.set(post, -1);
                                        MyPostsAdapter.detect.put(post, -1);
                                        if (MyPostsAdapter.detect.get(post) == -1) {
                                            try {
                                                int oldLike = ProfileGetPosts.posts.get(post).getInt("likes");
                                                int newlike = oldLike - 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        MyPostsFrag.adapter.notifyItemChanged(post);

                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), ProfileGetPosts.posts.get(post));
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                    } else {
                                        down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);
                                }
                            });
                        } else {
                            if (MyPostsFrag.integerHashMap.get(post) == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                }
                                like.setEnabled(false);
                                down.setEnabled(false);
                            } else if (MyPostsFrag.integerHashMap.get(post) == -1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                } else {
                                    down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                }
                                like.setEnabled(false);
                                down.setEnabled(false);
                            } else if (MyPostsFrag.integerHashMap.get(post) == 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    down.setImageDrawable(getActivity().getDrawable(R.drawable.gray_down_vote_new));
                                    like.setImageDrawable(getActivity().getDrawable(R.drawable.gray_up_vote_new));

                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.gray_up_vote_new));
                                    down.setImageDrawable(getResources().getDrawable(R.drawable.gray_down_vote_new));

                                }
                                like.setEnabled(true);
                                down.setEnabled(true);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (fromD.equals("my_replies"))
        {
            try {
                if (!ProfileGetPosts.posts.get(post).isNull("img")) {
                    String data = ProfileGetPosts.posts.get(post).getString("img");
                    byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                    ind.setText(ProfileGetPosts.posts.get(post).getString("text"));
                    likes.setText(ProfileGetPosts.posts.get(post).getString("likes"));
                    if (MyPostsAdapter.detect.containsKey(post)) {
                        if (MyPostsAdapter.detect.get(post) == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        } else if (MyPostsAdapter.detect.get(post) == -1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                            } else {
                                down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                            }
                            like.setEnabled(false);
                            down.setEnabled(false);
                        }
                    } else {
                        if (MyRepliesFrag.integerHashMap.get(post) == 0) {
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (MyRepliesFrag.integerHashMap.get(post) == 0) {
                                        MyRepliesFrag.integerHashMap.set(post, 1);
                                        MyRepliesAdapter.detect.put(post, 1);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), ProfileGetPosts.posts.get(post));
                                        if (MyRepliesAdapter.detect.get(post) == 1) {
                                            try {
                                                int oldLike = ProfileGetPosts.posts.get(post).getInt("likes");
                                                int newlike = oldLike + 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        MyRepliesFrag.adapter.notifyItemChanged(post);
                                        likeAction.setLikeAction(1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                    } else {
                                        like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);

                                }
                            });
                            down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (MyRepliesFrag.integerHashMap.get(post) == 0) {
                                        MyRepliesFrag.integerHashMap.set(post, -1);
                                        MyRepliesAdapter.detect.put(post, -1);
                                        if (MyRepliesAdapter.detect.get(post) == -1) {
                                            try {
                                                int oldLike = ProfileGetPosts.posts.get(post).getInt("likes");
                                                int newlike = oldLike - 1;
                                                likes.setText("" + newlike);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        MyRepliesFrag.adapter.notifyItemChanged(post);
                                        LikeAction likeAction = new LikeAction((AppCompatActivity) getActivity(), ProfileGetPosts.posts.get(post));
                                        likeAction.setLikeAction(-1);
                                        likeAction.execute();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                    } else {
                                        down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                    }
                                    like.setEnabled(false);
                                    down.setEnabled(false);
                                }
                            });
                        } else {
                            if (MyRepliesFrag.integerHashMap.get(post) == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    like.setImageDrawable(getActivity().getDrawable(R.drawable.up_vote_new));
                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.up_vote_new));
                                }
                                like.setEnabled(false);
                                down.setEnabled(false);
                            } else if (MyRepliesFrag.integerHashMap.get(post) == -1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    down.setImageDrawable(getActivity().getDrawable(R.drawable.down_vote_new));
                                } else {
                                    down.setImageDrawable(getResources().getDrawable(R.drawable.down_vote_new));
                                }
                                like.setEnabled(false);
                                down.setEnabled(false);
                            } else if (MyRepliesFrag.integerHashMap.get(post) == 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    down.setImageDrawable(getActivity().getDrawable(R.drawable.gray_down_vote_new));
                                    like.setImageDrawable(getActivity().getDrawable(R.drawable.gray_up_vote_new));

                                } else {
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.gray_up_vote_new));
                                    down.setImageDrawable(getResources().getDrawable(R.drawable.gray_down_vote_new));

                                }
                                like.setEnabled(true);
                                down.setEnabled(true);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

}
