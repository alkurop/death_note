package com.omar.deathnote.main.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.main.bll.IMainEventHandler;
import com.omar.deathnote.models.SpinnerItem;
import com.omar.deathnote.notes.ui.NoteActivity;
import com.omar.deathnote.spinner.MySpinnerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IMainView {

    @InjectView(R.id.rv_main)
    RecyclerView rv_MainList;
    @InjectView(R.id.fl_background)
    FrameLayout fl_Background;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.spinner)
    Spinner spinner;

    private IMainEventHandler presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        presenter = App.getMainPresenter();
        presenter.init(this);
        presenter.loadData();
    }

    @OnClick(R.id.fab)
    public void FabClicked() {
        presenter.fabClicked();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rv_MainList;
    }

    @Override
    public void openEmptyNote() {
        startActivity(new Intent(this, NoteActivity.class));
    }

    @Override
    public void openNote(int id) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(Constants.ID, id);
        startActivity(intent);
    }

    @Override
    public void setUpSpinner(int pos, final MySpinnerAdapter.SpinnerCallback spinnerCallback) {
        List<SpinnerItem> spinnerItemList = new ArrayList<>();

        for (int i = 0; i < Constants.select_images.length; i++) {
            spinnerItemList.add(new SpinnerItem(Constants.select_images[i], getString(Constants.select_names[i])));
        }
        MySpinnerAdapter spinnerAdatper = new MySpinnerAdapter(spinnerItemList);
        spinner.setAdapter(spinnerAdatper);
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCallback.onItemSelected(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
