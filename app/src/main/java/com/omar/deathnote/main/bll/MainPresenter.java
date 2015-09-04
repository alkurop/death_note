package com.omar.deathnote.main.bll;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.omar.deathnote.App;
import com.omar.deathnote.db.providers.MainListProvider;
import com.omar.deathnote.main.ui.IMainView;
import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public class MainPresenter implements IMainEventHandler{
    private IMainView view;
    private RecyclerView rv_Main;
    private IMainAdapterCallback mainAdapterCallback;
     private MainListProvider.IMainListCallback mainListCallback;
    private AdapterMainList adapterMainList;


    private int style;


    @Override
    public void Init(IMainView _view) {
        style = 0;
        view = _view;
        setMainListProviderCallback();
        setAdapterCallback();
        getData(style);
    }

    @Override
    public void DisplayView() {
        setList();
    }

    @Override
    public void FabClicked() {
    }

    public void setList() {
        rv_Main = view.GetRecyclerView();
        rv_Main.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void setAdapterCallback(){
        mainAdapterCallback = new IMainAdapterCallback() {
            @Override
            public void DeleteItem(int id) {

            }

            @Override
            public void OpenNote(int id) {

            }
        };
    }


    public void getData(int style) {

        MainListProvider.I(view.GetLoaderManager()).  GetMainList(mainListCallback, style);
    }

    public void setMainListProviderCallback()
    {
        mainListCallback = new MainListProvider.IMainListCallback() {
            @Override
            public void onSuccess(List<ItemMainList> data) {

                    getData(data);

            }

            @Override
            public void onError(String error) {

            }
        };
    }

    public void setAdapter(List<ItemMainList> data){
        adapterMainList = new AdapterMainList(data,mainAdapterCallback);
         rv_Main.setAdapter(adapterMainList);
    }

    public void updateAdapter(List<ItemMainList> data){
        adapterMainList.SetDataList(data);
    }

    public void getData(List<ItemMainList> data){
        if (adapterMainList == null)
            setAdapter(data);
            else
        updateAdapter(data);
    }

 }
