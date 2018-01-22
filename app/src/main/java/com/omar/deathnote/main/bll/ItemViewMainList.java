package com.omar.deathnote.main.bll;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.omar.deathnote.R;
import com.omar.deathnote.models.ItemMainList;

/**
 * Created by omar on 8/29/15.
 */
public class ItemViewMainList extends RecyclerView.ViewHolder {

    @BindView(R.id.itemTitle)
    TextView itemTitle;
    @BindView(R.id.itemDate)
    TextView itemDate;
    @BindView(R.id.itemImg)
    ImageView itemImg;

    private ItemMainList dataHolder;

    private IMainAdapterCallback callback;


    public ItemViewMainList(View view) {
        super(view);
        ButterKnife.bind(this,view);
    }

    public void bindData(ItemMainList dataHolder, IMainAdapterCallback callback) {
        this.callback = callback;
        this.dataHolder = dataHolder;
        itemTitle.setText(dataHolder.title);
        itemDate.setText(dataHolder.timedate);
        itemImg.setImageResource(dataHolder.img);
    }

    @OnClick(R.id.del)
    public void delClicked() {
        callback.deleteItem(dataHolder.id);
    }

    @OnClick(R.id.container)
    public void itemClicked() {
        callback.openNote(dataHolder.id);
    }


}
