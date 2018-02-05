package com.omar.deathnote.main;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omar.deathnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MySpinnerAdapter extends BaseAdapter {

    private final List<SpinnerItem> spinnerItemList;

    public MySpinnerAdapter(List<SpinnerItem> spinnerItemList) {
        this.spinnerItemList = spinnerItemList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select, parent, false);
        SpinnerViewHolder holder = new SpinnerViewHolder(convertView);
        holder.setIcon(spinnerItemList.get(position).getImageId());
        holder.setText(spinnerItemList.get(position).getName());
        return convertView;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select, parent, false);
        SpinnerViewHolder holder = new SpinnerViewHolder(convertView);
        holder.setIcon(spinnerItemList.get(position).getImageId());
        holder.setText(spinnerItemList.get(position).getName());
        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    @Override
    public int getCount() {
        return spinnerItemList.size();
    }

    @Override
    public SpinnerItem getItem(int position) {
        return spinnerItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class SpinnerViewHolder {
        private final ImageView icon;
        private final TextView name;

        private SpinnerViewHolder(View parent) {
            icon = parent.findViewById(R.id.itemImg);
            name = parent.findViewById(R.id.itemName);
        }

        public void setText(String text) {
            name.setText(text);
        }

        void setIcon(int iconID) {
            Picasso.with(icon.getContext()).load(iconID).into(icon);
        }
    }
}
