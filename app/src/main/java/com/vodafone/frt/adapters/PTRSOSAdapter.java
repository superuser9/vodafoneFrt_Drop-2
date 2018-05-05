package com.vodafone.frt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vodafone.frt.R;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRResponseSOSModel;

import java.util.List;


public class PTRSOSAdapter extends BaseAdapter {
    private final Context context;
    private CQCallBackItemsAvailable cqCallBackItemsAvailable;
    private List<PTRResponseSOSModel> dataSet;

    public PTRSOSAdapter(Context ctx) {
        context = ctx;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setItemCallBackItemAvailable(CQCallBackItemsAvailable callBackItemAvailable) {
        cqCallBackItemsAvailable = callBackItemAvailable;
    }

    public void setDataSet(List<PTRResponseSOSModel> dataSet) {
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
        PTRResponseSOSModel frtResponseSOSModel = dataSet.get(position);
        if (dataSet.size() == 0)
            cqCallBackItemsAvailable.available(false);
        else {
            cqCallBackItemsAvailable.available(true);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_sos, parent, false);
                holder.sostitle = convertView.findViewById(R.id.sos_desc);
                holder.sosnumber = convertView.findViewById(R.id.phone);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.sosnumber.setText("Contact No: " + frtResponseSOSModel.getPhone());
            holder.sostitle.setText(frtResponseSOSModel.getSos_desc());
        }
        return convertView;
    }

    private class ViewHolder {
        FRTTextviewTrebuchetMS sosnumber, sostitle;
    }

    public interface CQCallBackItemsAvailable {
        void available(boolean isavailable);
    }

}