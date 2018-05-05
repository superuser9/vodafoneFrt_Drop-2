package com.vodafone.frt.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.FRTMgrFeedbackActivity;
import com.vodafone.frt.models.MGRResponseFeedback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/15/2018.
 */

public class MGRFeedbackAdapter extends BaseAdapter implements Comparable<MGRResponseFeedback> {
    private Context context;
    private FRTMgrFeedbackActivity listener;
    private List<MGRResponseFeedback> dataSet;

    public MGRFeedbackAdapter(Context context){
        this.context=context;
    }
    public void setSelectionListener(FRTMgrFeedbackActivity listener) {
        this.listener = listener;
    }

    public void setDataSet(List<MGRResponseFeedback> dataSet) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        MGRResponseFeedback mgrResponseFeedback = dataSet.get(position);

        if (convertView == null){

            convertView = inflater.inflate(R.layout.mgr_feedback_adapter,parent,false);
            holder = new ViewHolder();
            holder.attendance_date = convertView.findViewById(R.id.attendance_date);
            holder.feedback_textView =   convertView.findViewById(R.id.feedback_textView);
            holder.userNameTextView = convertView.findViewById(R.id.userNameTextView);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mgrResponseFeedback.getCreated_on() != null && !mgrResponseFeedback.getCreated_on().equalsIgnoreCase("null")){
            holder.attendance_date.setText(mgrResponseFeedback.getCreated_on());
        }
        //holder.attendance_date.setText(!mgrResponseFeedback.getCreated_on().equalsIgnoreCase("null") && mgrResponseFeedback.getCreated_on() != null ? mgrResponseFeedback.getCreated_on() : "");
        if (mgrResponseFeedback.getFeedback_text() != null && !mgrResponseFeedback.getFeedback_text().equalsIgnoreCase("null")){
            holder.feedback_textView.setText(mgrResponseFeedback.getFeedback_text());
        }
        if (mgrResponseFeedback.getUser_name() != null && !mgrResponseFeedback.getUser_name().equalsIgnoreCase("null")){
            holder.userNameTextView.setText(mgrResponseFeedback.getUser_name());
        }


        return convertView;
    }

    @Override
    public int compareTo(@NonNull MGRResponseFeedback o) {
        return o.getCreated_on().compareTo(o.getCreated_on());
    }


    private class ViewHolder {
        TextView attendance_date, feedback_textView,
                userNameTextView;


    }
}
