package com.omar.deathnote.main.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.omar.deathnote.R;
import com.omar.deathnote.main.bll.IMainAdapterCallback;
import com.omar.deathnote.models.ItemMainList;

/**
 * Created by omar on 8/29/15.
 */
public class ItemViewMainList extends RecyclerView.ViewHolder {

    @InjectView(R.id.itemTitle)
    TextView itemTitle;
    @InjectView(R.id.itemDate)
    TextView itemDate;
    @InjectView(R.id.itemImg)
    ImageView itemImg;



    private ItemMainList dataHolder;

    private IMainAdapterCallback callback;


    public ItemViewMainList(View view) {
        super(view);
        ButterKnife.inject(this,view);
    }

    public void BindData( ItemMainList dataHolder, IMainAdapterCallback callback) {
        this.callback = callback;
        this.dataHolder = dataHolder;
        itemTitle.setText(dataHolder.title);
        itemDate.setText(dataHolder.timedate);
        itemImg.setImageResource(dataHolder.img);
    }

    @OnClick(R.id.del)
    public void delClicked() {
        callback.DeleteItem(dataHolder.id);
    }

    @OnClick(R.id.container)
    public void itemClicked() {
        callback.OpenNote(dataHolder.id);
    }


}