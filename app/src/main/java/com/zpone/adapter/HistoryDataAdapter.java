package com.zpone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zpone.R;
import com.zpone.model.HistoryData;

import java.util.List;

/**
 * Created by zx on 2017/3/7.
 */

public class HistoryDataAdapter extends BaseAdapter {
    private Context mContext;
    private List<HistoryData> data;

    public HistoryDataAdapter(Context mContext, List<HistoryData> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history_data,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv_date.setText(data.get(position).getDate());
        holder.tv_time.setText(data.get(position).getTime());
        holder.tv_watt.setText(data.get(position).getWatt());

        return convertView;
    }

    class ViewHolder{
        TextView tv_date;
        TextView tv_time;
        TextView tv_watt;

        public ViewHolder(View view) {
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_watt = (TextView) view.findViewById(R.id.tv_watt);
        }
    }
}
