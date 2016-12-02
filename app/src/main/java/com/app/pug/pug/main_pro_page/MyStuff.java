package com.app.pug.pug.main_pro_page;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.pug.pug.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyStuff extends Fragment {
    public MyStuff() {
    }

    public static MyStuff newInstance() {

        Bundle args = new Bundle();

        MyStuff fragment = new MyStuff();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.profile_personal, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.my_stuff_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        MyStuffAdapter adapter = new MyStuffAdapter((AppCompatActivity) getActivity());
        recyclerView.setAdapter(adapter);

        return root;
    }


}
