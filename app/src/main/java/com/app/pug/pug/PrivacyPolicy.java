package com.app.pug.pug;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;



/**
 * A simple {@link Fragment} subclass.
 */
public class PrivacyPolicy extends Fragment {


    public PrivacyPolicy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        WebView web= (WebView) view.findViewById(R.id.privacy);
        web.loadUrl("https://www.iubenda.com/privacy-policy/7814558/full-legal");
        ImageView back= (ImageView) view.findViewById(R.id.backbut);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;

    }

}
