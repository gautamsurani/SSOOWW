package com.serviceonwheel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.serviceonwheel.fragment.LoginFragment;
import com.serviceonwheel.fragment.SignupFragment;

/*
 * Created by welcome on 28-11-2017.
 */

public class LoginTabAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public LoginTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new LoginFragment();
            case 1:
                return new SignupFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
