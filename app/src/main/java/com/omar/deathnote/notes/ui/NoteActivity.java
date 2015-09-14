package com.omar.deathnote.notes.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.models.SpinnerItem;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.spinner.MySpinnerAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements INoteView {

    @InjectView(R.id.noteList)
    LinearLayout ll_main;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.spinner)
    Spinner spinner;
    @InjectView(R.id.scrollView1)
    ScrollView scrollView;

    private INoteEventHandler presenter;
    private Target bgTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.inject(this);
        presenter = App.getNotePresenter();
        presenter.setView(this);
        bgTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                scrollView.setBackground(new BitmapDrawable(bitmap) );
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };
        if (savedInstanceState == null) presenter.getContentId(getIntent());
        else presenter.displayView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.saveContent();
    }

    @Override
    public void displayFragment(Content item, Fragment fragment) {
        FrameLayout container = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.content_container, null, false);
        container.setId(item.getUID());
        ll_main.addView(container);
        getSupportFragmentManager().beginTransaction().add(item.getUID(), fragment).commit();
    }


    @Override
    public void removeFragment(Content item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(item.getUID());
        View view = ll_main.findViewById(item.getUID());

        if (fragment != null) getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (view != null) ll_main.removeView(view);
    }

    @Override
    public void clearList(List<Content> contentList) {
        for (Content item : contentList) {
            removeFragment(item);
        }
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        presenter.saveContent();
        switch (item.getItemId()) {

            case R.id.action_share:
                presenter.shareClicked();
                break;

            case R.id.save:
                presenter.saveClicked();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUpSpinner(int pos,final MySpinnerAdapter.SpinnerCallback spinnerCallback) {
        List<SpinnerItem> spinnerItemList = new ArrayList<>();

        for(int i =1 ; i <   Constants.select_images. length; i++){
            spinnerItemList.add(new SpinnerItem(Constants.select_images[i],getString(Constants.select_names[i])));
        }

        MySpinnerAdapter spinnerAdatper = new MySpinnerAdapter(spinnerItemList );

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

    @Override
    public void setBackGround(int index) {
        Picasso.with(this).load(Constants.note_bg_images[index]).into(bgTarget);
    }
}
