package com.omar.deathnote.dialogs.add_dialog.item;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.omar.deathnote.R;

/**
 * Created by omar on 9/15/15.
 */
public class AddDialogItemViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.itemTitle)
    TextView itemTitle;

    private IAddDialogItemDataHolder dataHolder;


    public AddDialogItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }


    @OnClick(R.id.itemTitle)
    public void doAction() {
        dataHolder.doAction();
    }

    public void onBindView(IAddDialogItemDataHolder dataHolder){
        itemTitle.setPaintFlags(itemTitle.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        itemTitle.setText(dataHolder.getTitle());
        this.dataHolder = dataHolder;
    }

}
