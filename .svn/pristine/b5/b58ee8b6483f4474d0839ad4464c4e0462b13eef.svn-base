package com.vodafone.frt.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.vodafone.frt.R;
import com.vodafone.frt.models.BaseMessageResp;
import com.vodafone.frt.utility.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @lepton
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ChatRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<BaseMessageResp> mMessageList;
    private String userChatId, senderChatId;



    public ChatRecyclerAdapter(Context context, String userChatId, String senderChatId) {
        mMessageList = new ArrayList<>();
        mContext = context;
        this.userChatId = userChatId;
        this.senderChatId = senderChatId;
    }



    public void setMessageList(List<BaseMessageResp> messages) {
        mMessageList = messages;
        notifyDataSetChanged();
    }

    public void addFirst(BaseMessageResp message) {
        if(message == null)return;
        mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    public void addLast(BaseMessageResp message) {
        if(message == null)return;
        mMessageList.add(message);
        notifyDataSetChanged();
    }

    public void delete(String msgId) {
        if(mMessageList == null)return;
        for(BaseMessageResp msg : mMessageList) {
            if(msg.getId().equals(msgId)) {
                mMessageList.remove(msg);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void update(BaseMessageResp message) {
        BaseMessageResp baseMessage;
        for (int index = 0; index < mMessageList.size(); index++) {
            baseMessage = mMessageList.get(index);
            if(message.getId().equals(baseMessage.getId())) {
                mMessageList.remove(index);
                mMessageList.add(index, message);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i("Naresh","ViewType: "+viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_blue, parent, false);
        return new UserMessageHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessageResp message = mMessageList.get(position);
        boolean isNewDay = false;
        if (position < mMessageList.size() - 1) {
            BaseMessageResp prevMessage = mMessageList.get(position + 1);
            if (!DateUtils.hasSameDate(DateUtils.getDateInMillisecond(message.getMessage_time()), DateUtils.getDateInMillisecond(prevMessage.getMessage_time()))) {
                isNewDay = true;
            }
        } else if (position == mMessageList.size() - 1) {
            isNewDay = true;
        }
        ((UserMessageHolder) holder).bind(mContext,holder.getItemViewType(), message, isNewDay, position);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private class UserMessageHolder extends RecyclerView.ViewHolder {
        UserMessageHolder(View itemView) {
            super(itemView);
        }
        void bind(final Context context, int type, final BaseMessageResp message, boolean isNewDay, final int postion) {
            TextView tv_msg = (TextView) itemView.findViewById(R.id.tv_msg);
            TextView tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            TextView tv_date = (TextView)itemView.findViewById(R.id.tv_date);
            LinearLayout ll_chat_bg = (LinearLayout)itemView.findViewById(R.id.ll_chat_bg);

            int drawableId = message.getSender_id().equals(userChatId) ? R.drawable.chat_bubble_blue : R.drawable.chat_bubble_white;
            ll_chat_bg.setBackgroundResource(drawableId);

            if (isNewDay) {
                tv_date.setVisibility(View.VISIBLE);
                tv_date.setText(DateUtils.getDateInMillisecond(message.getMessage_time()));
            } else {
                tv_date.setVisibility(View.GONE);
            }
            tv_msg.setText(message.getMessage_content());
            tv_time.setText(DateUtils.getTime(message.getMessage_time()));
            //tv_time.setText(message.getMessage_time());
        }
    }


}
