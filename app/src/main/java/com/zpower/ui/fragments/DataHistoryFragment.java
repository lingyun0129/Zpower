package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zpower.R;
import com.zpower.adapter.HistoryDataAdapter;
import com.zpower.model.HistoryData;
import com.zpower.utils.DBHelper;

import java.util.List;

/**
 * Created by zx on 2017/3/7.
 */

public class DataHistoryFragment extends BaseFragment {

    private View rootView;
    private List<HistoryData> data;

    public static DataHistoryFragment newInstance(){
        return new DataHistoryFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history_data,container,false);
        initData();
        initView();
        return rootView;
    }

    private void initData() {
        DBHelper dbHelper=new DBHelper(getActivity());
        data=dbHelper.queryHistoryData();
  /*      for (int i = 0 ; i <20 ; i++){
            HistoryData historyData = new HistoryData();
            historyData.setDate("02月17日");
            historyData.setTime("03:30:12");
            historyData.setWatt("320");
            data.add(historyData);
        } */
    }

    private void initView() {
        ListView lv_history_data = (ListView) rootView.findViewById(R.id.lv_history_data);
        HistoryDataAdapter historyDataAdapter = new HistoryDataAdapter(getActivity(),data);
        lv_history_data.setAdapter(historyDataAdapter);
    }
}
