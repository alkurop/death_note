package com.omar.deathnote.spinner;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.omar.deathnote.R;
import com.omar.deathnote.models.SpinnerItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by omar on 9/10/15.
 */
public class MySpinnerAdapter implements SpinnerAdapter {
    public interface SpinnerCallback{
        void onItemSelected(int position);
    }
    private List<SpinnerItem> spinnerItemList;

    public MySpinnerAdapter(List<SpinnerItem> spinnerItemList ) {
        this.spinnerItemList = spinnerItemList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select, parent, false);

            SpinnerViewHolder holder = new SpinnerViewHolder(convertView);
            holder.setIcon(spinnerItemList.get(position).getImageId());
            holder.setText(spinnerItemList.get(position).getName());
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select, parent, false);
        SpinnerViewHolder holder = new SpinnerViewHolder(convertView);
        holder.setIcon(spinnerItemList.get(position).getImageId());
        holder.setText(spinnerItemList.get(position).getName());
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

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

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }



    class SpinnerViewHolder {
        private ImageView icon;
        private TextView name;

        public SpinnerViewHolder(View parent) {
            icon = (ImageView)parent.findViewById(R.id.itemImg);
            name = (TextView) parent.findViewById(R.id.itemName);
        }

        public void setText(String text){
                name.setText(text);
        }
        void setIcon(int iconID){
            Picasso.with(icon.getContext()).load(iconID).into(icon);
        }
    }
}
