package com.vodafone.frt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.models.PTRResponseScheduleRouteModel;

import java.util.List;

/**
 * Created by Ashutosh Kumar on 21-Jan-18.
 */


public class PTRUsersListAdapter extends BaseAdapter {
    private final Context context;
    private SelectionViewAttendanceListener listener;
    private List<PTRResponseScheduleRouteModel> dataSet;

    public PTRUsersListAdapter(Context context) {
        this.context = context;

    }

    public void setSelectionListener(SelectionViewAttendanceListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<PTRResponseScheduleRouteModel> dataSet) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        PTRResponseScheduleRouteModel scheduledroute = dataSet.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_users_list, parent, false);
            holder.assignment_type = convertView.findViewById(R.id.assignment_type);
            /*holder.route_name = convertView.findViewById(R.id.route_name);*/
           /* holder.duration = convertView.findViewById(R.id.duration);
            holder.timing = convertView.findViewById(R.id.timing);
            holder.working_days = convertView.findViewById(R.id.working_days);
            holder.img_map = convertView.findViewById(R.id.img_map);
            holder.working_days_title = convertView.findViewById(R.id.working_days_title);
            holder.working_days_layout = convertView.findViewById(R.id.working_days_layout);*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
       /* holder.route_name.setText(scheduledroute.getRoute_name());*/
        holder.assignment_type.setText(/*scheduledroute.getAssignment_type()*/"User 1");
       /* if ((!scheduledroute.getStart_date().equalsIgnoreCase("null") && scheduledroute.getStart_date() != null)
                || (!scheduledroute.getEnd_date().equalsIgnoreCase("null") && scheduledroute.getEnd_date() != null))
            holder.duration.setText(scheduledroute.getStart_date() + " To " + scheduledroute.getEnd_date());
        else
            holder.duration.setText("");
        if ((!scheduledroute.getStart_time().equalsIgnoreCase("null") && scheduledroute.getStart_time() != null)
                || (!scheduledroute.getEnd_time().equalsIgnoreCase("null") && scheduledroute.getEnd_date() != null))
            holder.timing.setText(scheduledroute.getStart_time() + " To " + scheduledroute.getEnd_time());
        else
            holder.timing.setText("");

        //todo: check key and image
        if (scheduledroute.getAssignment_type().equalsIgnoreCase("Weekly") || scheduledroute.getAssignment_type().equalsIgnoreCase("Daily")) {
            holder.working_days_layout.setVisibility(View.VISIBLE);
            holder.working_days_title.setText("Working Days");
            holder.working_days.setText(String.valueOf(scheduledroute.getWorking_days()));
        } else if (scheduledroute.getAssignment_type().equalsIgnoreCase("Monthly")) {
            holder.working_days_layout.setVisibility(View.VISIBLE);
            holder.working_days_title.setText("Day Of Month");
            holder.working_days.setText(String.valueOf(scheduledroute.getDay_of_month()));
        } else {
            holder.working_days_layout.setVisibility(View.GONE);
        }*/

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickItem(position);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView assignment_type, route_name/*, duration, timing, working_days, working_days_title*/;
        /*ImageView img_map;
        LinearLayout working_days_layout;*/
    }

    public interface SelectionViewAttendanceListener {
        void onClickItem(int position);
    }
}