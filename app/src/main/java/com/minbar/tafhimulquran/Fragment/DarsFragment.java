package com.minbar.tafhimulquran.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minbar.tafhimulquran.Adapter.DarsAdapter;
import com.minbar.tafhimulquran.Model.DarsModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.ArrayList;
import java.util.List;


public class DarsFragment extends Fragment {


    DarsAdapter adapter;
    SqlLiteDbHelper dbHelper;
    RecyclerView recyclerView;
    List<DarsModel> surahModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dars, container, false);



        this.dbHelper = new SqlLiteDbHelper(getActivity());
        RecyclerView recyclerView2 = (RecyclerView) v.findViewById(R.id.recyclerDars);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.surahModelList = new ArrayList();
        this.adapter = new DarsAdapter(getActivity(), this.dbHelper.getDars());
        this.recyclerView.setAdapter(this.adapter);



        return v;
    }
}