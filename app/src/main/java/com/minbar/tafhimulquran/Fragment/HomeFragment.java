package com.minbar.tafhimulquran.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Adapter.SurahAdapter;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {



    SurahAdapter adapter;
    SqlLiteDbHelper dbHelper;
    RecyclerView recyclerView;
    List<SurahModel> surahModelList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);



        this.dbHelper = new SqlLiteDbHelper(getActivity());
        RecyclerView recyclerView2 = (RecyclerView) v.findViewById(R.id.recyclerViewid);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.surahModelList = new ArrayList();
        this.adapter = new SurahAdapter(getActivity(), this.dbHelper.getSurah());
        this.recyclerView.setAdapter(this.adapter);



        return v;
    }
}