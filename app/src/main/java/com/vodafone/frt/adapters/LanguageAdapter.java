package com.vodafone.frt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vodafone.frt.R;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;

import java.util.List;

/**
 * Created by vishal on 25/2/18
 */

public class LanguageAdapter extends BaseAdapter {
    private final Context context;
    private List<String> dataSet;

    public LanguageAdapter(Context ctx) {
        context = ctx;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setDataSet(List<String> dataSet) {
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
        String language = dataSet.get(position);
        LanguageAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new LanguageAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_language, parent, false);
            holder.langname = convertView.findViewById(R.id.langname);
            convertView.setTag(holder);
        } else {
            holder = (LanguageAdapter.ViewHolder) convertView.getTag();
        }
        holder.langname.setText(language);
        return convertView;
    }

    private class ViewHolder {
        FRTTextviewTrebuchetMS langname;
    }

    public interface CQCallBackItemsAvailable {
        void available(boolean isavailable);
    }

}
