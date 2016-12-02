package com.app.pug.pug.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.pug.pug.CamRes;
import com.app.pug.pug.R;
import com.app.pug.pug.global_stuff.GlobalFeedFragment;
import com.app.pug.pug.local_feed.Yammers;
import com.app.pug.pug.main_pro_page.MyStuff;
import com.app.pug.pug.public_map_view.PublicMaps;
import com.app.pug.pug.utils.LegaStuff;
import com.app.pug.pug.utils.PagerSlidingTabStrip;

import java.util.Date;

public class ActivityMainDataSet extends AppCompatActivity {


    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;
    public static Handler handler=new Handler();
    PowerManager.WakeLock wakeLock;
    Runnable runnable;
    public static String url_address = "https://pug.us-central-pug.com/pug";
    ImageView camera;
    Animation animation;
    Animation in;
    int REQUEST_CODE=1;
    ContentValues contentValues;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_data_set);
        camera= (ImageView) findViewById(R.id.camera);
        animation= AnimationUtils.loadAnimation(this,R.anim.fade_out);
        in=AnimationUtils.loadAnimation(this,R.anim.fade_in);
        //camera.setVisibility(View.VISIBLE);

        PowerManager manager= (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock=manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"TAG");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myPagerAdapter=new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        ViewPagerListener listener=new ViewPagerListener();

        viewPager.setOnPageChangeListener(listener);
        PagerSlidingTabStrip newStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        newStrip.setViewPager(viewPager);
        handler.post(runnable);
        if (camera.getVisibility()==View.VISIBLE)
        {
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(ActivityMainDataSet.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(ActivityMainDataSet.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},1);
                        }
                    }
                    else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        contentValues=new ContentValues();
                        contentValues.put(MediaStore.Images.Media.TITLE,(new Date()).getTime());
                        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Pug Pug");
                        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                        LegaStuff save=new LegaStuff(ActivityMainDataSet.this);
                        save.setimageUri(imageUri.toString());

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_CODE&&resultCode==-1)
        {
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
            ft.replace(R.id.replace, CamRes.newInstance());
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0]==0)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            contentValues=new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE,(new Date()).getTime());
            contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Coyote Inc");
            imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            LegaStuff save=new LegaStuff(ActivityMainDataSet.this);
            save.setimageUri(imageUri.toString());

            if (intent.resolveActivity(getPackageManager()) != null)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();


    }
    public class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        int[] icons = {R.drawable.map_pin_marked, R.drawable.earth,R.drawable.ic_action_name2,R.drawable.ic_person_outline_24dp};
        int[] switchIcon = {R.drawable.icon_selector_tab_1, R.drawable.icon_selector_tab2,R.drawable.icon_selector_tab4, R.drawable.icon_selector_tab3};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Yammers.newInstance();
                case 1:
                    return new PublicMaps();
                case 2:
                    return GlobalFeedFragment.newInstance();
                case 3:
                    return MyStuff.newInstance();
                default:
                    return Yammers.newInstance();
            }
        }


        @Override
        public int getCount() {
            return icons.length;
        }


        @Override
        public int getPageIconResId(int position) {
            return switchIcon[position];
        }
    }
    public  class ViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position)
            {
                case 0:
                    if (camera.getVisibility()==View.GONE)
                    {
                        camera.startAnimation(in);
                        camera.setVisibility(View.VISIBLE);
                        break;

                    }
                case 1:
                    if (camera.getVisibility()==View.VISIBLE) {
                        camera.startAnimation(animation);
                        camera.setVisibility(View.GONE);
                        break;

                    }
                case 2:
                    if (camera.getVisibility()==View.VISIBLE) {
                        camera.startAnimation(animation);
                        camera.setVisibility(View.GONE);
                        break;

                    }
                case 3:
                    if (camera.getVisibility()==View.VISIBLE) {
                        camera.startAnimation(animation);
                        camera.setVisibility(View.GONE);
                        break;
                    }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


}

