package com.vodafone.frt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.models.PTRResponseTrackPatrollerModel;

import java.util.List;

/**
 * Created by qss on 12/1/18
 */

public class PTRTrackPatrollerRouteAdapter extends BaseAdapter {
    private final Context context;
    private PTRTrackPatrollerRouteAdapter.SelectionViewAttendanceListener listener;
    private List<PTRResponseTrackPatrollerModel> dataSet;

    public PTRTrackPatrollerRouteAdapter(Context context) {
        this.context = context;

    }

    public void setSelectionListener(PTRTrackPatrollerRouteAdapter.SelectionViewAttendanceListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<PTRResponseTrackPatrollerModel> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PTRTrackPatrollerRouteAdapter.ViewHolder holder;
        PTRResponseTrackPatrollerModel trackPatrollerModel = dataSet.get(position);
        if (convertView == null) {
            holder = new PTRTrackPatrollerRouteAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_track_patroller, parent, false);
            holder.tv_userName = convertView.findViewById(R.id.tv_userName);
            holder.tv_fullName = convertView.findViewById(R.id.tv_fullName);

            holder.img_map = convertView.findViewById(R.id.img_map);
            convertView.setTag(holder);
        } else {
            holder = (PTRTrackPatrollerRouteAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_userName.setText(trackPatrollerModel.getUser_name());
        holder.tv_fullName.setText(trackPatrollerModel.getFull_name());
        holder.img_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickItem(position);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView tv_userName, tv_fullName;
        ImageView img_map;
    }

    public interface SelectionViewAttendanceListener {
        void onClickItem(int position);
    }
}
