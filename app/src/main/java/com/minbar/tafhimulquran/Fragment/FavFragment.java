package com.minbar.tafhimulquran.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.minbar.tafhimulquran.Adapter.FavVerseAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;
import java.util.ArrayList;

public class FavFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavVerseAdapter adapter;
    private SqlLiteDbHelper dbHelper;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fav, container, false);

        recyclerView = v.findViewById(R.id.recycler_fav);
        emptyView = v.findViewById(R.id.tv_empty_fav);
        dbHelper = new SqlLiteDbHelper(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        checkList();

        return v;
    }

    public void checkList() {
        XovenHandler xovenHandler = new XovenHandler(getActivity());
        ArrayList<String> favList = xovenHandler.getAllFav();
        
        if (favList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < favList.size(); i++) {
                sb.append(favList.get(i));
                if (i < favList.size() - 1) sb.append(",");
            }
            
            adapter = new FavVerseAdapter(getActivity(), dbHelper.getFav(sb.toString()));
            recyclerView.setAdapter(adapter);
        }
    }
}
