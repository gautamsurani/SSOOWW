package com.serviceonwheel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.serviceonwheel.fragment.BookingHistoryFragment;
import com.serviceonwheel.fragment.BookingOngoingFragment;


public class BookingTabAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public BookingTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new BookingOngoingFragment();
            case 1:
                return new BookingHistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
