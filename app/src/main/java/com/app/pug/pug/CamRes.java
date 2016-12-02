package com.app.pug.pug;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.pug.pug.activities.ActivityMainDataSet;
import com.app.pug.pug.send_stuff.SendPostThread;
import com.app.pug.pug.utils.LegaStuff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class CamRes extends Fragment {
    Uri img;
    ImageView imageView;
    ProgressDialog loading;
    Bitmap bitmap;
    public static int width;




    public CamRes() {
        // Required empty public constructor
    }


    public static CamRes newInstance() {
        CamRes fragment = new CamRes();

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LegaStuff get=new LegaStuff(getContext());
        img= Uri.parse(get.getUri());
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width=displayMetrics.widthPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.cam_res, container, false);
        loading=new ProgressDialog(getActivity());
        imageView= (ImageView) view.findViewById(R.id.img);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),img);
            imageView.setImageBitmap(bitmap);
            final ImageView upload= (ImageView) view.findViewById(R.id.upload);
            loading.setMessage("Posting...");
            final EditText edits= (EditText) view.findViewById(R.id.img_text);
            edits.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length()>2) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            upload.setImageDrawable(getActivity().getDrawable(R.drawable.green_ic_cloud_upload_24dp));
                        }
                        else
                        {
                            upload.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.green_ic_cloud_upload_24dp));

                        }
                        upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!edits.getText().equals("")) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
                                    byte[] bytes = stream.toByteArray();
                                    final String dec = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);
                                    ActivityMainDataSet.handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            SendPostThread send = new SendPostThread(edits, (AppCompatActivity) getActivity(), true, null, loading, true, dec);
                                            send.execute();
                                            InputMethodManager in= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            in.hideSoftInputFromWindow(edits.getWindowToken(),0);
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        }
                                    });
                                }


                            }
                        });
                    }

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }


}
