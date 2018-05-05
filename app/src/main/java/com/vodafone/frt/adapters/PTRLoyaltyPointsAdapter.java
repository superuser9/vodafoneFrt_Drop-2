package com.vodafone.frt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.LoyalityPointsActivity;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRResponseLoyaltyStatus;

import java.util.List;

/**
 * Created by Ajay Tiwari on 3/15/2018.
 */

public class PTRLoyaltyPointsAdapter extends BaseAdapter {

    private  Context context;
    private LoyalityPointsActivity listener;
    private List<PTRResponseLoyaltyStatus> dataSet;

    public PTRLoyaltyPointsAdapter(Context context){
        this.context=context;
    }
    public void setSelectionListener(LoyalityPointsActivity listener) {
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setDataSet(List<PTRResponseLoyaltyStatus> dataSet) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        PTRResponseLoyaltyStatus loyaltyStatus = dataSet.get(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.loyalty_points_adapter, parent, false);
            holder = new ViewHolder();
           // holder.attendance_date = convertView.findViewById(R.id.attendance_date);
            holder.rewardsPointsTextView = convertView.findViewById(R.id.rewardsPointsTextView);
            holder.LoyaltyStatusView = convertView.findViewById(R.id.LoyaltyStatusView);
            holder.userNameTextView = convertView.findViewById(R.id.userNameTextView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
       // holder.attendance_date.setText(!loyaltyStatus.getCreated_on().equalsIgnoreCase("null") && loyaltyStatus.getCreated_on() != null ? loyaltyStatus.getCreated_on() : "");
        if (loyaltyStatus.getLoyalty_status() != null && !loyaltyStatus.getLoyalty_status().equalsIgnoreCase("null")){
            holder.LoyaltyStatusView.setText( loyaltyStatus.getLoyalty_status());
        }
        if (loyaltyStatus.getReward_points() != null && !loyaltyStatus.getReward_points().equalsIgnoreCase("null")){
            holder.rewardsPointsTextView.setText(/*"Rewards Points" + "\n" +*/loyaltyStatus.getReward_points());
        }
        if (loyaltyStatus.getUser_name() != null && !loyaltyStatus.getUser_name().equalsIgnoreCase("null")){
            holder.userNameTextView.setText(loyaltyStatus.getUser_name());
        }


        return convertView;
    }

    private class ViewHolder {
        FRTTextviewTrebuchetMS attendance_date, rewardsPointsTextView, LoyaltyStatusView,
                userNameTextView;


    }
}
