package com.minbar.tafhimulquran.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.minbar.tafhimulquran.Fragment.DarsFragment;
import com.minbar.tafhimulquran.Fragment.OthersFragment;
import com.minbar.tafhimulquran.Fragment.HomeFragment;
import com.minbar.tafhimulquran.Fragment.SettingFragment;
import com.minbar.tafhimulquran.Fragment.SubjectFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 2:
                return new HomeFragment();
            case 3:
                return new OthersFragment();
            case 1:
                return new DarsFragment();
            case 4:
                return new SettingFragment();
            case 0:
                return new SubjectFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

}