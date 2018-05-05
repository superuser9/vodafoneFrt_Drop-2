package com.vodafone.frt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vodafone.frt.R;
import com.vodafone.frt.models.FRTResponseSubOrdinateModel;

import java.util.List;

/**
 * Created by Ajay Tiwari on 4/13/2018.
 */

public class FRTPTRUserListAdapter extends BaseAdapter {
    private final Context context;
    private SelectionViewAttendanceListener listener;
    private List<FRTResponseSubOrdinateModel> dataSet;

    public FRTPTRUserListAdapter(Context context) {
        this.context = context;

    }

    public void setSelectionListener(SelectionViewAttendanceListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<FRTResponseSubOrdinateModel> dataSet) {
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
        FRTResponseSubOrdinateModel frtResponseSubOrdinateModel = dataSet.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_frt_ptr_userlist, parent, false);
            holder.userName_textView = convertView.findViewById(R.id.userName_textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName_textView.setText(frtResponseSubOrdinateModel.getUser_name());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickItem(position);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView userName_textView;
        /*ImageView img_map;
        LinearLayout working_days_layout;*/
    }

    public interface SelectionViewAttendanceListener {
        void onClickItem(int position);
    }
}
