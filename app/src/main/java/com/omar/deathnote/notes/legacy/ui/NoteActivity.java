package com.omar.deathnote.notes.legacy.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.main.MySpinnerAdapter;
import com.omar.deathnote.notes.ContentType;
import com.omar.deathnote.models.SpinnerItem;
import com.omar.deathnote.notes.legacy.bll.INoteEventHandler;
import com.omar.deathnote.notes.legacy.item.bll.IContentEventHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity
        extends AppCompatActivity
        implements INoteView, IScrollCallback {

    @BindView(R.id.noteList)
    LinearLayout ll_main;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.iv_BackGround)
    ImageView iv_Background;
    @BindView(R.id.scrollView1)
    ScrollView scrollView1;

    private INoteEventHandler presenter;

    @OnClick(R.id.toolbar)
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.fab)
    public void fabCliked() {
        hideKeyboard();
        presenter.fabClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
        presenter = App.getNotePresenter();
        presenter.setView(this);
        scrollView1.setSmoothScrollingEnabled(true);

        if (savedInstanceState == null) presenter.getContentId(getIntent());
        else presenter.displayView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.saveContent();
    }

    @Override
    public void displayFragment(IContentEventHandler eventHandler, Fragment fragment ) {
        FrameLayout container = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.content_container, null, false);
        //container.setId(eventHandler.getContent().getUID());
        ll_main.addView(container);
       // getSupportFragmentManager().beginTransaction().add(eventHandler.getContent().getUID(), fragment).commit();
    }


    @Override
    public void removeFragment(ContentType item) {
       // Fragment fragment = getSupportFragmentManager().findFragmentById(item.getUID());
       // View view = ll_main.findViewById(item.getUID());

       // if (fragment != null) getSupportFragmentManager().beginTransaction().remove(fragment).commit();
       // if (view != null) ll_main.removeView(view);
    }

    @Override
    public void clearList(List<ContentType> contentList) {
        for (ContentType item : contentList) {
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
    public void setUpSpinner(int pos, final MySpinnerAdapter.SpinnerCallback spinnerCallback) {
        List<SpinnerItem> spinnerItemList = new ArrayList<>();
        for (int i = 1; i < Constants.select_images.length; i++) {
            spinnerItemList.add(new SpinnerItem(Constants.select_images[i], getString(Constants.select_names[i])));
        }

        MySpinnerAdapter spinnerAdatper = new MySpinnerAdapter(spinnerItemList);
        spinner.setAdapter(spinnerAdatper);
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCallback.onItemSelected(i);
                hideKeyboard();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void setBackGround(int index) {
        Picasso.with(this).load(Constants.note_bg_images[index]).into(iv_Background);
    }

    @Override
    public void scrollToBottom() {
        int h1 = ll_main.getHeight();
        scrollView1.smoothScrollTo(0,h1 );
    }
}
