package com.minbar.tafhimulquran.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.minbar.tafhimulquran.Activity.DarsListActivity;
import com.minbar.tafhimulquran.Activity.MapsActivity;
import com.minbar.tafhimulquran.Activity.NoteActivity;
import com.minbar.tafhimulquran.Activity.OvidhanActivity;
import com.minbar.tafhimulquran.Activity.PageMainActivity;
import com.minbar.tafhimulquran.Activity.SubjectListActivity;
import com.minbar.tafhimulquran.Activity.TafheemIntroduceActivity;
import com.minbar.tafhimulquran.Activity.TajwidActivity;
import com.minbar.tafhimulquran.Daily.DailyActivity;
import com.minbar.tafhimulquran.Hadith.HadithChapterActivity;
import com.minbar.tafhimulquran.Prayer.PrayerActivity;
import com.minbar.tafhimulquran.R;

public class LibraryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);

        v.findViewById(R.id.btnPrayer).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), PrayerActivity.class)));
        v.findViewById(R.id.btnRiyadus).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), HadithChapterActivity.class)));
        v.findViewById(R.id.btnDaily).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), DailyActivity.class)));
        v.findViewById(R.id.btnChapaQuran).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), PageMainActivity.class)));
        v.findViewById(R.id.btnOvidhan).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), OvidhanActivity.class)));
        v.findViewById(R.id.btnTajwid).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), TajwidActivity.class)));
        v.findViewById(R.id.btnIntro).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), TafheemIntroduceActivity.class)));

        v.findViewById(R.id.btnSubject).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), SubjectListActivity.class)));
        v.findViewById(R.id.btnDars).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), DarsListActivity.class)));
        v.findViewById(R.id.btnMaps).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), MapsActivity.class)));
        v.findViewById(R.id.btnNotes).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), NoteActivity.class)));

        return v;
    }
}
