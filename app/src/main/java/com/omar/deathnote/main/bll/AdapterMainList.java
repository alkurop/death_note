package com.omar.deathnote.main.bll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.omar.deathnote.R;
import com.omar.deathnote.main.ui.ItemViewMainList;
import com.omar.deathnote.models.ItemMainList;

import java.util.List;

/**
 * Created by omar on 8/29/15.
 */
public class AdapterMainList extends RecyclerView.Adapter<ItemViewMainList> {

    private List<ItemMainList> dataList;
    private IMainAdapterCallback callback;

    public AdapterMainList(List<ItemMainList> dataList, IMainAdapterCallback callback){
        this.dataList = dataList;
        this.callback = callback;

    }

    public void setDataList(List<ItemMainList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewMainList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new ItemViewMainList(view);
    }

    @Override
    public void onBindViewHolder(ItemViewMainList holder, int position) {
            holder.bindData(dataList.get(position), callback);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
