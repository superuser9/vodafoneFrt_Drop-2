package com.vodafone.frt.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.MGRTaskAssignActivity;
import com.vodafone.frt.fragments.RejectedTaskIssueFragment;
import com.vodafone.frt.models.MGRResponseRouteIssueDetailsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/29/2018.
 */

public class MGRTaskAssignAdapter extends BaseAdapter {

    DataTransferInterface dtInterface;
    SparseBooleanArray mCheckStates;
    private Context mContext;
   // int checkCounter = 0;
    public ArrayList<Integer> issueId_list = new ArrayList<>();

    private List<MGRResponseRouteIssueDetailsModel> modelList;
    public MGRTaskAssignAdapter(Context context, List<MGRResponseRouteIssueDetailsModel> models,DataTransferInterface dtInterface){
        mContext = context;
        modelList = models;
        this.dtInterface = dtInterface;
        //checkCounter = 0;
        mCheckStates = new SparseBooleanArray(models.size());
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
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

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;
        MGRResponseRouteIssueDetailsModel responseRouteIssueDetailsModel = modelList.get(position);
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.mgr_task_assign_adapter, parent, false);
            holder.patrollerNameTextView = convertView.findViewById(R.id.patrollerNameTextView);
            holder.issueTextView = convertView.findViewById(R.id.issueTextView);
            holder.patrolleRemarksTextView = convertView.findViewById(R.id.patrolleRemarksTextView);
            holder.managerRemarksTextView = convertView.findViewById(R.id.managerRemarksTextView);
            holder.checkBoxTask = convertView.findViewById(R.id.checkBoxTask);
            holder.createdOnTextView = convertView.findViewById(R.id.createdOnTextView);
            holder.modifiedOnTextView=convertView.findViewById(R.id.modifiedOnTextView);
            holder.statusTextView= convertView.findViewById(R.id.statusTextView);
            holder.routeNameTextView = convertView.findViewById(R.id.routeNameTextView);
            holder.img_cancelTask = convertView.findViewById(R.id.img_cancelTask);
            holder.img_cancelTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  int getPosition = (Integer) v.getTag();

                    int issueIDButton  = modelList.get(position).getIssue_id();
                            ((DataTransferInterface) mContext).setIssueId(issueIDButton);

                    //if (issue_id_list.size() >0) {
                        DialogFragment newFragment = RejectedTaskIssueFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putInt("ISSUE_ID", issueIDButton);
                        newFragment.setArguments(bundle);
                      newFragment.show(((Activity) mContext).getFragmentManager(), "dialog");


                }
            });
            //holder.checkBoxTask.setTag(position);
           // holder.checkBoxTask.setChecked(modelList.get(position).isSelected());
            holder.checkBoxTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    int issueIDCheckbox  = modelList.get(position).getIssue_id();
                    modelList.get(getPosition).setSelected(buttonView.isChecked());
                   /* if (holder.checkBoxTask.isChecked() && checkCounter >3){
                        holder.checkBoxTask.setChecked(false);
                       // Toast.makeText(mContext, R.string.max3Issue, Toast.LENGTH_SHORT).show();
                        checkCounter--;
                    }*/
                   // else {
                        MGRResponseRouteIssueDetailsModel p = modelList.get(position);
                        p.setSelected(holder.checkBoxTask.isChecked());
                        if (holder.checkBoxTask.isChecked()){
                            //modelList.get(position).setSelected(true);
                            if (mContext instanceof MGRTaskAssignActivity){
                                if (!issueId_list.contains(modelList.get(position).getIssue_id())) {
                                    issueId_list.add(modelList.get(position).getIssue_id());

                                    ((DataTransferInterface) mContext).setValues(issueId_list);
                                    Log.d(this.getClass().getName(), "ADD_LIST" + issueId_list);
                                    holder.checkBoxTask.setChecked(modelList.get(position).isSelected());
                                }
                            }
                            //checkCounter++;
                        }
                        else {
                                   issueId_list.remove(issueId_list.indexOf(issueIDCheckbox));
                                    ((DataTransferInterface) mContext).setValues(issueId_list);
                                    Log.d(this.getClass().getName(),"REMOVE_LIST" + issueId_list);
                               holder.checkBoxTask.setChecked(modelList.get(position).isSelected());
                           //checkCounter--;
                        }
                       // checkCounter--;

                   // }
                }
            });

            convertView.setTag(holder);
            convertView.setTag(R.id.issueTextView,holder.issueTextView);
            convertView.setTag(R.id.checkBoxTask, holder.checkBoxTask);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }




        if (!responseRouteIssueDetailsModel.getPatroller_user_name().equalsIgnoreCase("null")){
            holder.patrollerNameTextView.setText(responseRouteIssueDetailsModel.getPatroller_user_name());
        }

        if (!responseRouteIssueDetailsModel.getPatroller_remark().equalsIgnoreCase("null")){
            holder.patrolleRemarksTextView.setText(responseRouteIssueDetailsModel.getPatroller_remark());
        }

        if (!responseRouteIssueDetailsModel.getManager_remark().equalsIgnoreCase("null")){
            holder.managerRemarksTextView.setText(responseRouteIssueDetailsModel.getManager_remark());
        }

        if (!responseRouteIssueDetailsModel.getCreated_on().equalsIgnoreCase("null")){
            holder.createdOnTextView.setText(responseRouteIssueDetailsModel.getCreated_on());
        }
        if (!responseRouteIssueDetailsModel.getModified_on().equalsIgnoreCase("null")){
            holder.modifiedOnTextView.setText(responseRouteIssueDetailsModel.getModified_on());
        }
        if (!responseRouteIssueDetailsModel.getStatus().equalsIgnoreCase("null")){
            holder.statusTextView.setText(responseRouteIssueDetailsModel.getStatus());
        }
        if (!responseRouteIssueDetailsModel.getRoute_name().equalsIgnoreCase("null")) {
            holder.routeNameTextView.setText(responseRouteIssueDetailsModel.getRoute_name());
        }


        holder.checkBoxTask.setTag(position);
        holder.issueTextView.setText(responseRouteIssueDetailsModel.getIssue_desc());
        holder.checkBoxTask.setChecked(modelList.get(position).isSelected());
        holder.img_cancelTask.setTag(position);

        return convertView;
    }


    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }

    private class ViewHolder {
        CheckBox checkBoxTask;
        TextView issueTextView, patrollerNameTextView, patrolleRemarksTextView,routeNameTextView,createdOnTextView,
                modifiedOnTextView, statusTextView,managerRemarksTextView;
        ImageView img_cancelTask;
    }

    public interface DataTransferInterface {
         void setValues(ArrayList<Integer> issueId_list);
         void setIssueId(int issueId);
    }

}
