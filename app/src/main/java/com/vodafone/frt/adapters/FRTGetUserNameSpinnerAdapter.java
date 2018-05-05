package com.vodafone.frt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.FRTResponseSubOrdinateModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/30/2018.
 */

public class FRTGetUserNameSpinnerAdapter extends BaseAdapter{

    private Context mContext;
    int checkCounter = 0;
    public ArrayList<Integer> issueId_list = new ArrayList<>();

    private List<FRTResponseSubOrdinateModel> modelList;
    private int id;

    public FRTGetUserNameSpinnerAdapter(Context context , List<FRTResponseSubOrdinateModel> models){
        mContext = context;
        modelList = models;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        FRTResponseSubOrdinateModel frtResponseSubOrdinateModel = modelList.get(position);
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
            holder.userNameTextView = convertView.findViewById(R.id.userNameTextView);
            holder.frtIdTextView = convertView.findViewById(R.id.frtIdTextView);
             id  = frtResponseSubOrdinateModel.getFrt_user_id();
            convertView.setTag(holder);
        }
        else {
           holder = (ViewHolder)convertView.getTag();
        }

/*
        if (position==0){
            holder.frtIdTextView.setText(0+"");
            holder.userNameTextView.setText(R.string.selectFRt);
            position++;
        }
        else {*/
            holder.frtIdTextView.setText(id +"");
            holder.userNameTextView.setText(frtResponseSubOrdinateModel.getUser_name());
      // }
        return convertView;
    }



    private class ViewHolder {
        FRTTextviewTrebuchetMS userNameTextView,frtIdTextView;
    }
}
