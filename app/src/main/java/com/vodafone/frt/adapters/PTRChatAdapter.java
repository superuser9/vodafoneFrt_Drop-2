package com.vodafone.frt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.models.PTRMessageModel;

import java.util.List;

/**
 * Created by Ashutosh Kumar on 21-Jan-18.
 */


import android.widget.LinearLayout;

/**
 * Created by Ashutosh Kumar on 21-Jan-18.
 */


public class PTRChatAdapter extends BaseAdapter {
    private final Context context;
    private SelectionViewChatListener listener;
    private List<PTRMessageModel> dataSet;

    public PTRChatAdapter(Context context) {
        this.context = context;

    }

    public void setSelectionListener(SelectionViewChatListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<PTRMessageModel> dataSet) {
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
        PTRMessageModel messageModel = dataSet.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.rc_item_message_friend, parent, false);
            holder.ll_user = convertView.findViewById(R.id.ll_user);
            holder.ll_friend = convertView.findViewById(R.id.ll_friend);
            holder.textFriend = convertView.findViewById(R.id.textFriend);
            holder.friendtime = convertView.findViewById(R.id.friendtime);
            holder.textuser = convertView.findViewById(R.id.textuser);
            holder.userTime = convertView.findViewById(R.id.userTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!messageModel.getMessageType().isEmpty() && messageModel.getMessageType().equals("Sent")) {
            holder.ll_friend.setVisibility(View.GONE);
            holder.friendtime.setVisibility(View.GONE);
            holder.ll_user.setVisibility(View.VISIBLE);
            holder.userTime.setVisibility(View.VISIBLE);
            holder.userTime.setText(messageModel.getMessageTime());
            holder.textuser.setText(messageModel.getMessageText());
        }
        else if (!messageModel.getMessageType().isEmpty() && messageModel.getMessageType().equals("Recieved")) {
            holder.ll_friend.setVisibility(View.VISIBLE);
            holder.friendtime.setVisibility(View.VISIBLE);
            holder.ll_user.setVisibility(View.GONE);
            holder.userTime.setVisibility(View.GONE);
            holder.friendtime.setText(messageModel.getMessageTime());
            holder.textFriend.setText(messageModel.getMessageText());
        }

        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickItem(position);
            }
        });*/
        return convertView;
    }

    private class ViewHolder {
        TextView textFriend, textuser, friendtime, userTime;
        LinearLayout ll_user, ll_friend;
    }

    public interface SelectionViewChatListener {
        void onClickItem(int position);
    }
}