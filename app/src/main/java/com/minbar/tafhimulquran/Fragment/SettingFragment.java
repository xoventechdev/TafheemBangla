package com.minbar.tafhimulquran.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minbar.tafhimulquran.Adapter.MapsAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;


public class SettingFragment extends Fragment  {



    MapsAdapter adapter;
    SqlLiteDbHelper dbHelper;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);





        this.dbHelper = new SqlLiteDbHelper(getActivity());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView2 = (RecyclerView) v.findViewById(R.id.recyclerMaps);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.adapter = new MapsAdapter(getActivity(), this.dbHelper.getMaps());
        this.recyclerView.setAdapter(this.adapter);













        return v;
    }


}