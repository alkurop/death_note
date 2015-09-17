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
import com.omar.deathnote.notes.item.bll.IContentEventHandler;

/**
 * Created by omar on 9/8/15.
 */
abstract public class BaseItemFragment extends Fragment implements IContentView{


    private  IContentEventHandler eventHandler;
    private boolean isRequestsFocus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        ButterKnife.inject(this, v);
        eventHandler.displayView();
        return v;
    }

    @Override
    public void setEventHandler(IContentEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }



    @OnClick(R.id.del)
    public void onDeleteClicked() {
        eventHandler.delete();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(isRequestsFocus){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestFocus();
                        }
                    });
                }
            }).start();

            isRequestsFocus = false;
        }
    }

    @Override
    public void shouldRequestFocus() {
        isRequestsFocus = true;
    }
}
