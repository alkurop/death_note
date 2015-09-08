package com.omar.deathnote.notes.item.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.omar.deathnote.R;
import com.omar.deathnote.notes.item.bll.base.IContentEventHandler;

/**
 * Created by omar on 9/8/15.
 */
abstract public class BaseItemFragment extends Fragment implements IContentView{


    private  IContentEventHandler eventHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(GetLayout(), container, false);
        ButterKnife.inject(this, v);
        eventHandler.DisplayView();
        return v;
    }

    @Override
    public void SetEventHandler(IContentEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }



    @OnClick(R.id.del)
    public void OnDeleteClicked() {
        eventHandler.Delete();
    }


}
