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

import java.util.ArrayList;
import java.util.List;

import justbucket.videolib.fragment.GridFragment;

public class SecondActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAdapter mAdapter;

    public static final Intent newIntent(Context context) {
        return new Intent(context, SecondActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        List<String> tags = new ArrayList<>();
        tags.add("one");
        tags.add("two");
        tags.add("three");
        mAdapter = new TabsAdapter(getSupportFragmentManager(), tags);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class TabsAdapter extends FragmentStatePagerAdapter {

        private List<String> titles;
        private List<Fragment> mFragments = new ArrayList<>();

        public TabsAdapter(FragmentManager fm, List<String> tags) {
            super(fm);
            titles = tags;
            for (String tag : tags) {
                GridFragment fragment = GridFragment.newInstance(tag);
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
