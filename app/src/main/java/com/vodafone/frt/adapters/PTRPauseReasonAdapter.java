package com.vodafone.frt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vodafone.frt.R;
import com.vodafone.frt.fonts.FRTRadioButtonTrebuchetMS;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRResponsePauseReasonModel;

import java.util.List;

/**
 * Created by vishal on 12/12/17
 */
public class PTRPauseReasonAdapter extends BaseAdapter {
    private final Context context;
    private List<PTRResponsePauseReasonModel> dataSet;
    private int selectedPosition = -1;
    private FRTCallbackListItemChecked frtCallbackListItemChecked;
    private ViewHolder holder;
    public PTRPauseReasonAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    public void setDataSet(List<PTRResponsePauseReasonModel> dataSet) {
        this.dataSet = dataSet;
    }


    public void setItemCallBack(FRTCallbackListItemChecked frtCallbackListItemChecked1) {
        frtCallbackListItemChecked = frtCallbackListItemChecked1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PTRResponsePauseReasonModel reason = dataSet.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_reason_list, parent, false);
            holder.reasonTextView = convertView.findViewById(R.id.reasonTextView);
            holder.reasonTextView1 = convertView.findViewById(R.id.reasonTextView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.reasonTextView.setChecked(position == selectedPosition);
        holder.reasonTextView.setTag(position);
        holder.reasonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChecked(view);
                frtCallbackListItemChecked.onSelect(true, position);
            }
        });
        holder.reasonTextView.setText(reason.getDescription());
        return convertView;
    }

    private void setChecked(View v) {
        selectedPosition = (Integer) v.getTag();
        notifyDataSetChanged();
    }

    public void setSelected() {
        holder.reasonTextView.setChecked(false);
    }

    private class ViewHolder {
        FRTRadioButtonTrebuchetMS reasonTextView;
        FRTTextviewTrebuchetMS reasonTextView1;
    }

    public interface FRTCallbackListItemChecked {
        void onSelect(boolean flag, int position);
    }
}