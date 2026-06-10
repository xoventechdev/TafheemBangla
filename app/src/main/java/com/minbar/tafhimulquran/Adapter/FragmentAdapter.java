package com.minbar.tafhimulquran.Adapter;


import static com.minbar.tafhimulquran.Activity.SingleActivity.p;
import static com.minbar.tafhimulquran.Activity.SingleActivity.surahid;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.minbar.tafhimulquran.Fragment.VerseFragment;

public class FragmentAdapter extends FragmentStateAdapter {


    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new VerseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VerseFragment.surahidF, String.valueOf(surahid));

        // Pass the actual verse ID from the 'p' array instead of the position
        if (p != null && position < p.length) {
            bundle.putString(VerseFragment.verseidF, p[position]);
        } else {
            bundle.putString(VerseFragment.verseidF, String.valueOf(position));
        }
        
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return p != null ? p.length : 0;
    }

}
