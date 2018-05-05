package com.vodafone.frt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.vodafone.frt.R;
import com.vodafone.frt.models.UserDataModel;

import java.util.List;

public class UserListAdapter extends BaseAdapter {
    private final Context context;
    private SelectionViewChatListener listener;
    private List<UserDataModel> dataSet;

    public UserListAdapter(Context context, List<UserDataModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;

    }


    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<UserDataModel> dataSet) {
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
        UserDataModel messageModel = dataSet.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_item, parent, false);
            holder.tv_username = convertView.findViewById(R.id.tv_username);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.tv_username.setText(messageModel.getUser_name());
        convertView.setTag(R.integer.data,messageModel);
        return convertView;
    }

    private class ViewHolder {
        TextView tv_username;
    }

    public interface SelectionViewChatListener {
        void onClickItem(int position);
    }
}