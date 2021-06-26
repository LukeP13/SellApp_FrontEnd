package org.udg.pds.todoandroid.fragmentPagerAdapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.fragment.ProductListFragment;
import org.udg.pds.todoandroid.fragment.ProfileFragment;
import org.udg.pds.todoandroid.fragment.ProfileMapFragment;
import org.udg.pds.todoandroid.fragment.RateListFragment;
import org.udg.pds.todoandroid.fragment.UserProductListFragment;
import org.udg.pds.todoandroid.fragment.whiteFragment;

public class TabsProfilePagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[] { R.string.tab_products, R.string.tab_rates, R.string.tab_location };

    private final ProfileFragment mContext;
    private User user;

    public TabsProfilePagerAdapter(ProfileFragment context, FragmentManager fm, User _u) {
        super(fm);
        mContext = context;
        user = _u;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //Product
                return new UserProductListFragment();
            case 1: //Coments
                return RateListFragment.newInstance(user);
            case 2: //Location
                return new ProfileMapFragment(user.location);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}
