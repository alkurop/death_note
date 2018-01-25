package com.omar.deathnote.notes.add.bll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omar.deathnote.R;
import com.omar.deathnote.notes.add.item.AddDialogItemViewHolder;
import com.omar.deathnote.notes.add.item.IAddDialogItemDataHolder;

import java.util.List;

public class AddDialogAdaper extends RecyclerView.Adapter<AddDialogItemViewHolder> {

    private final List<IAddDialogItemDataHolder> items;

    AddDialogAdaper(List<IAddDialogItemDataHolder> items) {
        this.items = items;
    }

    @Override
    public AddDialogItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_dialog_item, viewGroup, false);
        return new AddDialogItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddDialogItemViewHolder viewHolder, int i) {
        viewHolder.onBindView(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
