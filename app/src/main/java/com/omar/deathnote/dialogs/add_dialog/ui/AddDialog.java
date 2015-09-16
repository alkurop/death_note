package com.omar.deathnote.dialogs.add_dialog.ui;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.omar.deathnote.R;
import com.omar.deathnote.dialogs.add_dialog.bll.IAddDialogPresenter;


/**
 * Created by omar on 9/14/15.
 */
public class AddDialog extends DialogFragment implements IAddDialogView{

    @InjectView(R.id.tv_addDialog_title)
    TextView tvTitle;

    @InjectView(R.id.lv_addDialog_list)
    RecyclerView lvList;

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
        lvList.setLayoutManager(new LinearLayoutManager(getContext()));
        getDialog().setCanceledOnTouchOutside(true);
        tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return v;
    }

    @Override
    public void setTitleLabel(String label) {
        tvTitle.setText(label);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return lvList;
    }

    @Override
    public void setEventHandler(IAddDialogPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.displayView();
    }
}
