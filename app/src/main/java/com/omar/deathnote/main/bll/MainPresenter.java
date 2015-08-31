package com.omar.deathnote.main.bll;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.db.providers.MainListProvider;
import com.omar.deathnote.loaders.DeleteSomeNoteLoader;
import com.omar.deathnote.loaders.MainListLoader;
import com.omar.deathnote.main.ui.IMainView;
import java.util.List;

import static com.omar.deathnote.Constants.*;

/**
 * Created by omar on 8/27/15.
 */
public class MainPresenter implements IMainEventHandler{
    private IMainView view;
    private RecyclerView rv_Main;
    private LoaderManager loaderManager;
    private Context context;
    private AdapterMainList adapterMainList;
    private IMainAdapterCallback mainAdapterCallback;
    private int style;


    @Override
    public void Init(IMainView _view) {
        style = 0;
        view = _view;
        loaderManager = view.GetLoaderManager();
        context = App.getContext();
        GetData(style);
    }


    @Override
    public void DisplayView() {
        SetList();
    }

    @Override
    public void FabClicked() {
    }

    @Override
    public void SetList() {
        rv_Main = view.GetRecyclerView();
        rv_Main.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void GetData(int style) {

        MainListProvider.I(view.GetLoaderManager()).  GetMainList(new MainListProvider.IMainListCallback() {
            @Override
            public void onSuccess(List<ItemMainList> data) {
                SetData(data);
            }

            @Override
            public void onError(String error) {

            }
        }, style);
    }




    public void setAdapter(List<ItemMainList> data){
        adapterMainList = new AdapterMainList(data,mainAdapterCallback);
         rv_Main.setAdapter(adapterMainList);
    }
    public void updateAdapter(List<ItemMainList> data){
        adapterMainList.SetDataList(data);
    }

    public void SetData(List<ItemMainList> data){
        if (adapterMainList == null)
            setAdapter(data);
            else
        updateAdapter(data);
    }

 }
