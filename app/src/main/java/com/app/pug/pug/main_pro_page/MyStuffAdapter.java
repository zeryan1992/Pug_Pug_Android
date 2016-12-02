package com.app.pug.pug.main_pro_page;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.pug.pug.PrivacyPolicy;
import com.app.pug.pug.R;
import com.app.pug.pug.pro_posts.MyPostsFrag;
import com.app.pug.pug.pro_replies.MyRepliesFrag;
import com.app.pug.pug.utils.TermsOfSe;

import java.util.ArrayList;

/**
 * Created by zeryan on 3/4/16.
 */
public class MyStuffAdapter extends RecyclerView.Adapter<MyStuffAdapter.ViewHolder> {
    ArrayList<String> items = new ArrayList<>();
    AppCompatActivity activity;

    public MyStuffAdapter(AppCompatActivity activity) {
        this.activity = activity;
        items.add(0, "Your Posts");
        items.add(1, "Your Replies");
        items.add(2, "Privacy Policy");
        items.add(3, "Terms of Services");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_stuff_one_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.items.setText(items.get(position));
        holder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out,R.anim.push_right_in,R.anim.push_right_out);
                    ft.replace(R.id.replace, new MyPostsFrag());
                    ft.addToBackStack(null);
                    ft.commit();

                }
                else if (position==1)
                {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out,R.anim.push_right_in,R.anim.push_right_out);
                    ft.replace(R.id.replace, new MyRepliesFrag());
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else if (position==2)
                {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out,R.anim.push_right_in,R.anim.push_right_out);
                    ft.replace(R.id.replace, new PrivacyPolicy());
                    ft.addToBackStack(null);
                    ft.commit();

                }
                else if (position==3)
                {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out,R.anim.push_right_in,R.anim.push_right_out);
                    ft.replace(R.id.replace, new TermsOfSe());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView items;

        public ViewHolder(View itemView) {
            super(itemView);
            items = (TextView) itemView.findViewById(R.id.my_stuff_texts);
        }


    }
}
