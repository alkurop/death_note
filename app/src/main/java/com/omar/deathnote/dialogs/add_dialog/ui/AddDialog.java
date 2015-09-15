package com.omar.deathnote.dialogs.add_dialog.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.omar.deathnote.R;
import com.omar.deathnote.dialogs.add_dialog.bll.IAddDialogPresenter;

/**
 * Created by omar on 9/14/15.
 */
public class AddDialog extends AppCompatDialogFragment implements IAddDialogView{

    @InjectView(R.id.tv_addDialog_title)
    TextView tvTitle;

    @InjectView(R.id.lv_addDialog_list)
    ListView lvList;

    @OnItemClick(R.id.lv_addDialog_list)
      void listItemClicked(int position){
            presenter.listItemClicked(position);
    }

    private IAddDialogPresenter presenter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add, container,false);
        ButterKnife.inject(this,v);
        return v;
    }

    @Override
    public void setTitleLabel(String label) {
        tvTitle.setText(label);
    }

    @Override
    public ListView getListView() {
        return lvList;
    }

    @Override
    public void setEventPresenter(IAddDialogPresenter presenter) {
        this.presenter = presenter;
    }
}
