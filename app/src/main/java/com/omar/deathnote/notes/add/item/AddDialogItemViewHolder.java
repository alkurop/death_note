package com.omar.deathnote.notes.add.item;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

import com.omar.deathnote.R;

public class AddDialogItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.itemTitle)
    TextView itemTitle;

    private IAddDialogItemDataHolder dataHolder;


    public AddDialogItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


    @OnClick(R.id.itemTitle)
    void doAction() {
        dataHolder.doAction();
    }

    public void onBindView(IAddDialogItemDataHolder dataHolder) {
        itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        itemTitle.setText(dataHolder.getTitle());
        this.dataHolder = dataHolder;
    }

}
