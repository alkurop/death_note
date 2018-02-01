package com.omar.deathnote.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

import com.omar.deathnote.Constants;
import com.omar.deathnote.R;

class NoteViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.itemTitle)
    TextView itemTitle;
    @BindView(R.id.itemDate)
    TextView itemDate;
    @BindView(R.id.itemImg)
    ImageView itemImg;

    private MainAdapterCallback callback;
    private NoteViewModel note;


    NoteViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    void bindData(NoteViewModel note, MainAdapterCallback callback) {
        this.callback = callback;
        this.note = note;
        itemTitle.setText(note.getTitle());
        itemDate.setText(note.getTimedate());
        itemImg.setImageResource(Constants.getStyleImage(note.getStyle()));
    }

    @OnClick(R.id.del)
    void delClicked() {
        callback.deleteItem(note.getId());
    }

    @OnClick(R.id.container)
    void itemClicked() {
        callback.openNote(note.getId());
    }
}
