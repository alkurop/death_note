package com.omar.deathnote.main.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.omar.deathnote.App;
import com.omar.deathnote.R;
import com.omar.deathnote.main.bll.IMainEventHandler;
import com.omar.deathnote.notes.ui.NoteActivity;


public class MainActivity extends AppCompatActivity implements IMainView {


    @InjectView(R.id.rv_main)
             RecyclerView rv_MainList;
    @InjectView(R.id.fl_background)
            FrameLayout fl_Background;
    @InjectView(R.id.toolbar)
            Toolbar toolbar;

    private IMainEventHandler presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        presenter = App.getMainPresenter();
        presenter.Init(this);
        presenter.DisplayView();
    }

    @OnClick(R.id.fab)
    public void FabClicked() {
        presenter.FabClicked();
    }

    @Override
    public RecyclerView GetRecyclerView() {
        return rv_MainList;
    }

    @Override
    public android.support.v4.app.LoaderManager GetLoaderManager() {
       return getSupportLoaderManager();
    }

    @Override
    public void OpenEmptyNote(){

        startActivity(new Intent(this,NoteActivity.class));
        App.getNotePresenter().CreateEmptyContent();
    }

}
