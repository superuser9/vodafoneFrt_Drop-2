package com.vodafone.frt.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.vodafone.frt.R;
import com.vodafone.frt.activities.PTRMyCheckInActivity;
import com.vodafone.frt.models.PTRResponseSelfCheckInModel;

import java.util.List;

/**
 * Created by Ajay Tiwari on 2/26/2018.
 */

public class FRTCheckInAdapter  extends BaseAdapter {

    private final Context context;
    private PTRMyCheckInActivity listener;
    private List<PTRResponseSelfCheckInModel> dataSet;


    public FRTCheckInAdapter (Context context){
        this.context= context;
    }


    public void setSelectionListener(PTRMyCheckInActivity listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<PTRResponseSelfCheckInModel> dataSet) {
        this.dataSet = dataSet;
    }


 /*   public void addItem(final String item) {
        dataSet.add(item);
        notifyDataSetChanged();
    }*/

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        PTRResponseSelfCheckInModel checkInModel = dataSet.get(position);

        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_selfcheckin_list, parent, false);
            holder = new ViewHolder();
            holder.attendance_date = convertView.findViewById(R.id.attendance_date);
            holder.routeNameTextView = convertView.findViewById(R.id.routeNameTextView);
            holder.plannedTimeTextView = convertView.findViewById(R.id.plannedTimeTextView);
            holder.plannedEndTime = convertView.findViewById(R.id.plannedEndTime);
            holder.statusTextView = convertView.findViewById(R.id.statusTextView);
            holder.approvedOnTextView = convertView.findViewById(R.id.approvedOnTextView);
            holder.approvedOnTextViewFixed= convertView.findViewById(R.id.approvedOnTextViewFixed);
            holder.remarksTextView = convertView.findViewById(R.id.remarksTextView);
            holder.managerRemarksTextView = convertView.findViewById(R.id.managerRemarksTextView);
            holder.LayoutManagerRemarks= convertView.findViewById(R.id.LayoutManagerRemarks);
            holder.approvedOnLinearLayout = convertView.findViewById(R.id.approvedOnLinearLayout);
            convertView.setTag(holder);
            
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.attendance_date.setText(!checkInModel.getCreated_on().equalsIgnoreCase("null") && checkInModel.getCreated_on() != null ? checkInModel.getCreated_on() : "");

       // holder.routeNameTextView.setText(!checkInModel.getRoute_name().equalsIgnoreCase("null") && checkInModel.getRoute_name() != null ? checkInModel.getRoute_name() : "");
      //  holder.remarksTextView.setText(checkInModel.getRemarks());

        if (checkInModel.getRoute_name() != null && !checkInModel.getRoute_name().equalsIgnoreCase("null")  ){
            holder.routeNameTextView.setText(checkInModel.getRoute_name());
        }
         if (checkInModel.getRemarks() != null && !checkInModel.getRemarks().equalsIgnoreCase("null")   ){
            holder.remarksTextView.setText(checkInModel.getRemarks());
        }

        if (checkInModel.getStatus() != null && checkInModel.getStatus().equalsIgnoreCase("Rejected")){
            holder.approvedOnLinearLayout.setVisibility(View.VISIBLE);
            holder.approvedOnTextViewFixed.setText(R.string.rejectedOn);
            holder.approvedOnTextView.setText(checkInModel.getApproved_on());

        }
         else if (checkInModel.getStatus() != null && checkInModel.getStatus().equalsIgnoreCase("Approved")){
            holder.approvedOnLinearLayout.setVisibility(View.VISIBLE);
            holder.approvedOnTextViewFixed.setText(R.string.approvedon);
            holder.approvedOnTextView.setText(checkInModel.getApproved_on());

        }
         else if(checkInModel.getStatus() != null && checkInModel.getStatus().equalsIgnoreCase("Pending"))
        {
            holder.approvedOnLinearLayout.setVisibility(View.GONE);
        }

        if ((checkInModel.getPlanned_start_time().equalsIgnoreCase("null")
                && checkInModel.getPlanned_start_time() == null)
                || (checkInModel.getPlanned_end_time().equalsIgnoreCase("null")
                && checkInModel.getPlanned_end_time() == null)) {
            holder.plannedTimeTextView.setText("");
        } else {
            holder.plannedTimeTextView.setText(checkInModel.getPlanned_start_time());
            holder.plannedEndTime.setText(checkInModel.getPlanned_end_time());
        }

         if (checkInModel.getStatus() != null && checkInModel.getStatus().equalsIgnoreCase("Rejected")){
            holder.statusTextView.setText(checkInModel.getStatus());
             holder.statusTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorred));

        }
         else if (checkInModel.getStatus() != null && checkInModel.getStatus().equalsIgnoreCase("Approved")){
             holder.statusTextView.setText(checkInModel.getStatus());
             holder.statusTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorgreen));
         }
          else if (checkInModel.getStatus() != null && checkInModel.getStatus().equalsIgnoreCase("Pending")){
             holder.statusTextView.setText(checkInModel.getStatus());
             holder.statusTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorpurple));
         }

        if (!checkInModel.getManager_remarks().isEmpty()){
            holder.LayoutManagerRemarks.setVisibility(View.VISIBLE);
            holder.managerRemarksTextView.setText(checkInModel.getManager_remarks());
        }
        else
        {
            holder.LayoutManagerRemarks.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView attendance_date, routeNameTextView,
                plannedTimeTextView, plannedEndTime,
                statusTextView,approvedOnTextView,
                remarksTextView,
                managerRemarksTextView,approvedOnTextViewFixed;
        LinearLayout LayoutManagerRemarks,approvedOnLinearLayout;

    }

    public interface SelectionViewCheckInListener {
        void onClickItem(int position);
    }
}
