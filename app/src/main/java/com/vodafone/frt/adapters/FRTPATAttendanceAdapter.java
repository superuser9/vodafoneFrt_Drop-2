package com.vodafone.frt.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.fragments.AcceptLeaveStatusFragment;
import com.vodafone.frt.fragments.RejectedLeaveStatusFragment;
import com.vodafone.frt.models.MGRResponseAttendanceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/20/2018.
 */

public class FRTPATAttendanceAdapter extends BaseAdapter {


    private final Context context;
    private SelectionViewAttendanceListener listener;
    private List<MGRResponseAttendanceModel> dataSet;

    public FRTPATAttendanceAdapter(Context context){
        this.context=context;
    }


    public void setSelectionListener(SelectionViewAttendanceListener listener) {
        this.listener = listener;
    }

    public void setDataSet(List<MGRResponseAttendanceModel> dataSet) {
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
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        View row = convertView;


        MGRResponseAttendanceModel atendance = dataSet.get(position);
        row = null;
        if (convertView== null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_frt_patroller_attendance_list, parent, false);
            holder.attendance_date = convertView.findViewById(R.id.attendance_date);
            holder.userNameTextView = convertView.findViewById(R.id.userNameTextView);
            holder.fromDateTextView = convertView.findViewById(R.id.fromDateTextView);
            holder.toDateTextView = convertView.findViewById(R.id.toDateTextView);
            holder.leaveReasonTextView = convertView.findViewById(R.id.leaveReasonTextView);
            holder.managerRemarksTextView = convertView.findViewById(R.id.managerRemarksTextView);
            holder.approveOnTextView = convertView.findViewById(R.id.approveOnTextView);
            holder.approvedOnTextViewFixed = convertView.findViewById(R.id.approvedOnTextViewFixed);
            holder.leaveStatusTextView = convertView.findViewById(R.id.leaveStatusTextView);
            holder.LayoutapproveOn = convertView.findViewById(R.id.LayoutapproveOn);
            holder.LayoutManagerRemarks = convertView.findViewById(R.id.LayoutManagerRemarks);
           // holder.img_rejectLeave = convertView.findViewById(R.id.img_rejectLeave);
           // holder.img_acceptLeave = convertView.findViewById(R.id.img_acceptLeave);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }



        if (atendance.getUser_name() != null && !atendance.getUser_name().equals("null")){
            holder.userNameTextView.setText(atendance.getUser_name());
        }

        if (atendance.getCreated_on() != null && !atendance.getCreated_on().equals("null")){
            holder.attendance_date.setText(atendance.getCreated_on());
        }
        if (atendance.getManager_remark() != null && !atendance.getManager_remark().equals("null")){
           holder.LayoutManagerRemarks.setVisibility(View.VISIBLE);
            holder.managerRemarksTextView.setText(atendance.getManager_remark());
        }
        else {
            holder.LayoutManagerRemarks.setVisibility(View.GONE);
        }
        if (atendance.getFrom_date() != null && !atendance.getFrom_date().equals("null")){
            holder.fromDateTextView.setText(atendance.getFrom_date());
        }
        if (atendance.getTo_date() != null && !atendance.getTo_date().equals("null")){
            holder.toDateTextView.setText(atendance.getTo_date());
        }
        if (atendance.getLeave_reason() != null && !atendance.getLeave_reason().equals("null")){
            holder.leaveReasonTextView.setText(atendance.getLeave_reason());
        }
        if (atendance.getLeave_status() != null && !atendance.getLeave_status().equals("null")){
            holder.leaveStatusTextView.setText(atendance.getLeave_status());
        }
        /*if (dataSet.get(position).getApproved_on() != null && !dataSet.get(position).getApproved_on().equals("null")){
            holder.approveOnTextView.setText(dataSet.get(position).getApproved_on());
        }*/

        if (atendance.getLeave_status()!= null && atendance.getLeave_status().equalsIgnoreCase("Rejected")){
            holder.approvedOnTextViewFixed.setText(R.string.rejectedOn);
            holder.approveOnTextView.setText(atendance.getApproved_on());
            holder.LayoutapproveOn.setVisibility(View.VISIBLE);

        }
        else if (atendance.getLeave_status() != null && atendance.getLeave_status().equalsIgnoreCase("Approved"))
        {
            holder.approvedOnTextViewFixed.setText(R.string.approvedon);
            holder.approveOnTextView.setText(atendance.getApproved_on());

        }
        else{
            holder.LayoutapproveOn.setVisibility(View.GONE);
        }

        /*if (atendance.getApproved_on() != null && atendance.getApproved_on().equalsIgnoreCase("null")){
            //holder.approveOnTextView.setText(atendance.getApproved_on());
            holder.LayoutapproveOn.setVisibility(View.VISIBLE);
        }*/
        /*else {
            holder.LayoutapproveOn.setVisibility(View.GONE);
        }*/


        holder.approvedOnTextViewFixed.setTag(position);
        holder.leaveStatusTextView.setTag(position);
        holder.approveOnTextView.setTag(position);
        //if (atendance.g)
        return convertView;
    }



    private class ViewHolder {
        TextView attendance_date, userNameTextView, fromDateTextView, toDateTextView,leaveReasonTextView,leaveStatusTextView,
                managerRemarksTextView,approveOnTextView,approvedOnTextViewFixed;
        LinearLayout LayoutapproveOn,LayoutManagerRemarks;
       // ImageView img_rejectLeave,img_acceptLeave;
    }

    public interface SelectionViewAttendanceListener {
        void onClickItem(int position);
    }
}
