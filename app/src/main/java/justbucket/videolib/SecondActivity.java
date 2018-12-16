package justbucket.videolib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import justbucket.videolib.fragment.GridFragment;

public class SecondActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAdapter mAdapter;
    private SecondViewModel mSecondViewModel;
    private static final String TAG_KEY = "tag-key";

    public static final Intent newIntent(Context context, List<String> strings) {
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putStringArrayListExtra(TAG_KEY, (ArrayList<String>) strings);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        List<String> tags = new ArrayList<>();
        Intent startIntent = getIntent();
        if(startIntent != null) {
            tags = startIntent.getStringArrayListExtra(TAG_KEY);
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
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_grid, menu);
        return true;
    }

    private class TabsAdapter extends FragmentStatePagerAdapter {

        private List<String> titles;
        private List<Fragment> mFragments = new ArrayList<>();

        public TabsAdapter(FragmentManager fm, List<String> tags) {
            super(fm);
            titles = tags;
            for (String tag : tags) {
                GridFragment fragment = new GridFragment();
                mFragments.add(fragment);

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

}
