package com.omar.deathnote.main.v2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alkurop.database.Note;
import com.omar.deathnote.R;

import java.util.ArrayList;
import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter<NoteViewHolder> {
    private List<Note> dataList;
    private final MainAdapterCallback callback;

    MainListAdapter(MainAdapterCallback callback) {
        this.dataList = new ArrayList<>(0);
        this.callback = callback;
    }

    void setDataList(List<Note> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        holder.bindData(dataList.get(position), callback);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
