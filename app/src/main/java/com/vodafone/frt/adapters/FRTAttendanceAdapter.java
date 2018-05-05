package com.vodafone.frt.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.models.FRTResponseAttendanceModel;
import com.vodafone.frt.models.PTRResponseAttendanceModel;

import java.util.List;

/**
 * Created by ajay on 10/4/18
 */

public class FRTAttendanceAdapter extends BaseAdapter {
    private final Context context;
    private SelectionViewAttendanceListener listener;
    private List<FRTResponseAttendanceModel> dataSet;

    public FRTAttendanceAdapter(Context context) {
        this.context = context;
    }

    public void setSelectionListener(SelectionViewAttendanceListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        if (dataSet.size() >0) {
            return dataSet.size();
        }
        else return 0;
    }

    public void setDataSet(List<FRTResponseAttendanceModel> dataSet) {
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
        ViewHolder holder;
        FRTResponseAttendanceModel atendance = dataSet.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_frt_attendance_list, parent, false);
            holder.attendance_date = convertView.findViewById(R.id.attendance_date);
            holder.issueTypeTextView = convertView.findViewById(R.id.issueTypeTextView);
            holder.checkInTimeTextView = convertView.findViewById(R.id.checkInTimeTextView);
            holder.checkOutTimeTextView = convertView.findViewById(R.id.checkOutTimeTextView);
            holder.img_map = convertView.findViewById(R.id.img_map);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.attendance_date.setText(!atendance.getAttendance_date().equalsIgnoreCase("null") && atendance.getAttendance_date() != null ? atendance.getAttendance_date() : "");
        if (atendance.getIssue_desc() != null && !atendance.getIssue_desc().equalsIgnoreCase("null")){
            holder.issueTypeTextView.setText(atendance.getIssue_desc());
        }

        if (atendance.getCheckin_time() != null && !atendance.getCheckin_time().equalsIgnoreCase("null")){
            holder.checkInTimeTextView.setText(atendance.getCheckin_time());
        }
        else {
            holder.checkInTimeTextView.setText("");
        }

        if (atendance.getCheckout_time() != null && !atendance.getCheckout_time().equalsIgnoreCase("null")){
            holder.checkOutTimeTextView.setText(atendance.getCheckout_time());
        }
        else {
            holder.checkOutTimeTextView.setText("");
        }



        holder.img_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickItem(position);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(position);
            }
        });
        holder.checkInTimeTextView.setTag(position);
        holder.checkOutTimeTextView.setTag(position);
        return convertView;
    }

    private class ViewHolder {
        TextView attendance_date, issueTypeTextView, checkInTimeTextView, checkOutTimeTextView;
        ImageView img_map;
    }

    public interface SelectionViewAttendanceListener {
        void onClickItem(int position);
    }
}