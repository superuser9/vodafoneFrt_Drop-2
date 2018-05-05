package com.vodafone.frt.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vodafone.frt.R;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.FRTResponseRouteCommonModelFRT;
import com.vodafone.frt.models.FRTResponseTaskDetailForFrtModel;

import java.util.List;

/**
 * Created by Ajay Tiwari on 3/16/2018.
 */

public class FRTRouteAdapterAssignedFRT extends BaseAdapter {

    private final Context context;
    private SelectionViewAttendanceListener listener;
    private List<FRTResponseRouteCommonModelFRT> dataSet;

    public FRTRouteAdapterAssignedFRT(Context mcontext){
        context =mcontext;
    }

    public void setSelectionListener(SelectionViewAttendanceListener listener) {
        this.listener = listener;
    }


    public void setDataSet(List<FRTResponseRouteCommonModelFRT> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int getCount() {
        return dataSet.size();
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
        try {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_routes_assigned_frt_new, parent, false);
                holder.assignment_date = convertView.findViewById(R.id.assignment_date);
                holder.issue_type_name = convertView.findViewById(R.id.issue_type_name);
                holder.managerRemarksTextView = convertView.findViewById(R.id.managerRemarksTextView);
                holder.patrolleRemarksTv = convertView.findViewById(R.id.patrolleRemarksTv);
                holder.patrollerRemarksLayout = convertView.findViewById(R.id.patrollerRemarksLayout);
                holder.managerRemarksLayout = convertView.findViewById(R.id.managerRemarksLayout);
                holder.img_map = convertView.findViewById(R.id.img_map);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (dataSet.get(position).getIssue_type() != null && !dataSet.get(position).getIssue_type().equalsIgnoreCase("null")){
                holder.issue_type_name.setText(dataSet.get(position).getIssue_type());
            }
            if (!dataSet.get(position).getAssigned_date().equalsIgnoreCase("null") && !TextUtils.isEmpty(dataSet.get(position).getAssigned_date()))
                holder.assignment_date.setText(dataSet.get(position).getAssigned_date().split(" ")[0]);
            else
                holder.assignment_date.setText("");

            if (!dataSet.get(position).getPatroller_remark().equalsIgnoreCase("null") && !TextUtils.isEmpty(dataSet.get(position).getPatroller_remark())){
                holder.patrollerRemarksLayout.setVisibility(View.VISIBLE);
                holder.patrolleRemarksTv.setText(dataSet.get(position).getPatroller_remark());
            }
            else {
                holder.patrollerRemarksLayout.setVisibility(View.GONE);
            }
            if (!dataSet.get(position).getManager_remark().equalsIgnoreCase("null") && !TextUtils.isEmpty(dataSet.get(position).getManager_remark())){
                holder.managerRemarksLayout.setVisibility(View.VISIBLE);
                holder.managerRemarksTextView.setText(dataSet.get(position).getManager_remark());
            }
            else {
                holder.managerRemarksLayout.setVisibility(View.GONE);
            }


            holder.img_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickItem(position);
                }
            });

        } catch (Exception e){

        }

            return convertView;
        }

    private class ViewHolder {
        FRTTextviewTrebuchetMS issue_type_name, managerRemarksTextView, patrolleRemarksTv, assignment_date;
        ImageView img_map;
        LinearLayout patrollerRemarksLayout,managerRemarksLayout;
    }

    public interface SelectionViewAttendanceListener {
        void onClickItem(int position);
    }
}
