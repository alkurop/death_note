package com.omar.deathnote.main.bll;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.omar.deathnote.App;
import com.omar.deathnote.db.providers.DeleteNoteProvider;
import com.omar.deathnote.db.providers.MainListProvider;
import com.omar.deathnote.main.ui.IMainView;
import com.omar.deathnote.models.ItemMainList;
import com.omar.deathnote.spinner.MySpinnerAdapter;

import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public class MainPresenter implements IMainEventHandler {
    private IMainView view;
    private RecyclerView rv_Main;
    private IMainAdapterCallback mainAdapterCallback;
    private MainListProvider.IMainListCallback mainListCallback;
    private AdapterMainList adapterMainList;
    private int style;

    @Override
    public void init(IMainView _view) {
        style = 0;
        view = _view;
        setMainListProviderCallback();
        setAdapterCallback();
        setList();
        setUpSpinner(0);
    }
    private void setUpSpinner(final int pos){
        view.setUpSpinner(pos, new MySpinnerAdapter.SpinnerCallback() {
            @Override
            public void onItemSelected(int position) {
                setData(position);
            }
        });
    }
    @Override
    public void fabClicked() {
        view.openEmptyNote();
    }

    @Override
    public void loadData() {
        setData(style);
    }

    public void setList() {
        rv_Main = view.getRecyclerView();
        rv_Main.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void setAdapterCallback() {
        mainAdapterCallback = new IMainAdapterCallback() {
            @Override
            public void deleteItem(int id) {
                DeleteNoteProvider.I(view.getSupportLoaderManager()).DeleteNote(id, new DeleteNoteProvider.IDeleteNoteCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }

            @Override
            public void openNote(int id) {
                view.openNote(id);
            }
        };
    }

    private void setData(int style) {

        MainListProvider.I(view.getSupportLoaderManager()).GetMainList(mainListCallback, style);
    }

    private void setMainListProviderCallback() {
        mainListCallback = new MainListProvider.IMainListCallback() {
            @Override
            public void onSuccess(List<ItemMainList> data) {

                setData(data);

            }

            @Override
            public void onError(String error) {

            }
        };
    }

    private void setAdapter(List<ItemMainList> data) {
        adapterMainList = new AdapterMainList(data, mainAdapterCallback);
        rv_Main.setAdapter(adapterMainList);
    }

    private void updateAdapter(List<ItemMainList> data) {
        adapterMainList.setDataList(data);
    }

    private void setData(List<ItemMainList> data) {
        if (adapterMainList == null) setAdapter(data);
        else updateAdapter(data);
    }

}
