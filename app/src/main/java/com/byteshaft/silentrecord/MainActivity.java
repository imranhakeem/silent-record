package com.byteshaft.silentrecord;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.byteshaft.silentrecord.fragments.AboutFragment;
import com.byteshaft.silentrecord.fragments.ContactFragment;
import com.byteshaft.silentrecord.fragments.ImagesActivity;
import com.byteshaft.silentrecord.fragments.ReportFragment;
import com.byteshaft.silentrecord.fragments.ScheduleActivity;
import com.byteshaft.silentrecord.fragments.SettingFragment;
import com.byteshaft.silentrecord.fragments.VideoFragment;
import com.byteshaft.silentrecord.fragments.VideosActivity;
import com.byteshaft.silentrecord.utils.CameraCharacteristics;
import com.byteshaft.silentrecord.utils.Helpers;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends AppCompatActivity implements MaterialTabListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mListTitles;
    private ListView mDrawerList;
    private ViewPager mViewPager;
    private MaterialTabHost mMaterialTabHost;
    private Resources mResources;
    private Fragment mFragment;
    private NotificationCompat.Builder mBuilder;
    private int mNotificationID = 001;
    private NotificationManager mNotifyManager;
    boolean isMainActivityActive = false;
//    int mPosition = -1;

    @Override
    protected void onPause() {
        super.onPause();
        isMainActivityActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMainActivityActive = true;
        Helpers helpers = new Helpers(getApplicationContext());
        if (helpers.isAppRunningForTheFirstTime()) {
            Camera camera = helpers.openCamera();
            if (camera != null) {
                new CameraCharacteristics(getApplicationContext(), camera);
                helpers.setIsAppRunningForTheFirstTime(false);
                camera.release();
            } else {
                helpers.showCameraResourceBusyDialog(this);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (!isMainActivityActive) {
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        } else {
            onStop();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        setContentView(R.layout.activity_main);
        Helpers.createDirectoryIfNotExists(AppGlobals.DIRECTORY.VIDEOS);
        Helpers.createDirectoryIfNotExists(AppGlobals.DIRECTORY.PICTURES);
        mMaterialTabHost = (MaterialTabHost) findViewById(R.id.tab_host);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mResources = getResources();
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mMaterialTabHost.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mViewPagerAdapter.getCount(); i++) {
            mMaterialTabHost.addTab(mMaterialTabHost.newTab().setIcon(getIcon(i)).setTabListener(this));
        }
        mDrawerToggle = getActionBarDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.list_drawer);
        mListTitles = new String[] {
                getString(R.string.title_section1),
                getString(R.string.title_section2),
                getString(R.string.title_section3),
                getString(R.string.title_section4),
                getString(R.string.title_section5),
                getString(R.string.title_section6),
        };
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void selectItem(int position) {
        isMainActivityActive = false;
        mDrawerList.setItemChecked(position, true);
        setTitle(mListTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    void newFragment(int position) {

        switch (position) {
            case 0:
                mFragment = new VideoFragment(AppGlobals.DIRECTORY.VIDEOS);
                break;
            case 1:
                mFragment = new VideoFragment(AppGlobals.DIRECTORY.PICTURES);
                break;
            case 2:
                mFragment = new SettingFragment();
                break;
            case 3:
                mFragment = new AboutFragment();
                break;
            case 4:
                mFragment = new ReportFragment();
                break;
            case 5:
                mFragment = new ContactFragment();
                break;
            default:
                return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();
        fragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
        fragmentManager.popBackStack();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle getActionBarDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
//                newFragment(mPosition);
            }
        };
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        mViewPager.setCurrentItem(materialTab.getPosition());

    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            newFragment(position);
            mMaterialTabHost.setVisibility(View.GONE);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            mFragment = new Fragment();

            switch (num) {
                case 0:
                    mFragment = new VideosActivity();
                    break;
                case 1:
                    mFragment = new ImagesActivity();
                    break;
                case 2:
                    mFragment = new ScheduleActivity();
                    break;
            }
            return mFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private Drawable getIcon(int position) {
        switch (position) {
            case 0:
                return mResources.getDrawable(R.drawable.videos);
            case 1:
                return mResources.getDrawable(R.drawable.images);
            case 2:
                return mResources.getDrawable(R.drawable.schedule);
            default:
                return null;
        }
    }
}
