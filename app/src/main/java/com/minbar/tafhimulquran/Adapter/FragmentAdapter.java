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

        bundle.putString(VerseFragment.verseidF, String.valueOf(position));
        //bundle.putString(VerseFragment.contentAr, jcontent);
        //bundle.putString(VerseFragment.contentTr, transTxt);
        //bundle.putString(VerseFragment.contentBn, banglaTxt);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return p.length;
    }

}
