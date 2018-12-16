package justbucket.videolib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import justbucket.videolib.fragment.BridgeFragment;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG_KEY = "tag-key";
    List<String> tags;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAdapter mAdapter;

    public static final Intent newIntent(Context context, List<String> strings) {
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putStringArrayListExtra(TAG_KEY, (ArrayList<String>) strings);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(TAG_KEY, (ArrayList<String>) tags);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tags = new ArrayList<>();
        Intent startIntent = getIntent();
        if (startIntent != null) {
            tags = startIntent.getStringArrayListExtra(TAG_KEY);
        } else if (savedInstanceState != null) {
            tags = savedInstanceState.getStringArrayList(TAG_KEY);
        }
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        mAdapter = new TabsAdapter(getSupportFragmentManager(), tags);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuCallback menuCallback = (MenuCallback) getSupportFragmentManager().getFragments().get(mViewPager.getCurrentItem()).getChildFragmentManager().getFragments().get(0);
        menuCallback.onMenuItemClicked(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.getChildFragmentManager().getBackStackEntryCount() > 1) {
                fragment.getChildFragmentManager().popBackStack();
                return;
            }
        }
        super.onBackPressed();
    }

    private class TabsAdapter extends FragmentStatePagerAdapter {

        private List<String> titles;
        private List<Fragment> mFragments = new ArrayList<>();

        public TabsAdapter(FragmentManager fm, List<String> tags) {
            super(fm);
            titles = tags;
            for (String tag : tags) {
                mFragments.add(BridgeFragment.newInstance(tag));
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }


    }

    public interface MenuCallback {
         void onMenuItemClicked(@MenuRes int id);
    }

}
